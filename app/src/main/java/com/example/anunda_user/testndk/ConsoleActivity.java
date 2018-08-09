package com.example.anunda_user.testndk;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;

import com.example.anunda_user.testndk.serialport_api.SerialPortActivity;

import java.io.IOException;

public class ConsoleActivity extends SerialPortActivity {

    TextView tvReception;
    EditText edEmission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);

        tvReception = findViewById(R.id.tvRec);
        edEmission = findViewById(R.id.etEmis);

        edEmission.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionID, KeyEvent keyEvent) {
                CharSequence t = textView.getText();
                char[] text = new char[t.length()];
                for (int i = 0; i < t.length(); i++) {
                    text[i] = t.charAt(i);
                }

                try {
                    mOutputStream.write(new String(text).getBytes());
                    mOutputStream.write('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        });
    }

    @Override
    protected void onDataReceived(final byte[] buffer, final int size) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //show data sended
                if (tvReception != null) {
                    tvReception.append(new String(buffer, 0, size));
                }
            }
        });
    }
}
