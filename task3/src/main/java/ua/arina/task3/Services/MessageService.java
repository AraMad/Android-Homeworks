package ua.arina.task3.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import ua.arina.task3.activitys.MainActivity;
import ua.arina.task3.R;
import ua.arina.task3.settings.Constants;

public class MessageService extends BasicService {

    private final String TAG = getClass().getSimpleName();

    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Constants.DEBUG) {
            Log.d(TAG, "onCreate service");
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Constants.DEBUG) {
            Log.d(TAG, "onStartCommand");
        }

        if (timer != null){
            timer.cancel();
        }

        long startTime = Long.parseLong(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.MESSAGE_TIME_INTERVAL_KEY, "")) * 60 * 1000;

        timer = new Timer();
        timer.schedule(new OwnTimerTask(), startTime, startTime);

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (Constants.DEBUG) {
            Log.d(TAG, "onDestroy service");
        }

        if (timer != null){
            timer.cancel();
        }
    }

    private class OwnTimerTask extends TimerTask{

        @Override
        public void run() {

            Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString(Constants.MESSAGE_TEXT_KEY, ""))
                    .setContentIntent(
                            PendingIntent.getActivity(getApplicationContext(), 0, activityIntent,0))
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);

        }
    }
}
