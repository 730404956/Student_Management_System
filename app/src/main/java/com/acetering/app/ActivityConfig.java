package com.acetering.app;

import android.app.Activity;
import android.os.Bundle;

import com.acetering.app.util.AppConfig;
import com.acetering.app.views.DialogFactory;

import androidx.preference.PreferenceFragmentCompat;


public class ActivityConfig extends PreferenceFragmentCompat {
    String TAG = "Settings";

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
        findPreference("sound_add_record").setOnPreferenceClickListener(preference -> {
            DialogFactory.createSoundRecorderDialog(getContext(), "add.mp3").show();
            return false;
        });
        findPreference("sound_delete_record").setOnPreferenceClickListener(preference -> {
            DialogFactory.createSoundRecorderDialog(getContext(), "delete.mp3").show();
            return false;
        });
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