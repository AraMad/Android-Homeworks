package ua.arina.task5.interfaces;

import javax.inject.Singleton;

import dagger.Component;
import ua.arina.task5.activitys.MainActivity;
import ua.arina.task5.daggermoduls.PreferencesModule;
import ua.arina.task5.daggermoduls.RetrofitModule;

/**
 * Created by Arina on 19.04.2017
 */

@Singleton
@Component(modules = {RetrofitModule.class, PreferencesModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
}
