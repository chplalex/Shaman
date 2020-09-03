package com.example.myapp.WeatherService;

import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;

import org.jetbrains.annotations.NotNull;

public class OpenWeatherService extends WeatherService {

    private final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU&units=metric";
    private final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";
    private String point = "Moscow";

    public void requestCurrent() {
        String spec = String.format(WEATHER_REQUEST, point, WEATHER_API_KEY);
        makeWeatherRequest(spec, CurrentWeatherData.class);
    }

    @Override
    public void setData(@NotNull Object data) {
        CurrentWeatherContainer.setData((CurrentWeatherData) data);
    }
}
