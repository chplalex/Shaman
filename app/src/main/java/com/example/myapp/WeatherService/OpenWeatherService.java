package com.example.myapp.WeatherService;

import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

public class OpenWeatherService extends WeatherService {

    private final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU&units=metric";
    private final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";
    private String point = "Moscow";

    public void requestCurrent() {
        String spec = String.format(WEATHER_REQUEST, point, WEATHER_API_KEY);
        makeWeatherRequest(spec, CurrentWeatherData.class);
    }

    public Object getData(String location, Type typeOfWeatherData) {
        String spec = String.format(WEATHER_REQUEST, location, WEATHER_API_KEY);
        return getDataFromRequest(spec, typeOfWeatherData);
    }

    @Override
    public void setData(@NotNull Object data) {
        CurrentWeatherContainer.setData((CurrentWeatherData) data);
    }
}
