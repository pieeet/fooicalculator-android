package com.rocdev.android.piet.fooicalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Piet on 10-1-2015.
 */
public class InstellingenFragment extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private SharedPreferences prefs;
    private boolean onthoudProcent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        onthoudProcent = prefs.getBoolean("pref_bewaar_percentage", true);
        this.setDefaultProcentPreference(onthoudProcent);
        prefs.registerOnSharedPreferenceChangeListener(this);
    }

    private void setDefaultProcentPreference(boolean onthoudProcent) {
        Preference defaultProcent = findPreference("pref_default_fooi");
        if (onthoudProcent)  {
            defaultProcent.setEnabled(false);
        } else {
            defaultProcent.setEnabled(true);
        }
    }

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("pref_bewaar_percentage")) {
            onthoudProcent = prefs.getBoolean(key, true);
        }
        this.setDefaultProcentPreference(onthoudProcent);
    }


}

