package com.example.anunda_user.testndk.serialport_api;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by Anunda_USER on 2/8/2561.
 */

public class Application extends android.app.Application {

    private SerialPort mSerialPort = null;
    public SerialPortFinder mSerialPortFinder = new SerialPortFinder();


    public SerialPort getSerialPort(Context context) throws SecurityException, IOException, InvalidParameterException {
        if (mSerialPort == null) {
            //getSharedPreferences from android_serialport_api.sample_preferences
            SharedPreferences localSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            String strPath = localSharedPreferences.getString("DEVICE", "");
            int baudrate = Integer.decode(localSharedPreferences.getString("BAUDRATE", "-1"));
            if ((strPath.length() == 0) || (baudrate == -1)) {
                throw new InvalidParameterException();
            }
            /* Open the serial port */
            mSerialPort = new SerialPort(new File(strPath), baudrate, 0);
        }
        return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}