package com.chplalex.shaman.utils

import android.content.res.Resources
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.model.api.WeatherData
import java.text.SimpleDateFormat
import java.util.*

val WeatherData.country: String
    get() = sys.country

val WeatherData.temp: Float
    get() = main.temp

val WeatherData.tempString: String
    get() = String.format(Locale.getDefault(), "%+.0f°C", main.temp)

fun WeatherData.pressure(resources: Resources) = String.format(
        Locale.getDefault(),
        "%d %s = %.0f %s",
        main.pressure,
        resources.getString(R.string.PressureUnit_hPa),
        main.pressure / HPAS_IN_ONE_MMHG,
        resources.getString(R.string.PressureUnit_mmHg)
)

val WeatherData.humidity: String
    get() = String.format(
            Locale.getDefault(),
            "%d",
            main.humidity
    )
fun WeatherData.sunMoving(resources: Resources) = String.format(
            Locale.getDefault(),
            "%s %s, %s %s",
            resources.getString(R.string.Sunrise),
            timeToString(sys.sunrise, timezone.toLong()),
            resources.getString(R.string.Sunset),
            timeToString(sys.sunset, timezone.toLong())
    )

fun WeatherData.windString(resources: Resources) = String.format(
            Locale.getDefault(),
            "%s %.0f %s",
            windDegToAzimuth(wind.deg, resources),
            wind.speed,
            resources.getString(R.string.WindSpeedUnit)
    )

val WeatherData.description: String
    get() = weather[0].description

val WeatherData.imageResource: Int
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

private fun windDegToAzimuth(deg: Int, resources: Resources) = resources.getString(when (deg) {
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
})