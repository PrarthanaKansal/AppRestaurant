package com.example.prarthana.myapplication_restaurant;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

//added preference in gradle
public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settingspreferences);
    }
}
