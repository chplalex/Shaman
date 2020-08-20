package com.example.myapp;

public class CurrentWeatherContainer {

    private static CurrentWeatherContainer instance;
    private static CurrentWeatherData data;

    private CurrentWeatherContainer() {
        // инициализация полей значениями по умолчанию
        data = new CurrentWeatherData();
        data.main = new Main();
        data.main.temp = (float) 23.5;
        data.main.pressure = 375;
        data.weather = new Weather[1];
        data.weather[0] = new Weather();
        data.weather[0].main = "Ясно";
        data.weather[0].description = "Лёгкая дымка";
        data.wind = new Wind();
        data.wind.speed = 10;
        data.wind.deg = 180;
    }

    public static CurrentWeatherContainer getInstance() {
        if (instance == null) {
            instance = new CurrentWeatherContainer();
        }
        return instance;
    }

    public static CurrentWeatherData getData() {
        return data;
    }

    public static void setData(CurrentWeatherData data) {
        CurrentWeatherContainer.data = data;
    }
}
