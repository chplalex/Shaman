package com.example.myapp.WeatherData;

public class CurrentWeatherContainer {

    private static CurrentWeatherContainer instance;
    private static CurrentWeatherData data;

    private CurrentWeatherContainer() {
        // инициализация полей значениями по умолчанию
        data = new CurrentWeatherData();
        data.main = new Main();
        data.main.temp = (float) 0;
        data.main.pressure = 0;
        data.main.humidity = 0;
        data.weather = new Weather[1];
        data.weather[0] = new Weather();
        data.weather[0].main = "";
        data.weather[0].description = "";
        data.wind = new Wind();
        data.wind.speed = 0;
        data.wind.deg = 0;
        data.sys = new Sys();
        data.coord = new Coord();
        data.clouds = new Clouds();
    }

    public static CurrentWeatherContainer getInstance() {
        initInstance();
        return instance;
    }

    public static CurrentWeatherData getData() {
        initInstance();
        return data;
    }

    public static void setData(CurrentWeatherData data) {
        initInstance();
        CurrentWeatherContainer.data = data;
    }

    private static void initInstance() {
        if (instance == null) {
            instance = new CurrentWeatherContainer();
        }
    }
}
