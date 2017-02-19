package ua.arina.task3.Services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import ua.arina.task3.Activitys.ChangeMessageActivity;
import ua.arina.task3.R;
import ua.arina.task3.Settings.Constants;

public class MessageService extends Service {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private static final long MESSAGE_TIME_INTERVAL = 1000 * 1800;
    private static final int NOTIFICATION_ID = 0;

    private String notification_text;

    @Override
    public void onCreate() {
        super.onCreate();

        if (DEBUG) {
            Log.d(TAG, "onCreate service");
        }

        SharedPreferences settings;
        settings = getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE);

        if (settings.contains(Constants.TEXT_SETTINGS_KEY)){
            notification_text = settings.getString(Constants.TEXT_SETTINGS_KEY, null);
        } else {
            notification_text = getResources().getString(R.string.notification_text);
        }
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (DEBUG) {
            Log.d(TAG, "onStartCommand");
        }

        Intent activityIntent = new Intent(getApplicationContext(), ChangeMessageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, activityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(notification_text)
                .setContentIntent(pendingIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);

        return Service.START_STICKY;
    }

    public static void setAlarm(Context context, boolean is_on){
        Intent intent = new Intent(context, MessageService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (is_on){
            alarmManager.setRepeating(AlarmManager.RTC,
                    System.currentTimeMillis(), MESSAGE_TIME_INTERVAL, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
    }
}