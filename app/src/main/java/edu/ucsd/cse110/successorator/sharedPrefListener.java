package edu.ucsd.cse110.successorator;

import android.content.SharedPreferences;

public class sharedPrefListener implements SharedPreferences.OnSharedPreferenceChangeListener{
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("mode")) {
            String newValue = sharedPreferences.getString(key, "Tod ");


        }
    }
}
