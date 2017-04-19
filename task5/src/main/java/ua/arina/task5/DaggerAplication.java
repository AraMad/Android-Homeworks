package ua.arina.task5;

import android.app.Application;

import ua.arina.task5.interfaces.AppComponent;
import ua.arina.task5.interfaces.DaggerAppComponent;
import ua.arina.task5.daggermoduls.RetrofitModule;

/**
 * Created by Arina on 19.04.2017
 */

public class DaggerAplication extends Application{
    private static AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        appComponent =
                DaggerAppComponent
                        .builder()
                        .retrofitModule(new RetrofitModule())
                        .build();
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
