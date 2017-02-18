package ua.arina.task3.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import ua.arina.task3.Services.MessageService;
import ua.arina.task3.Settings.Constants;

public class BootReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private SharedPreferences settings;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (DEBUG) {
            Log.d(TAG, "onReceive");
        }

        settings = context.getSharedPreferences(Constants.FILE_PREFERENSES, Context.MODE_PRIVATE);
        if (settings.contains(Constants.SERVICE_STATE_KEY) &&
                settings.getBoolean(Constants.SERVICE_STATE_KEY, true)){
            MessageService.setAlarm(context, true);
        }
    }
}