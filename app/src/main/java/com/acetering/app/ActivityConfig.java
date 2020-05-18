package com.acetering.app;

import android.app.Activity;
import android.os.Bundle;

import com.acetering.app.util.AppConfig;

import androidx.preference.PreferenceFragmentCompat;


public class ActivityConfig extends PreferenceFragmentCompat {
    String TAG = "Settings";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((Activity) getContext()).setTitle(R.string.title_activity_config);
    }

    @Override
    public void onPause() {
        super.onPause();
        AppConfig.applyConfig(getContext());
        AppConfig.changeAccountPreference(getContext());
    }
}