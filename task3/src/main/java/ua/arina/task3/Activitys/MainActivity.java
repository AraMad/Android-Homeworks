package ua.arina.task3.activitys;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import ua.arina.task3.R;
import ua.arina.task3.services.MessageService;
import ua.arina.task3.settings.Constants;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    private EditText usersText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Constants.DEBUG) {
            Log.d(TAG, "onCreate");
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

        ((Switch)findViewById(R.id.switch_service_state)).setOnCheckedChangeListener(
                (CompoundButton buttonView, boolean isChecked)->{
                    if(isChecked){
                        startService(new Intent(this, MessageService.class));
                    } else {
                        stopService(new Intent(this, MessageService.class));
                    }

                    getSharedPreferences(Constants.FILE_PREFERENCES, Context.MODE_PRIVATE).edit()
                            .putBoolean(Constants.SWITCH_STATE_KEY, isChecked)
                            .apply();
                });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        ((Switch)findViewById(R.id.switch_service_state))
                .setChecked(
                        getSharedPreferences(Constants.FILE_PREFERENCES,
                                Context.MODE_PRIVATE).getBoolean(Constants.SWITCH_STATE_KEY, true));
    }
}
