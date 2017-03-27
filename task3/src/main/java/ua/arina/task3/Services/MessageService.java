package ua.arina.task3.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import ua.arina.task3.activitys.MainActivity;
import ua.arina.task3.R;
import ua.arina.task3.settings.Constants;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class MessageService extends BasicService {

    private final String TAG = getClass().getSimpleName();

    private final long RESTART_TIME_INTERVAL = 1000;
    private final long MINUTE_IN_MILLISECONDS = 60 * 1000;
    private final int REQUEST_CODE = 1;

    private Timer timer;

    @Override
    public void onCreate() {
        super.onCreate();

        if (Constants.DEBUG) {
            Log.d(TAG, "onCreate service");
        }

        getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putLong(Constants.LAST_START_TIME_KEY, System.currentTimeMillis())
                .apply();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Constants.DEBUG) {
            Log.d(TAG, "onStartCommand");
        }

        if (timer != null){
            timer.cancel();
        }

        long messageTimeInterval = Long.parseLong(
                getDefaultSharedPreferences(getApplicationContext())
                .getString(Constants.MESSAGE_TIME_INTERVAL_KEY, "")) * MINUTE_IN_MILLISECONDS;

        timer = new Timer();
        timer.schedule(new OwnTimerTask(), getStartTime(messageTimeInterval), messageTimeInterval);

        getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putLong(Constants.LAST_START_TIME_KEY, System.currentTimeMillis())
                .apply();

        return Service.START_STICKY;
    }

    private long getStartTime(long messageTimeInterval){

        long difference = System.currentTimeMillis() -
                getDefaultSharedPreferences(getApplicationContext())
                        .getLong(Constants.LAST_START_TIME_KEY, 0);

        return (difference > messageTimeInterval)?
                messageTimeInterval : messageTimeInterval - difference;
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

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        if (Constants.DEBUG) {
            Log.d(TAG, "Service: onTaskRemoved");
        }

        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), REQUEST_CODE,
                        new Intent(getApplicationContext(), this.getClass()),
                        PendingIntent.FLAG_ONE_SHOT);

        ((AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE))
                .set(AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + RESTART_TIME_INTERVAL,
                        pendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    private class OwnTimerTask extends TimerTask{

        @Override
        public void run() {

            Intent activityIntent = new Intent(getApplicationContext(), MainActivity.class);

            Notification notification = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getResources().getString(R.string.notification_title))
                    .setContentText(getDefaultSharedPreferences(getApplicationContext())
                            .getString(Constants.MESSAGE_TEXT_KEY, ""))
                    .setContentIntent(PendingIntent
                            .getActivity(getApplicationContext(), 0, activityIntent,0))
                    .setAutoCancel(true)
                    .build();

            NotificationManager notificationManager = (NotificationManager)
                    getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(Constants.NOTIFICATION_ID, notification);

        }
    }
}
