package com.example.anunda_user.testndk;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;

import com.example.anunda_user.testndk.serialport_api.Application;
import com.example.anunda_user.testndk.serialport_api.SerialPortFinder;

/**
 * Created by Anunda_USER on 2/8/2561.
 */

public class SerialPortPerferences extends PreferenceActivity {

    private Application mApplication;
    private SerialPortFinder mSerialPortFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApplication = new Application();
        mSerialPortFinder = mApplication.mSerialPortFinder;
        addPreferencesFromResource(R.xml.serial_port_preferences);

        //Devices
        final ListPreference devices = (ListPreference) findPreference("DEVICE");
        String[] arrayOfString1 = mSerialPortFinder.getAllDevices();
        String[] arrayOfString2 = mSerialPortFinder.getAllDevicesPath();
        devices.setEntries(arrayOfString1);
        devices.setEntryValues(arrayOfString2);
        devices.setSummary(devices.getValue());
        devices.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject) {
                paramAnonymousPreference.setSummary((String) paramAnonymousObject);
                return true;
            }
        });

        //Baud rates
        final ListPreference baudrate = (ListPreference) findPreference("BAUDRATE");
        baudrate.setSummary(baudrate.getValue());
        baudrate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            public boolean onPreferenceChange(Preference paramAnonymousPreference, Object paramAnonymousObject) {
                paramAnonymousPreference.setSummary((String) paramAnonymousObject);
                return true;
            }
        });
    }
}
