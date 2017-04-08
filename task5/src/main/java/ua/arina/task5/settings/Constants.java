package ua.arina.task5.settings;

import android.graphics.Color;

/**
 * Created by Arina on 07.04.2017
 */

public class Constants {
    public static final String BASE_URL = "http://api.apixu.com/v1/";
    public static final String API_KEY = "c9dc0ef125a241ceb2a111749170704";

    public static final String DATE_FORMAT = "dd MM yyyy";

    public static final String CITY_NAME_KEY = "city_name";
    public static final String LAST_UPDATE_TIME_KEY = "last_update";
    public static final String WEATHER_DESCRIPTION_KEY = "weather_description";
    public static final String PICTURE_PATH_KEY = "picture";
    public static final String TEMPERATURE_KEY = "last_temperature";

    public static final String COLOR_KEY = "bar_color";

    //background colors
    public static final int FROST = Color.rgb(0,191,255);
    public static final int VERY_COLD = Color.rgb(135,206,250);
    public static final int COLD = Color.rgb(240,248,255);
    public static final int ZERO = Color.rgb(248,248,255);
    public static final int WARM = Color.rgb(255,215,0);
    public static final int VERY_WARM = Color.rgb(255,165,0);
    public static final int HOT = Color.rgb(255,69,0);
    public static final int DEFAULT_COLOR = Color.rgb(0,0,205);
}
