package ua.arina.task3.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import ua.arina.task3.R;
import ua.arina.task3.services.MessageService;
import ua.arina.task3.settings.Constants;

/**
 * Created by Arina on 17.03.2017
 */

public class SettingsFragment  extends PreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.settings, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen()
                .getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen()
                .getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if (Constants.DEBUG) {
            Log.d(TAG, "Fragment: onSharedPreferenceChanged");
        }

        if(key.equals(Constants.SERVICE_STATE_KEY)){

            if(sharedPreferences.getBoolean(Constants.SERVICE_STATE_KEY, true)){
                getActivity().startService(new Intent(getActivity(), MessageService.class));
            } else {
                getActivity().stopService(new Intent(getActivity(), MessageService.class));
            }

        } else if(key.equals(Constants.MESSAGE_TIME_INTERVAL_KEY)){
            getActivity().stopService(new Intent(getActivity(), MessageService.class));
            getActivity().startService(new Intent(getActivity(), MessageService.class));
        }
    }
}
