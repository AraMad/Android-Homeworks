package ua.arina.task5.models;

/**
 * Created by Arina on 26.04.2017
 */

public class WeatherData {

    private double temperature;
    private String description;
    private String cityName;
    private String iconPath;
    private String date;

    public WeatherData(double temperature, String description,
                       String iconPath, String cityName) {
        this.temperature = temperature;
        this.description = description;
        this.cityName = cityName;
        this.iconPath = iconPath;
    }

    public WeatherData(double temperature, String description, String iconPath,
                       String cityName, String date) {
        this.temperature = temperature;
        this.description = description;
        this.cityName = cityName;
        this.iconPath = iconPath;
        this.date = date;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
