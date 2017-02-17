package ua.arina.task3.Activitys;

import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ua.arina.task3.R;
import ua.arina.task3.Services.MessageService;

public class ChangeMessageActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    Button stopServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);

        if (DEBUG) {
            Log.d(TAG, "onCreate: second activity");
        }

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        stopServiceButton = (Button) findViewById(R.id.stop_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageService.setAlarm(getApplicationContext(), false);
                stopService(new Intent(ChangeMessageActivity.this, MessageService.class));
                stopServiceButton.setEnabled(false);
            }
        });
    }
}
