package ua.arina.task5.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;

import ua.arina.task5.R;
import ua.arina.task5.models.ItemModel;

/**
 * Created by Arina on 06.04.2017
 */

public class ChooseCity extends AppCompatActivity {

    private ArrayList<ItemModel> citys;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_choose_city);

        getSupportActionBar().setTitle(getResources().getString(R.string.menu_change_city_title));
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getIntent()
        //      .getIntExtra(Constants.COLOR_KEY, Constants.DEFAULT_COLOR)));

        /*getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(Constants.CITY_NAME_KEY, name)
                .apply();*/
    }
}
