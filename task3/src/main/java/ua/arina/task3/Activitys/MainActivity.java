package ua.arina.task3.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import ua.arina.task3.R;
import ua.arina.task3.Services.MessageService;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (DEBUG) {
            Log.d(TAG, "onCreate: MainActivity");
        }

         findViewById(R.id.start_service_button)
                .setOnClickListener(v -> startService(new Intent(this, MessageService.class)));

        findViewById(R.id.stop_service_button)
                .setOnClickListener(v -> stopService(new Intent(this, MessageService.class)));
    }
}
