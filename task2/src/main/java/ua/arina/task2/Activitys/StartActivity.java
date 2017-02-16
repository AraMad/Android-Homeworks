package ua.arina.task2.Activitys;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import ua.arina.task2.R;

public class StartActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private Button activityStartButton;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }

        textView = (TextView)findViewById(R.id.text_view_start_activity);
        textView.setText(TAG);

        activityStartButton = (Button)findViewById(R.id.activity_start_button);
        activityStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewActivityIntent = new Intent(StartActivity.this, SecondActivity.class);
                startActivity(startNewActivityIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (DEBUG) {
            Log.d(TAG, "onStart");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG) {
            Log.d(TAG, "onResume");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (DEBUG) {
            Log.d(TAG, "onPause");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (DEBUG) {
            Log.d(TAG, "onStop");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (DEBUG) {
            Log.d(TAG, "onRestart");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (DEBUG) {
            Log.d(TAG, "onDestroy");
        }
    }
}
