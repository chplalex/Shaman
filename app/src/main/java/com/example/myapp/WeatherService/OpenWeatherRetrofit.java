package com.example.myapp.WeatherService;

import com.example.myapp.WeatherData.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherRetrofit {
    @GET("data/2.5/weather")
    Call<WeatherData> loadWeather(@Query("q") String location,
                                  @Query("appid") String appId,
                                  @Query("lang") String lang,
                                  @Query("units") String units);
}
