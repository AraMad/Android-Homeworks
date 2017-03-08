package ua.arina.task3.activitys;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import ua.arina.task3.R;
import ua.arina.task3.settings.Constants;

public class ChangeMessageActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private static final boolean DEBUG = true;

    private EditText usersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_message);

        if (DEBUG) {
            Log.d(TAG, "onCreate: second activity");
        }

        usersText = (EditText) findViewById(R.id.users_text);

        findViewById(R.id.change_text_button).setOnClickListener(v ->
        {
            if (usersText.getText().toString().length() != 0){
                getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE).edit()
                        .putString(Constants.TEXT_SETTINGS_KEY, usersText.getText().toString())
                        .apply();
                Toast.makeText(getApplicationContext(), R.string.toast_change_text,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), R.string.toast_no_text,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


}
