package com.chplalex.shaman.WeatherService

import com.chplalex.shaman.mvp.model.WeatherData
import io.reactivex.Observable
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherRetrofit {

    @GET("data/2.5/weather")
    fun loadWeather(
        @Query("q") location: String?,
        @Query("appid") appId: String?,
        @Query("lang") lang: String?,
        @Query("units") units: String?
    ): Call<WeatherData>

    @GET("data/2.5/weather")
    fun loadWeatherRX(
        @Query("q") location: String?,
        @Query("appid") appId: String?,
        @Query("lang") lang: String?,
        @Query("units") units: String?
    ): Single<WeatherData>

    companion object {

        const val HTTP = "http"
        const val HTTPS = "https"
        const val BASE_URL = "://api.openweathermap.org/"
        const val APP_ID = "bb18dcd129bad0dd351cdb2816a1aa9b"
    }
}