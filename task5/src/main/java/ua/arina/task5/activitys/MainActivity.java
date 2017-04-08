package ua.arina.task5.activitys;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ua.arina.task5.R;
import ua.arina.task5.interfaces.WeatherApiInterface;
import ua.arina.task5.models.Current;
import ua.arina.task5.models.Weather;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.arina.task5.settings.Constants.API_KEY;
import static ua.arina.task5.settings.Constants.BASE_URL;
import static ua.arina.task5.settings.Constants.COLD;
import static ua.arina.task5.settings.Constants.COLOR_KEY;
import static ua.arina.task5.settings.Constants.DATE_FORMAT;
import static ua.arina.task5.settings.Constants.DEFAULT_COLOR;
import static ua.arina.task5.settings.Constants.FROST;
import static ua.arina.task5.settings.Constants.HOT;
import static ua.arina.task5.settings.Constants.LAST_UPDATE_TIME_KEY;
import static ua.arina.task5.settings.Constants.PICTURE_PATH_KEY;
import static ua.arina.task5.settings.Constants.TEMPERATURE_KEY;
import static ua.arina.task5.settings.Constants.WARM;
import static ua.arina.task5.settings.Constants.WEATHER_DESCRIPTION_KEY;
import static ua.arina.task5.settings.Constants.VERY_COLD;
import static ua.arina.task5.settings.Constants.VERY_WARM;
import static ua.arina.task5.settings.Constants.ZERO;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private final String TAG = getClass().getSimpleName();

    private Retrofit client;

    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView temperature;
    private TextView weatherDescription;
    private ImageView weatherPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreenState();
        setContentView(R.layout.activity_main);

        findViews();

        client = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        initScreen();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.change_city_menu_item:
                startActivity(new Intent(this, ChooseCity.class)
                .putExtra(COLOR_KEY, constraintLayout.getDrawingCacheBackgroundColor()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (!isInternetAvailable()) {
            Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }else {
            takeWeatherData();
        }
    }

    private void setFullscreenState(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void findViews(){
        constraintLayout = (ConstraintLayout) findViewById(R.id.main_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        temperature = (TextView) findViewById(R.id.temperature_info);
        weatherDescription = (TextView) findViewById(R.id.weather_status_info);
        weatherPicture = (ImageView) findViewById(R.id.weather_icon);
    }

    private void initScreen(){
        ((TextView) findViewById(R.id.day_info))
                .setText((new SimpleDateFormat(DATE_FORMAT)) // TODO: write day word
                        .format(new Date().getTime()));
        swipeRefreshLayout.setOnRefreshListener(this);

        //getDefaultSharedPreferences(getApplicationContext())
        //      .getString(CITY_NAME_KEY, null);
        getSupportActionBar().setTitle("Kirovograd");

        if (isInternetAvailable()) {
            takeWeatherData();
        } else if((new SimpleDateFormat(DATE_FORMAT)) // TODO: simplify code
                .format(new Date().getTime()).equals(getDefaultSharedPreferences(getApplicationContext())
                .getString(LAST_UPDATE_TIME_KEY, null))){
            setBackgroundAndActionBarColor(Double.valueOf(getDefaultSharedPreferences(getApplicationContext())
                    .getString(TEMPERATURE_KEY, null)));
            setDataOnViews(Double.valueOf(getDefaultSharedPreferences(getApplicationContext())
                            .getString(TEMPERATURE_KEY, null)),
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString(WEATHER_DESCRIPTION_KEY, null),
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString(PICTURE_PATH_KEY, null));
        } else {
            Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeDataToPreferences(String temperature, String weatherDescription,
                                        String picturePath){
        getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(LAST_UPDATE_TIME_KEY, (new SimpleDateFormat(DATE_FORMAT))
                        .format(new Date().getTime()))
                .putString(TEMPERATURE_KEY, temperature)
                .putString(WEATHER_DESCRIPTION_KEY, weatherDescription)
                .putString(PICTURE_PATH_KEY, picturePath)
                .apply();
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return !(activeNetwork == null || !activeNetwork.isConnected());
    }

    private void takeWeatherData(){

        Call<Weather> call = client
                .create(WeatherApiInterface.class)
                .getCurrentWeather(API_KEY, "Kirovograd");

        call.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Current currentWeather = response.body().getCurrent();

                    setBackgroundAndActionBarColor(currentWeather.getTempC());
                    setDataOnViews(currentWeather.getTempC(),
                            currentWeather.getCondition().getText(),
                            currentWeather.getCondition().getIcon());
                    writeDataToPreferences(Double.toString(currentWeather.getTempC()),
                            currentWeather.getCondition().getText(),
                            currentWeather.getCondition().getIcon());
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_text), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setBackgroundAndActionBarColor(double temperature){
        int color = DEFAULT_COLOR;

        if (temperature == 0){
            color = ZERO;
        } else if (temperature > 0 && temperature < 10.0){
            color = WARM;
        } else if(temperature >= 10.0 && temperature < 20.0){
            color = VERY_WARM;
        } else if(temperature >= 20.0){
            color = HOT;
        } else if(temperature < 0 && temperature > -10.0){
            color = COLD;
        } else if(temperature <= -10.0 && temperature > -20.0){
            color = VERY_COLD;
        } else if(temperature <= -20.0){
            color = FROST;
        }

        ObjectAnimator.ofObject(constraintLayout, "backgroundColor", new ArgbEvaluator(),
                Color.WHITE, color)
                .setDuration(1000)
                .start();
        getSupportActionBar().setBackgroundDrawable(
                new ColorDrawable(color));

    }

    private void setDataOnViews(double temperature, String weatherDescription,
                                String picturePath){
        this.temperature.setText(String.format(getResources()
                .getString(R.string.temperature_text), temperature));
        this.weatherDescription.setText(weatherDescription);
        Picasso.with(getApplicationContext())
                .load("http:" + picturePath) // TODO: replace + with builder or something else
                .placeholder(R.mipmap.no_image_picture)
                .error(R.mipmap.no_image_picture)
                .into(weatherPicture);
    }
}
