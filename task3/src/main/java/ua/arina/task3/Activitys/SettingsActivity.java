package ua.arina.task3.Activitys;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ua.arina.task3.R;
import ua.arina.task3.Services.MessageService;
import ua.arina.task3.Settings.Constants;

public class SettingsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private SharedPreferences settings;
    private Button stopServiceButton;
    private Button changeTextButton;
    private EditText usersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);

        if (DEBUG) {
            Log.d(TAG, "onCreate: second activity");
        }

        settings = getSharedPreferences(Constants.FILE_PREFERENSES, Context.MODE_PRIVATE);

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(0);

        usersText = (EditText) findViewById(R.id.users_text);

        changeTextButton = (Button) findViewById(R.id.change_text_button);
        changeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (usersText.getText() != null){
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString(Constants.TEXT_SETTINGS_KEY, usersText.getText().toString());
                    editor.apply();
                    stopService(new Intent(SettingsActivity.this, MessageService.class));
                    MessageService.setAlarm(getApplicationContext(), true);
                }
            }
        });

        stopServiceButton = (Button) findViewById(R.id.stop_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageService.setAlarm(getApplicationContext(), false);
                stopService(new Intent(SettingsActivity.this, MessageService.class));
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.SERVICE_STATE_KEY, false);
                editor.apply();
                stopServiceButton.setEnabled(false);
                changeTextButton.setEnabled(false);
            }
        });
    }
}
