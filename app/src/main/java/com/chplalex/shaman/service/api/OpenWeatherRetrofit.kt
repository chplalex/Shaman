package com.chplalex.shaman.service.api

import com.chplalex.shaman.mvp.model.api.WeatherData
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://api.openweathermap.org/"
const val APP_ID = "bb18dcd129bad0dd351cdb2816a1aa9b"

interface OpenWeatherRetrofit {

    @GET("data/2.5/weather")
    fun loadWeather(
        @Query("q") location: String?,
        @Query("appid") appId: String?,
        @Query("lang") lang: String?,
        @Query("units") units: String?
    ): Single<WeatherData>

}