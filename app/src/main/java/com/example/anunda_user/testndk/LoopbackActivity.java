package com.example.anunda_user.testndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.anunda_user.testndk.serialport_api.SerialPortActivity;

import java.io.IOException;

public class LoopbackActivity extends SerialPortActivity {

    byte mValueToSend;
    boolean mByteReceivedBack;
    Object mByteReceivedBackSemaphore = new Object();
    Integer mIncoming = new Integer(0);
    Integer mOutgoing = new Integer(0);
    Integer mLost = new Integer(0);
    Integer mCorrupted = new Integer(0);

    SendingThread mSendingThread;
    TextView mTextViewOutgoing;
    TextView mTextViewIncoming;
    TextView mTextViewLost;
    TextView mTextViewCorrupted;

    private class SendingThread extends Thread {
        @Override
        public void run() {
            while (!isInterrupted()) {
                synchronized (mByteReceivedBackSemaphore) {
                    mByteReceivedBack = false;
                    try {
                        if (mOutputStream != null) {
                            mOutputStream.write(mValueToSend);
                        } else {
                            return;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        return;
                    }
                    mOutgoing++;
                    // Wait for 100ms before sending next byte, or as soon as
                    // the sent byte has been read back.
                    try {
                        mByteReceivedBackSemaphore.wait(100);
                        if (mByteReceivedBack == true) {
                            // Byte has been received
                            mIncoming++;
                        } else {
                            // Timeout
                            mLost++;
                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mTextViewOutgoing.setText(mOutgoing.toString());
                                mTextViewLost.setText(mLost.toString());
                                mTextViewIncoming.setText(mIncoming.toString());
                                mTextViewCorrupted.setText(mCorrupted.toString());
                            }
                        });
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loopback);

        mTextViewOutgoing = findViewById(R.id.TextViewOutgoingValue);
        mTextViewIncoming = findViewById(R.id.TextViewIncomingValue);
        mTextViewLost = findViewById(R.id.textViewLostValue);
        mTextViewCorrupted = findViewById(R.id.textViewCorruptedValue);
        if (mSerialPort != null) {
            mSendingThread = new SendingThread();
            mSendingThread.start();
        }
    }

    @Override
    protected void onDataReceived(byte[] buffer, int size) {
        synchronized (mByteReceivedBackSemaphore) {
            int i;
            for (i = 0; i < size; i++) {
                if ((buffer[i] == mValueToSend) && (mByteReceivedBack == false)) {
                    mValueToSend++;
                    // This byte was expected
                    // Wake-up the sending thread
                    mByteReceivedBack = true;
                    mByteReceivedBackSemaphore.notify();
                } else {
                    // The byte was not expected
                    mCorrupted++;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if(mSendingThread != null)
            mSendingThread.interrupt();
        super.onDestroy();
    }
}
