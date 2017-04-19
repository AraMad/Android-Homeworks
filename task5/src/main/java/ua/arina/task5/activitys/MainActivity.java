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
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ua.arina.task5.DaggerAplication;
import ua.arina.task5.R;
import ua.arina.task5.interfaces.AppComponent;
import ua.arina.task5.interfaces.WeatherApiInterface;
import ua.arina.task5.models.Current;
import ua.arina.task5.models.Weather;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static ua.arina.task5.BuildConfig.DEBUG;
import static ua.arina.task5.settings.Constants.BACKGROUND_ANIMATION_DURATION;
import static ua.arina.task5.settings.Constants.BACKGROUND_ANIMATION_PROPERTY_NAME;
import static ua.arina.task5.settings.Constants.CITY_NAME_KEY;
import static ua.arina.task5.settings.Constants.COLD_COLOR;
import static ua.arina.task5.settings.Constants.DEFAULT_COLOR;
import static ua.arina.task5.settings.Constants.FROST;
import static ua.arina.task5.settings.Constants.FROST_COLOR;
import static ua.arina.task5.settings.Constants.HOT;
import static ua.arina.task5.settings.Constants.HOT_COLOR;
import static ua.arina.task5.settings.Constants.LAST_UPDATE_TIME_KEY;
import static ua.arina.task5.settings.Constants.PICTURE_PATH_KEY;
import static ua.arina.task5.settings.Constants.DOWNLOAD_PICTURE_PROTOCOL_NAME;
import static ua.arina.task5.settings.Constants.TEMPERATURE_KEY;
import static ua.arina.task5.settings.Constants.VERY_COLD;
import static ua.arina.task5.settings.Constants.WARM;
import static ua.arina.task5.settings.Constants.WARM_COLOR;
import static ua.arina.task5.settings.Constants.WEATHER_DESCRIPTION_KEY;
import static ua.arina.task5.settings.Constants.VERY_COLD_COLOR;
import static ua.arina.task5.settings.Constants.VERY_WARM_COLOR;
import static ua.arina.task5.settings.Constants.ZERO;
import static ua.arina.task5.settings.Constants.ZERO_COLOR;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{

    private final String TAG = getClass().getSimpleName();

    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Inject Retrofit client;

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

        getAppComponent().inject(this);

        findViews();
        initScreen();
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

                Place place = PlaceAutocomplete.getPlace(this, data);

                if (DEBUG) {
                    Log.d(TAG, "Place: " + place.getName());
                }

                if (Locale.getDefault().equals(Locale.ENGLISH)
                        || Locale.getDefault().equals(Locale.CANADA) ||
                        Locale.getDefault().equals(Locale.UK)
                        || Locale.getDefault().equals(Locale.US)){

                    if (DEBUG) {
                        Log.d(TAG, "Locale");
                    }

                    if (place.getName().equals("Kropyvnytskyi")){
                        takeWeatherData("Kirovograd");
                    } else{
                        takeWeatherData(place.getName().toString());
                    }

                } else{

                    if (place.getName().equals("Кировоград")){
                        takeWeatherData("Kirovograd");
                    } else{
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
                        List<Address> addresses = null;
                        try {
                            addresses = geocoder.getFromLocation(place.getLatLng().latitude,
                                    place.getLatLng().longitude, 1);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (addresses != null && !addresses.isEmpty()) {
                            takeWeatherData(addresses.get(0).getLocality());
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        if (!isInternetAvailable()) {
            showToastMessage(getString(R.string.no_internet_text));
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
            showToastMessage(getString(R.string.no_internet_text));
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
                .getCurrentWeather(getString(R.string.apixu_key), cityName);

        call.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful() && response.body() != null) {

                    Current currentWeather = response.body().getCurrent();

                    try {
                        writeDataToPreferences(Double.toString(currentWeather.getTempC()),
                                currentWeather.getCondition().getText(),
                                currentWeather.getCondition().getIcon(),
                                response.body().getLocation().getName());
                        setBackgroundAndActionBarColor(currentWeather.getTempC());
                        setDataOnViews(currentWeather.getTempC(),
                                currentWeather.getCondition().getText(),
                                currentWeather.getCondition().getIcon(),
                                response.body().getLocation().getName());
                    } catch (NullPointerException e){
                        e.printStackTrace();
                        showToastMessage(getString(R.string.no_data_text));
                    }

                } else {
                    showToastMessage(getString(R.string.error_text));
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                showToastMessage(getString(R.string.error_text));
            }
        });
    }

    private void showToastMessage(String message){
        StyleableToast.makeText(getApplicationContext(),
                message,
                Toast.LENGTH_SHORT,
                R.style.StyledToast)
                .show();
    }

    private void setBackgroundAndActionBarColor(double temperature){
        int color = DEFAULT_COLOR;

        if (temperature == ZERO){
            color = ZERO_COLOR;
        } else if (temperature > ZERO && temperature < WARM){
            color = WARM_COLOR;
        } else if(temperature >= WARM && temperature < HOT){
            color = VERY_WARM_COLOR;
        } else if(temperature >= HOT){
            color = HOT_COLOR;
        } else if(temperature < ZERO && temperature > VERY_COLD){
            color = COLD_COLOR;
        } else if(temperature <= VERY_COLD && temperature > FROST){
            color = VERY_COLD_COLOR;
        } else if(temperature <= FROST){
            color = FROST_COLOR;
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

    private void setDataOnViews(double temperature, String weatherDescription,
                                String picturePath, String cityName){
        this.temperature.setText(String.format(getResources()
                .getString(R.string.temperature_text), temperature));
        this.weatherDescription.setText(weatherDescription);
        if(actionBar != null){
            actionBar.setTitle(cityName);
        }
        Picasso.with(getApplicationContext())
                .load((new StringBuilder(DOWNLOAD_PICTURE_PROTOCOL_NAME).append(picturePath)).toString())
                .placeholder(R.drawable.no_image_picture)
                .error(R.drawable.no_image_picture)
                .into(weatherPicture);
    }

    AppComponent getAppComponent() {
        return ((DaggerAplication)getApplication()).getAppComponent();
    }
}
