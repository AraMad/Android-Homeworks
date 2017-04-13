package ua.arina.task5.settings;

import android.graphics.Color;

/**
 * Created by Arina on 07.04.2017
 */

public class Constants {
    public static final String BASE_APIXU_URL = "http://api.apixu.com/v1/";
    public static final String DOWNLOAD_PICTURE_PROTOCOL_NAME = "http:";

    public static final String CITY_NAME_KEY = "city_name";
    public static final String LAST_UPDATE_TIME_KEY = "last_update";
    public static final String WEATHER_DESCRIPTION_KEY = "weather_description";
    public static final String PICTURE_PATH_KEY = "picture";
    public static final String TEMPERATURE_KEY = "last_temperature";

    public static final String BACKGROUND_ANIMATION_PROPERTY_NAME = "backgroundColor";
    public static final int BACKGROUND_ANIMATION_DURATION = 1000;

    public static final int FROST_COLOR = Color.rgb(0,191,255);
    public static final int VERY_COLD_COLOR = Color.rgb(135,206,250);
    public static final int COLD_COLOR = Color.rgb(240,248,255);
    public static final int ZERO_COLOR = Color.rgb(248,248,255);
    public static final int WARM_COLOR = Color.rgb(255,215,0);
    public static final int VERY_WARM_COLOR = Color.rgb(255,165,0);
    public static final int HOT_COLOR = Color.rgb(255,69,0);
    public static final int DEFAULT_COLOR = Color.rgb(0,0,205);

    public static final double ZERO = 0;
    public static final double FROST = -20.0;
    public static final double VERY_COLD = -10.0;
    public static final double HOT = 20.0;
    public static final double WARM = 10.0;
}
