package ua.arina.task3.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import ua.arina.task3.services.MessageService;

public class BootReceiver extends BroadcastReceiver {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (DEBUG) {
            Log.d(TAG, "onReceive");
        }

        context.startService(new Intent(context, MessageService.class));
    }
}