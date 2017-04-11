package ua.arina.task5.activitys;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
import static ua.arina.task5.BuildConfig.DEBUG;
import static ua.arina.task5.settings.Constants.APIXU_API_KEY;
import static ua.arina.task5.settings.Constants.BASE_URL;
import static ua.arina.task5.settings.Constants.CITY_NAME_KEY;
import static ua.arina.task5.settings.Constants.COLD;
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

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private Retrofit client;

    private ConstraintLayout constraintLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView temperature;
    private TextView weatherDescription;
    private ImageView weatherPicture;
    private ActionBar actionBar;

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
            case R.id.peek_city:
                try {
                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                    .setFilter(new AutocompleteFilter.Builder()
                                            .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                                            .build())
                                    .build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException |
                        GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_text), Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);

                if (DEBUG) {
                    Log.d(TAG, "Place: " + place.getName());
                }

                if (place.getName().equals("Кировоград")){
                    takeWeatherData("Kirovograd");
                } else{
                    final Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(place.getLatLng().latitude,
                                place.getLatLng().longitude, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (addresses != null && !addresses.isEmpty()) {
                        takeWeatherData(addresses.get(0).getLocality());

                        if (DEBUG) {
                            Log.d(TAG, "Place: " + addresses.get(0).getLocality());
                        }
                    }
                }

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_text), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (!isInternetAvailable()) {
            Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }else {
            takeWeatherData(getDefaultSharedPreferences(getApplicationContext())
                    .getString(CITY_NAME_KEY, null));
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
        actionBar = getSupportActionBar();
    }

    private void initScreen(){
        ((TextView) findViewById(R.id.day_info))
                .setText((DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                        .format(new Date().getTime()));

        swipeRefreshLayout.setOnRefreshListener(this);

        // TODO: simplify code
        if( (DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                .format(new Date().getTime()).equals(getDefaultSharedPreferences(getApplicationContext())
                        .getString(LAST_UPDATE_TIME_KEY, null)) ){
            setBackgroundAndActionBarColor(Double.valueOf(getDefaultSharedPreferences(getApplicationContext())
                    .getString(TEMPERATURE_KEY, null)));

            setDataOnViews(Double.valueOf(getDefaultSharedPreferences(getApplicationContext())
                            .getString(TEMPERATURE_KEY, null)),
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString(WEATHER_DESCRIPTION_KEY, null),
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString(PICTURE_PATH_KEY, null),
                    getDefaultSharedPreferences(getApplicationContext())
                            .getString(CITY_NAME_KEY, null));
        }

        if (isInternetAvailable()) {
            takeWeatherData(getDefaultSharedPreferences(getApplicationContext())
                    .getString(CITY_NAME_KEY, null));
        } else {
            Toast.makeText(this, getString(R.string.no_internet_text), Toast.LENGTH_SHORT).show();
        }
    }

    private void writeDataToPreferences(String temperature, String weatherDescription,
                                        String picturePath, String cityName){
        getDefaultSharedPreferences(getApplicationContext())
                .edit()
                .putString(LAST_UPDATE_TIME_KEY, (DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                        .format(new Date().getTime()))
                .putString(TEMPERATURE_KEY, temperature)
                .putString(WEATHER_DESCRIPTION_KEY, weatherDescription)
                .putString(PICTURE_PATH_KEY, picturePath)
                .putString(CITY_NAME_KEY, cityName)
                .apply();
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return !(activeNetwork == null || !activeNetwork.isConnected());
    }

    private void takeWeatherData(String cityName){

        Call<Weather> call = client
                .create(WeatherApiInterface.class)
                .getCurrentWeather(APIXU_API_KEY, cityName);

        call.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Current currentWeather = response.body().getCurrent();

                    if (currentWeather != null){
                        try {
                            writeDataToPreferences(Double.toString(currentWeather.getTempC()),
                                    currentWeather.getCondition().getText(),
                                    currentWeather.getCondition().getIcon(),
                                    cityName);
                            setBackgroundAndActionBarColor(currentWeather.getTempC());
                            setDataOnViews(currentWeather.getTempC(),
                                    currentWeather.getCondition().getText(),
                                    currentWeather.getCondition().getIcon(),
                                    cityName);
                        } catch (NullPointerException e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.error_text), Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_text), Toast.LENGTH_SHORT).show();
                    }
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
        if(actionBar != null){
            actionBar.setBackgroundDrawable(
                    new ColorDrawable(color));
        }

    }

    private void setDataOnViews(double temperature, String weatherDescription,
                                String picturePath, String cityName){
        this.temperature.setText(String.format(getResources()
                .getString(R.string.temperature_text), temperature));
        this.weatherDescription.setText(weatherDescription);
        getSupportActionBar().setTitle(cityName);
        Picasso.with(getApplicationContext())
                .load("http:" + picturePath) // TODO: replace + with builder or something else
                .placeholder(R.mipmap.no_image_picture)
                .error(R.mipmap.no_image_picture)
                .into(weatherPicture);
    }
}
