package com.example.myapp.WeatherService;

import com.example.myapp.WeatherData.WeatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeatherRetrofit {
    String HTTP = "http";
    String HTTPS = "https";
    String BASE_URL = "://api.openweathermap.org/";
    String APP_ID = "bb18dcd129bad0dd351cdb2816a1aa9b";

    @GET("data/2.5/weather")
    Call<WeatherData> loadWeather(@Query("q") String location,
                                  @Query("appid") String appId,
                                  @Query("lang") String lang,
                                  @Query("units") String units);
}
