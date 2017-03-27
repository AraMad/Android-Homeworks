package ua.arina.task3.activitys;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ua.arina.task3.fragments.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }
}
