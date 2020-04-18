package com.acetering.app;

import android.os.Bundle;

import com.acetering.app.util.AppConfig;
import com.acetering.student_input.R;

import androidx.preference.PreferenceFragmentCompat;


public class ActivityConfig extends PreferenceFragmentCompat {
    String TAG = "Settings";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppConfig.applyConfig(getContext());
        AppConfig.changeAccountPreference(getContext());
    }
}