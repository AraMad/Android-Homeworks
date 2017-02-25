package ua.arina.task3.Activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import ua.arina.task3.R;
import ua.arina.task3.Settings.Constants;

public class ChangeMessageActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private SharedPreferences settings;
    private EditText usersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);

        if (DEBUG) {
            Log.d(TAG, "onCreate: second activity");
        }

        usersText = (EditText) findViewById(R.id.users_text);

        settings = getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE);

        findViewById(R.id.change_text_button).setOnClickListener(v ->
        {
            if (usersText.getText().toString().length() != 0){
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.TEXT_SETTINGS_KEY, usersText.getText().toString())
                        .apply();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_no_text,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
