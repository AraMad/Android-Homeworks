package ua.arina.task3.Activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import ua.arina.task3.R;
import ua.arina.task3.Services.MessageService;
import ua.arina.task3.Settings.Constants;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private Button startServiceButton;
    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settings = getSharedPreferences(Constants.FILE_PREFERENSES, Context.MODE_PRIVATE);

        startServiceButton = (Button) findViewById(R.id.start_service_button);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessageService.setAlarm(getApplicationContext(), true);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(Constants.SERVICE_STATE_KEY, true);
                editor.apply();
                startServiceButton.setEnabled(false);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "onDestroy activity");
        }
    }
}
