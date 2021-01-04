package com.chplalex.shaman.Start

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.LOLLIPOP_MR1
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.chplalex.shaman.ui.App.Companion.instance
import com.chplalex.shaman.Common.Utils
import com.chplalex.shaman.Common.Utils.SP_NAME
import com.chplalex.shaman.Common.Utils.TAG
import com.chplalex.shaman.DBService.Location
import com.chplalex.shaman.DBService.Request
import com.chplalex.shaman.DBService.ShamanDao
import com.chplalex.shaman.mvp.model.WeatherData
import com.chplalex.shaman.WeatherService.OpenWeatherRetrofit
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ViewModelStart(application: Application) : AndroidViewModel(application), Observer<LocationData?> {

    val liveLocationData: MyMutableLiveData<LocationData?>
    val liveWeatherData: MyMutableLiveData<WeatherData?>
    val liveFavoriteData: MyMutableLiveData<Boolean?>

    private val openWeatherRetrofit: OpenWeatherRetrofit
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(SP_NAME, MODE_PRIVATE)
    private val shamanDao: ShamanDao = instance.shamanDao

    init {
        val baseURL = if (SDK_INT > LOLLIPOP_MR1) {
            OpenWeatherRetrofit.HTTPS + OpenWeatherRetrofit.BASE_URL
        } else {
            OpenWeatherRetrofit.HTTP + OpenWeatherRetrofit.BASE_URL
        }
        openWeatherRetrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenWeatherRetrofit::class.java)
        liveLocationData = MyMutableLiveData()
        liveWeatherData = MyMutableLiveData()
        liveFavoriteData = MyMutableLiveData()
    }

    override fun onChanged(locationData: LocationData?) {
        if (locationData == null || locationData.isEmpty) {
            liveWeatherData.updateValue(null)
            liveFavoriteData.updateValue(null)
            return
        }
        val lang = sharedPreferences.getString("pref_lang", "RU")
        val units = sharedPreferences.getString("pref_units", "metric")

        openWeatherRetrofit.loadWeather(locationData.toString(), OpenWeatherRetrofit.APP_ID, lang, units)
            .enqueue(object : Callback<WeatherData?> {

                override fun onResponse(call: Call<WeatherData?>, response: Response<WeatherData?>) {
                    if (response.isSuccessful && response.body() != null) {
                        val wd = response.body() as WeatherData
                        liveWeatherData.updateValue(wd)
                        dbDataExchange(wd)
                    } else {
                        liveWeatherData.updateValue(null)
                        liveFavoriteData.updateValue(null)
                    }
                }

                override fun onFailure(call: Call<WeatherData?>, t: Throwable) {
                    Log.d(TAG,"openWeatherRetrofit.loadWeather() -> onFailure() -> liveWeatherData.updateValue(null)")
                    Log.d(Utils.TAG, "Throwable t = " + t.message)
                    liveWeatherData.updateValue(null)
                    liveFavoriteData.updateValue(null)
                }
            })
    }

    fun initLocationData(arguments: Bundle?) {
//        val locationData = LocationData()
//        locationData.initFromBundle(arguments)
//        if (locationData.isEmpty) locationData.initFromSharedPreferences(sharedPreferences)
//        if (locationData.isEmpty) initLocationDataByCurrentLocation()
//        if (locationData.isEmpty)
//            liveLocationData.updateValue(null)
//        else
//            liveLocationData.updateValue(locationData)
    }

    fun initLocationDataByQuery(query: String) {
//        val locationData = LocationData(query.trim { it <= ' ' }, null)
//        if (locationData.isEmpty)
//            liveLocationData.updateValue(null)
//        else
//            liveLocationData.updateValue(locationData)
    }

    fun initLocationDataByCurrentLocation() {
//        LocationData().initFromCurrentLocation(getApplication()) { observable, _ ->
//            liveLocationData.updateValue(observable)
//        }
    }

    private fun dbDataExchange(wd: WeatherData) {
//        Thread {
//            val locations = shamanDao.getLocationByNameAndCountry(wd.getName(), wd.country)
//            if (locations.size == 0) {
//                shamanDao.insertLocation(
//                    Location(
//                        wd.id.toLong(),
//                        wd.name,
//                        wd.sys.country,
//                        wd.coord.lon,
//                        wd.coord.lat
//                    )
//                )
//                liveFavoriteData.postValue(false)
//            } else {
//                liveFavoriteData.postValue(locations[0].favorite)
//            }
//            shamanDao.insertRequest(
//                Request(
//                    wd.id.toLong(),
//                    wd.main.temp
//                )
//            )
//        }.start()
    }

    fun reverseFavorite() = Thread {
        val weatherData = liveWeatherData.value ?: return@Thread
        var favoriteData = liveFavoriteData.value
        favoriteData = if (favoriteData == null) true else !favoriteData
        shamanDao.updateLocationFavoriteByNameAndCountry(weatherData.name, weatherData.sys.country, favoriteData)
        liveFavoriteData.postValue(favoriteData)
    }.start()

}