package ua.arina.task5.daggermoduls;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;

/**
 * Created by Arina on 20.04.2017
 */

@Module
public class PreferencesModule {

    Context context;

    public PreferencesModule(Context context) {
        this.context = context;
    }

    @Provides
    @Singleton
    SharedPreferences providesSharedPreferences(){
        return getDefaultSharedPreferences(context);
    }
}
