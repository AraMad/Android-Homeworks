package ua.arina.task5.daggermoduls;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static ua.arina.task5.settings.Constants.BASE_APIXU_URL;

/**
 * Created by Arina on 19.04.2017
 */

@Module
public class RetrofitModule {

    @Provides
    @Singleton
    Retrofit providesRetrofitClient() {
        return new Retrofit.Builder()
                .baseUrl(BASE_APIXU_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
