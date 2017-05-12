package ua.arina.task5;

import android.app.Application;

import ua.arina.task5.daggermoduls.MessageDisplayerModule;
import ua.arina.task5.daggermoduls.PreferencesModule;
import ua.arina.task5.interfaces.AppComponent;
import ua.arina.task5.daggermoduls.RetrofitModule;
import ua.arina.task5.interfaces.DaggerAppComponent;

/**
 * Created by Arina on 19.04.2017
 */

public class WeatherApplication extends Application{
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent =
                DaggerAppComponent
                        .builder()
                        .retrofitModule(new RetrofitModule())
                        .preferencesModule(new PreferencesModule(getApplicationContext()))
                        .messageDisplayerModule(new MessageDisplayerModule())
                        .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
