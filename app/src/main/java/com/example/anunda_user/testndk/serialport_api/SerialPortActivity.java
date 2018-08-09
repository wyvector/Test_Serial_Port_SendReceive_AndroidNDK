package com.example.anunda_user.testndk.serialport_api;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.anunda_user.testndk.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;

/**
 * Created by Anunda_USER on 3/8/2561.
 */

public abstract class SerialPortActivity extends AppCompatActivity {

    private final String TAG = "Anunda";
    protected Application mApplication;
    protected SerialPort mSerialPort;
    protected OutputStream mOutputStream;
    protected InputStream mInputStream;
    private ReadThread mReadThread;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = new Application();
        try{
            mSerialPort = mApplication.getSerialPort(this);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
            mReadThread = new ReadThread();
            mReadThread.start();
        }catch (SecurityException e){
            DisplayError(R.string.error_security);
            Log.e(TAG,"error Security");
        }catch (IOException e){
            DisplayError(R.string.error_unknown);
            Log.e(TAG,"error IOException");
        }catch (InvalidParameterException e){
            DisplayError(R.string.error_configuration);
            Log.e(TAG,"error Configuration");
        }
    }

    private class ReadThread extends Thread{

        private ReadThread() {
        }

        @Override
        public void run() {
            super.run();

            while (!isInterrupted()){
                int size;
                try{
                    byte[] buffer = new byte[64];
                    if(mInputStream == null)break;
                    size = mInputStream.read(buffer);
                    if(size > 0){
                        onDataReceived(buffer,size);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void DisplayError(int resourceID){
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Error");
        b.setMessage(resourceID);
        b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SerialPortActivity.this.finish();
            }
        });
        b.show();
    }

    protected abstract void onDataReceived(final byte[] buffer ,final int size);

    @Override
    protected void onDestroy() {
        if(mReadThread != null)
            mReadThread.interrupt();
        mApplication.closeSerialPort();
        mSerialPort = null;
        super.onDestroy();
    }
}
