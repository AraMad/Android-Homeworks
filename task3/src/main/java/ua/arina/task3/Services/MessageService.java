package ua.arina.task3.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import ua.arina.task3.activitys.ChangeMessageActivity;
import ua.arina.task3.R;
import ua.arina.task3.settings.Constants;

public class MessageService extends Service {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private final long MESSAGE_TIME_INTERVAL = 1000 * 60;

    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEBUG) {
            Log.d(TAG, "onCreate service");
        }

        getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putLong(Constants.TIME_SETTINGS_KEY, System.currentTimeMillis())
                .apply();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (DEBUG) {
            Log.d(TAG, "onStartCommand");
        }

        if (timer != null){
            timer.cancel();
        }

        long start_time = MESSAGE_TIME_INTERVAL;
        long time_difference = System.currentTimeMillis() -
                getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE)
                .getLong(Constants.TIME_SETTINGS_KEY, MESSAGE_TIME_INTERVAL);

        if (time_difference < MESSAGE_TIME_INTERVAL){
            start_time = MESSAGE_TIME_INTERVAL - time_difference;
        }

        timer = new Timer();
        timer.schedule(new OwnTimerTask(), start_time, MESSAGE_TIME_INTERVAL);

        getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE).edit()
                .putLong(Constants.TIME_SETTINGS_KEY, System.currentTimeMillis())
                .apply();

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (DEBUG) {
            Log.d(TAG, "onDestroy service");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class OwnTimerTask extends TimerTask{

        @Override
        public void run() {

            Intent activityIntent = new Intent(getApplicationContext(), ChangeMessageActivity.class);

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE)
                            .getString(Constants.TEXT_SETTINGS_KEY,
                                    getResources().getString(R.string.notification_text)))
                    .setContentIntent(
                            PendingIntent.getActivity(getApplicationContext(),0, activityIntent,0))
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);

        }
    }
}
