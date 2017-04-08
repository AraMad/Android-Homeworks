package ua.arina.task5.interfaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import ua.arina.task5.models.Current;
import ua.arina.task5.models.Weather;

/**
 * Created by Arina on 07.04.2017
 */

public interface WeatherApiInterface {

    @GET("current.json")
    Call<Weather> getCurrentWeather(@Query("key") String key, @Query("q") String city);
}
