package com.example.myapp.WeatherService;

import java.lang.reflect.Type;

public class OpenWeatherService extends WeatherService {

    private final String WEATHER_REQUEST = "https://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s&lang=RU&units=metric";
    private final String WEATHER_API_KEY = "bb18dcd129bad0dd351cdb2816a1aa9b";

    public Object getData(String location, Type typeOfWeatherData) {
        String spec = String.format(WEATHER_REQUEST, location, WEATHER_API_KEY);
        return getDataFromRequest(spec, typeOfWeatherData);
    }

}
