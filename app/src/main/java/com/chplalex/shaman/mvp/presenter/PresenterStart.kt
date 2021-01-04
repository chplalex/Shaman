package com.chplalex.shaman.mvp.presenter

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.GONE
import android.view.View.VISIBLE
import com.chplalex.shaman.Common.Utils.HPAS_IN_ONE_MMHG
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LATITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LONGITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_NAME
import com.chplalex.shaman.Common.Utils.SP_NAME
import com.chplalex.shaman.DBService.Location
import com.chplalex.shaman.DBService.Request
import com.chplalex.shaman.DBService.ShamanDao
import com.chplalex.shaman.R
import com.chplalex.shaman.Start.LocationData
import com.chplalex.shaman.WeatherService.OpenWeatherRetrofit
import com.chplalex.shaman.mvp.model.WeatherData
import com.chplalex.shaman.mvp.service.LocationService
import com.chplalex.shaman.mvp.view.IViewStart
import com.chplalex.shaman.ui.App
import moxy.MvpPresenter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PresenterStart(private val context: Context, private val arguments: Bundle?) : MvpPresenter<IViewStart>() {

    private val retrofit: OpenWeatherRetrofit = App.instance.retrofit
    private val shamanDao: ShamanDao = App.instance.shamanDao
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(SP_NAME, MODE_PRIVATE)
    private val resources: Resources = context.resources

    private var weatherData: WeatherData? = null
    private var favoriteState: Boolean = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initRowsVisibility()
        initLocationData(arguments)
    }

    private fun initRowsVisibility() {
        viewState.setPressureVisibility(if (sharedPreferences.getBoolean("pref_pressure", true)) VISIBLE else GONE)
        viewState.setWindVisibility(if (sharedPreferences.getBoolean("pref_wind", true)) VISIBLE else GONE)
        viewState.setSunMovingVisibility(if (sharedPreferences.getBoolean("pref_sun_moving", true)) VISIBLE else GONE)
        viewState.setHumidityVisibility(if (sharedPreferences.getBoolean("pref_humidity", true)) VISIBLE else GONE)
    }

    fun initLocationData(arguments: Bundle?) {
        var locationData = LocationService.getFromBundle(arguments)
        if (locationData == null) locationData = LocationService.getFromSharedPreferences(sharedPreferences)
        if (locationData == null)
            LocationService.getFromCurrentLocation(context) { o, _ -> initWeatherData(o as LocationData) }
        else
            initWeatherData(locationData)
    }

    private fun initWeatherData(locationData: LocationData) {
        val lang = sharedPreferences.getString("pref_lang", "RU")
        val units = sharedPreferences.getString("pref_units", "metric")
        retrofit.loadWeather(locationData.toString(), OpenWeatherRetrofit.APP_ID, lang, units)
            .enqueue(object : Callback<WeatherData> {

                override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                    if (response.isSuccessful && response.body() != null) {
                        setWeatherData(response.body() as WeatherData)
                        dbDataExchange(response.body() as WeatherData)
                    } else {
                        setNoWeatherData()
                    }
                }

                override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                    setNoWeatherData()
                }

            })
    }

    private fun dbDataExchange(wd: WeatherData) = Thread {
        val locations = shamanDao.getLocationByNameAndCountry(wd.name, wd.country)

        if (locations.isEmpty()) {
            shamanDao.insertLocation(
                Location(
                    wd.id.toLong(),
                    wd.name,
                    wd.sys.country,
                    wd.coord.lon,
                    wd.coord.lat,
                    favoriteState
                )
            )
        } else {
            setFavoriteState(locations[0].favorite)
        }

        shamanDao.insertRequest(
            Request(
                wd.id.toLong(),
                wd.main.temp
            )
        )
    }.start()

    private fun setNoWeatherData() {
        this.weatherData = null

        Handler(Looper.getMainLooper()).post {
            viewState.setLocationName(resources.getString(R.string.not_found_location_name))
            viewState.setLocationCountry("--")
            viewState.setUncertainTemp()
            viewState.setWeatherDescription("--")
            viewState.setPressure("--")
            viewState.setWind("--")
            viewState.setSunMoving("--")
            viewState.setHumidity("--")
        }

        sharedPreferences.edit().apply {
            remove(LOCATION_ARG_NAME)
            remove(LOCATION_ARG_COUNTRY)
            remove(LOCATION_ARG_LONGITUDE)
            remove(LOCATION_ARG_LATITUDE)
            apply()
        }
    }

    private fun setWeatherData(weatherData: WeatherData) {
        this.weatherData = weatherData

        Handler(Looper.getMainLooper()).post {
            viewState.setLocationName(weatherData.name)
            viewState.setLocationCountry(weatherData.country)
            viewState.setTemp(weatherData.temp)
            viewState.setWeatherDescription(weatherData.description)
            viewState.setPressure(weatherData.pressure)
            viewState.setWind(weatherData.windString)
            viewState.setSunMoving(weatherData.sunMoving)
            viewState.setHumidity(weatherData.humidity)
        }

        sharedPreferences.edit().apply {
            putString(LOCATION_ARG_NAME, weatherData.name)
            putString(LOCATION_ARG_COUNTRY, weatherData.country)
            putFloat(LOCATION_ARG_LONGITUDE, weatherData.coord.lon)
            putFloat(LOCATION_ARG_LATITUDE, weatherData.coord.lat)
            apply()
        }
    }

    private fun setFavoriteState(state: Boolean) = Handler(Looper.getMainLooper()).post() {
        viewState.setFavoriteState(state)
    }

    private val WeatherData.country: String
        get() = sys.country
    private val WeatherData.temp: Float
        get() = main.temp
    private val WeatherData.tempString: String
        get() = String.format(Locale.getDefault(), "%+.0f°C", main.temp)
    private val WeatherData.pressure: String
        get() = String.format(
            Locale.getDefault(),
            "%d %s = %.0f %s",
            main.pressure,
            resources.getString(R.string.PressureUnit_hPa),
            main.pressure / HPAS_IN_ONE_MMHG,
            resources.getString(R.string.PressureUnit_mmHg)
        )
    private val WeatherData.humidity: String
        get() = String.format(
            Locale.getDefault(),
            "%d",
            main.humidity
        )
    private val WeatherData.sunMoving: String
        get() = String.format(
            Locale.getDefault(),
            "%s %s, %s %s",
            resources.getString(R.string.Sunrise),
            timeToString(sys.sunrise, timezone.toLong()),
            resources.getString(R.string.Sunset),
            timeToString(sys.sunset, timezone.toLong())
        )
    private val WeatherData.windString: String
        get() = String.format(
            Locale.getDefault(),
            "%s %.0f %s",
            windDegToAzimuth(wind.deg),
            wind.speed,
            resources.getString(R.string.WindSpeedUnit)
        )
    private val WeatherData.description: String
        get() = weather[0].description

    private val WeatherData.imageResource: Int
        get() = when (weather[0].icon) {
            "01d" -> R.drawable.ic_01d
            "02d" -> R.drawable.ic_02d
            "03d" -> R.drawable.ic_03d
            "04d" -> R.drawable.ic_04d
            "09d" -> R.drawable.ic_09d
            "10d" -> R.drawable.ic_10d
            "11d" -> R.drawable.ic_11d
            "13d" -> R.drawable.ic_13d
            "50d" -> R.drawable.ic_50d
            "01n" -> R.drawable.ic_01n
            "02n" -> R.drawable.ic_02n
            "03n" -> R.drawable.ic_03n
            "04n" -> R.drawable.ic_04n
            "09n" -> R.drawable.ic_09n
            "10n" -> R.drawable.ic_10n
            "11n" -> R.drawable.ic_11n
            "13n" -> R.drawable.ic_13n
            "50n" -> R.drawable.ic_50n
            else -> R.drawable.ic_report_problem
        }

    private fun timeToString(unixSeconds: Long, unixSecondsDiff: Long): String {
        val date = Date(unixSeconds * 1000L)
        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    private fun windDegToAzimuth(deg: Int): String {
        val id = when (deg) {
            in 0..22 -> R.string.wind_north
            in 23..67 -> R.string.wind_north_east
            in 68..112 -> R.string.wind_east
            in 113..157 -> R.string.wind_south_east
            in 158..202 -> R.string.wind_south
            in 203..247 -> R.string.wind_south_east
            in 248..292 -> R.string.wind_west
            in 293..337 -> R.string.wind_north_west
            in 338..359 -> R.string.wind_north
            else -> R.string.incorrect_data
        }
        return resources.getString(id)
    }

    fun actionLocationByQuerySelected(query: String) = LocationData(query.trim { it <= ' ' }, "", 0.0f, 0.0f).also {
        if (!it.isEmpty) {
            initWeatherData(it)
        }
    }

    fun actionMyLocationSelected() {
        LocationService.getFromCurrentLocation(context) { o, _ ->
            initWeatherData(o as LocationData)
        }
    }

    fun actionFavoriteSelected() = weatherData?.let {
        Thread {
            favoriteState = !favoriteState
            shamanDao.updateLocationFavoriteByNameAndCountry(it.name, it.country, favoriteState)
        }.start()
        setFavoriteState(favoriteState)
    }
}