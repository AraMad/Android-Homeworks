package ua.arina.task5.activitys;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ua.arina.task5.WeatherApplication;
import ua.arina.task5.R;
import ua.arina.task5.interfaces.AppComponent;
import ua.arina.task5.interfaces.WeatherApiInterface;
import ua.arina.task5.models.currentweather.Current;
import ua.arina.task5.models.currentweather.Weather;
import ua.arina.task5.models.WeatherData;
import ua.arina.task5.utils.MessageDisplayer;

import static ua.arina.task5.settings.Constants.BACKGROUND_ANIMATION_DURATION;
import static ua.arina.task5.settings.Constants.BACKGROUND_ANIMATION_PROPERTY_NAME;
import static ua.arina.task5.settings.Constants.CITY_NAME_KEY;
import static ua.arina.task5.settings.Constants.FROST;
import static ua.arina.task5.settings.Constants.HOT;
import static ua.arina.task5.settings.Constants.LAST_UPDATE_TIME_KEY;
import static ua.arina.task5.settings.Constants.PICTURE_PATH_KEY;
import static ua.arina.task5.settings.Constants.DOWNLOAD_PICTURE_PROTOCOL_NAME;
import static ua.arina.task5.settings.Constants.TEMPERATURE_KEY;
import static ua.arina.task5.settings.Constants.VERY_COLD;
import static ua.arina.task5.settings.Constants.WARM;
import static ua.arina.task5.settings.Constants.WEATHER_DESCRIPTION_KEY;
import static ua.arina.task5.settings.Constants.ZERO;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private final String TAG = getClass().getSimpleName();

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private final String SAVED_STATE = "state";

    @Inject
    Retrofit client;
    @Inject
    SharedPreferences preferences;
    @Inject
    MessageDisplayer messageDisplayer;

    @BindView(R.id.main_layout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.temperature_info)
    TextView temperature;
    @BindView(R.id.weather_status_info)
    TextView weatherDescription;
    @BindView(R.id.weather_icon)
    ImageView weatherPicture;

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        getAppComponent().inject(this);
        ButterKnife.bind(this);

        actionBar = getSupportActionBar();

        initScreen(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(SAVED_STATE, true);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
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
                takeWeatherData(getPlaceName(PlaceAutocomplete.getPlace(this, data)));
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (!isInternetAvailable()) {
            messageDisplayer
                    .showToastMessage(getString(R.string.no_internet_text), getApplicationContext());
        }else {
            takeWeatherData(preferences
                    .getString(CITY_NAME_KEY, null));
        }
    }

    private void initScreen(Bundle savedInstanceState){
        ((TextView) findViewById(R.id.day_info))
                .setText((DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                        .format(new Date().getTime()));

        swipeRefreshLayout.setOnRefreshListener(this);

        if( (DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                .format(new Date().getTime()).equals(preferences
                        .getString(LAST_UPDATE_TIME_KEY, null)) ){

            setBackgroundAndActionBarColor(Double.valueOf(preferences
                    .getString(TEMPERATURE_KEY, null)));

            setDataOnViews(new WeatherData(Double.valueOf(preferences
                    .getString(TEMPERATURE_KEY, null)),
                    preferences
                            .getString(WEATHER_DESCRIPTION_KEY, null),
                    preferences
                            .getString(PICTURE_PATH_KEY, null),
                    preferences
                            .getString(CITY_NAME_KEY, null)));
        }

        if (savedInstanceState != null && !savedInstanceState.getBoolean(SAVED_STATE, false)){
            if (isInternetAvailable()) {
                takeWeatherData(preferences
                        .getString(CITY_NAME_KEY, null));
            } else {
                messageDisplayer
                        .showToastMessage(getString(R.string.no_internet_text), getApplicationContext());
            }
        }
    }

    private void writeDataToPreferences(WeatherData weatherData){
        preferences
                .edit()
                .putString(LAST_UPDATE_TIME_KEY, (DateFormat.getDateInstance(DateFormat.LONG, Locale.ENGLISH))
                        .format(new Date().getTime()))
                .putString(TEMPERATURE_KEY, Double.toString(weatherData.getTemperature()))
                .putString(WEATHER_DESCRIPTION_KEY, weatherData.getDescription())
                .putString(PICTURE_PATH_KEY, weatherData.getIconPath())
                .putString(CITY_NAME_KEY, weatherData.getCityName())
                .apply();
    }

    private boolean isInternetAvailable(){
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return !(activeNetwork == null || !activeNetwork.isConnected());
    }

    private void takeWeatherData(String cityName){

        if(cityName == null){
            messageDisplayer
                    .showToastMessage(getString(R.string.no_data_text), getApplicationContext());
            return;
        }

        Call<Weather> call = client
                .create(WeatherApiInterface.class)
                .getCurrentWeather(getString(R.string.apixu_key), cityName);

        call.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Current currentWeather = response.body().getCurrent();

                    try {

                        WeatherData weatherData = new WeatherData(currentWeather.getTempC(),
                                currentWeather.getCondition().getText(),
                                currentWeather.getCondition().getIcon(),
                                response.body().getLocation().getName());

                        writeDataToPreferences(weatherData);
                        setBackgroundAndActionBarColor(currentWeather.getTempC());
                        setDataOnViews(weatherData);

                    } catch (NullPointerException e){
                        e.printStackTrace();
                        messageDisplayer
                        .showToastMessage(getString(R.string.no_data_text),
                                getApplicationContext());
                    }

                } else {
                    messageDisplayer
                            .showToastMessage(getString(R.string.error_text), getApplicationContext());
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                messageDisplayer
                .showToastMessage(getString(R.string.error_text), getApplicationContext());
            }
        });
    }

    private void setBackgroundAndActionBarColor(double temperature){
        int color = ResourcesCompat.getColor(getResources(), R.color.default_color, null);

        if (temperature == ZERO){
            color = ResourcesCompat.getColor(getResources(), R.color.zero, null);
        } else if (temperature > ZERO && temperature < WARM){
            color = ResourcesCompat.getColor(getResources(), R.color.warm, null);
        } else if(temperature >= WARM && temperature < HOT){
            color = ResourcesCompat.getColor(getResources(), R.color.very_warm, null);
        } else if(temperature >= HOT){
            color = ResourcesCompat.getColor(getResources(), R.color.hot, null);
        } else if(temperature < ZERO && temperature > VERY_COLD){
            color = ResourcesCompat.getColor(getResources(), R.color.cold, null);
        } else if(temperature <= VERY_COLD && temperature > FROST){
            color = ResourcesCompat.getColor(getResources(), R.color.very_cold, null);
        } else if(temperature <= FROST){
            color = ResourcesCompat.getColor(getResources(), R.color.frost, null);
        }

        ObjectAnimator.ofObject(constraintLayout, BACKGROUND_ANIMATION_PROPERTY_NAME,
                new ArgbEvaluator(), Color.WHITE, color)
                .setDuration(BACKGROUND_ANIMATION_DURATION)
                .start();
        if(actionBar != null){
            actionBar.setBackgroundDrawable(
                    new ColorDrawable(color));
        }

    }

    private void setDataOnViews(WeatherData weatherData){

        this.temperature.setText(String.format(getResources()
                .getString(R.string.temperature_text), weatherData.getTemperature()));
        this.weatherDescription.setText(weatherData.getDescription());
        if(actionBar != null){
            actionBar.setTitle(weatherData.getCityName());
        }

        Picasso.with(getApplicationContext())
                .load((new StringBuilder(DOWNLOAD_PICTURE_PROTOCOL_NAME)
                        .append(weatherData.getIconPath())).toString())
                .placeholder(R.drawable.no_image_picture)
                .error(R.drawable.no_image_picture)
                .into(weatherPicture);
    }

    private String getPlaceName(Place place){

        if (Locale.getDefault().equals(Locale.ENGLISH)
                || Locale.getDefault().equals(Locale.CANADA)
                || Locale.getDefault().equals(Locale.UK)
                || Locale.getDefault().equals(Locale.US)){

            return place.getName().toString();

        } else{

            if (place.getName().equals("Kropyvnytskyi") || place.getName().equals("Кировоград")) {
                return  "Kirovograd";
            }

            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(place.getLatLng().latitude,
                        place.getLatLng().longitude, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getLocality();
            }
        }

        return null;
    }

    AppComponent getAppComponent() {
        return ((WeatherApplication)getApplication()).getAppComponent();
    }
}
