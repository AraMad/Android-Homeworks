package ua.arina.task2.Activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import ua.arina.task2.R;

/**
 * Created by Arina on 16.02.2017.
 */

public class SecondActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private static final boolean DEBUG = true;

    private TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        if (DEBUG) {
            Log.d(TAG, "onCreate");
        }

        textView = (TextView) findViewById(R.id.text_view_second_activity);
        textView.setText(TAG);
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
