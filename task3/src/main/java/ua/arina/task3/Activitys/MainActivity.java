package ua.arina.task3.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ua.arina.task3.R;
import ua.arina.task3.Services.MessageService;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private Button startServiceButton;
    private Button stopServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG) {
            Log.d(TAG, "onCreate: MainActivity");
        }

        startServiceButton = (Button) findViewById(R.id.start_service_button);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageService.setAlarm(getApplicationContext(), true);
            }
        });

        stopServiceButton = (Button) findViewById(R.id.stop_service_button);
        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageService.setAlarm(getApplicationContext(), false);
                stopService(new Intent(MainActivity.this, MessageService.class));
            }
        });
    }
}