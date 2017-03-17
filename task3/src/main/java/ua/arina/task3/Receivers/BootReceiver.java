package ua.arina.task3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import ua.arina.task3.services.MessageService;
import ua.arina.task3.settings.Constants;

public class BootReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Constants.DEBUG) {
            Log.d(TAG, "onReceive");
        }

        if (PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(Constants.SERVICE_STATE_KEY, true)){
            context.startService(new Intent(context, MessageService.class));
        }
    }
}