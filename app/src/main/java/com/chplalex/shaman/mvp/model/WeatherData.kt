package com.chplalex.shaman.mvp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("timezone") @Expose
    var timezone: Int = 0,
    @SerializedName("id") @Expose
    var id: Int = 0,
    @SerializedName("name") @Expose
    var name: String = "",
    @SerializedName("cod") @Expose
    var cod: Int = 0,
    @SerializedName("coord") @Expose
    var coord: Coord = Coord(),
    @SerializedName("weather") @Expose
    var weather: Array<Weather> = arrayOf(Weather()),
    @SerializedName("main") @Expose
    var main: Main = Main(),
    @SerializedName("wind") @Expose
    var wind: Wind = Wind(),
    @SerializedName("clouds") @Expose
    var clouds: Clouds = Clouds(),
    @SerializedName("sys") @Expose
    var sys: Sys = Sys()
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WeatherData

        if (timezone != other.timezone) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (cod != other.cod) return false
        if (coord != other.coord) return false
        if (!weather.contentEquals(other.weather)) return false
        if (main != other.main) return false
        if (wind != other.wind) return false
        if (clouds != other.clouds) return false
        if (sys != other.sys) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timezone
        result = 31 * result + id
        result = 31 * result + name.hashCode()
        result = 31 * result + cod
        result = 31 * result + coord.hashCode()
        result = 31 * result + weather.contentHashCode()
        result = 31 * result + main.hashCode()
        result = 31 * result + wind.hashCode()
        result = 31 * result + clouds.hashCode()
        result = 31 * result + sys.hashCode()
        return result
    }
}

//    val lon: Float
//        get() = coord!!.lon
//
//    val lat: Float
//        get() = coord!!.lat
//
//    val country: String?
//        get() = if (sys == null || sys!!.country == null) "" else sys!!.country
//
//    val temp: Float
//        get() = main!!.temp
//
//    val tempString: String
//        get() = String.format(Locale.getDefault(), "%+.0f°C", main!!.temp)
//
//    val pressure: String
//        get() = String.format(
//            Locale.getDefault(),
//            "%d %s = %.0f %s",
//            main!!.pressure,
//            resources!!.getString(R.string.PressureUnit_hPa),
//            main!!.pressure / HPAS_IN_ONE_MMHG,
//            resources!!.getString(R.string.PressureUnit_mmHg)
//        )
//
//    val humidity: String
//        get() = String.format(
//            Locale.getDefault(),
//            "%d",
//            main!!.humidity
//        )
//
//    val sunMoving: String
//        get() = String.format(
//            Locale.getDefault(),
//            "%s %s, %s %s",
//            resources!!.getString(R.string.Sunrise),
//            timeToString(sys!!.sunrise, timezone.toLong()),
//            resources!!.getString(R.string.Sunset),
//            timeToString(sys!!.sunset, timezone.toLong())
//        )
//
//    fun getWind(): String {
//        return String.format(
//            Locale.getDefault(),
//            "%s %.0f %s",
//            windDegToAzimuth(wind!!.deg),
//            wind!!.speed,
//            resources!!.getString(R.string.WindSpeedUnit)
//        )
//    }
//
//    val description: String?
//        get() = weather[0]!!.description
//
//    private fun windDegToAzimuth(deg: Int): String {
//        if (338 <= deg || deg < 23) {
//            return resources!!.getString(R.string.wind_north)
//        }
//        if (23 <= deg && deg < 68) {
//            return resources!!.getString(R.string.wind_north_east)
//        }
//        if (68 <= deg && deg < 113) {
//            return resources!!.getString(R.string.wind_east)
//        }
//        if (113 <= deg && deg < 158) {
//            return resources!!.getString(R.string.wind_south_east)
//        }
//        if (158 <= deg && deg < 203) {
//            return resources!!.getString(R.string.wind_south)
//        }
//        if (203 <= deg && deg < 248) {
//            return resources!!.getString(R.string.wind_south_east)
//        }
//        if (248 <= deg && deg < 293) {
//            return resources!!.getString(R.string.wind_west)
//        }
//        return if (293 <= deg && deg < 338) {
//            resources!!.getString(R.string.wind_north_west)
//        } else String.format(
//            Locale.getDefault(),
//            "%s: %d",
//            resources!!.getString(R.string.incorrect_data),
//            deg
//        )
//    }
//
//    private fun timeToString(unixSeconds: Long, unixSecondsDiff: Long): String {
//        val date = Date(unixSeconds * 1000L)
//        val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
//        return simpleDateFormat.format(date)
//    }
//
//    val imageResource: Int
//        get() {
//            if (weather[0].icon == "01d") return R.drawable.ic_01d
//            if (weather[0].icon == "02d") return R.drawable.ic_02d
//            if (weather[0].icon == "03d") return R.drawable.ic_03d
//            if (weather[0].icon == "04d") return R.drawable.ic_04d
//            if (weather[0].icon == "09d") return R.drawable.ic_09d
//            if (weather[0].icon == "10d") return R.drawable.ic_10d
//            if (weather[0].icon == "11d") return R.drawable.ic_11d
//            if (weather[0].icon == "13d") return R.drawable.ic_13d
//            if (weather[0].icon == "50d") return R.drawable.ic_50d
//            if (weather[0].icon == "01n") return R.drawable.ic_01n
//            if (weather[0].icon == "02n") return R.drawable.ic_02n
//            if (weather[0].icon == "03n") return R.drawable.ic_03n
//            if (weather[0].icon == "04n") return R.drawable.ic_04n
//            if (weather[0].icon == "09n") return R.drawable.ic_09n
//            if (weather[0].icon == "10n") return R.drawable.ic_10n
//            if (weather[0].icon == "11n") return R.drawable.ic_11n
//            if (weather[0].icon == "13n") return R.drawable.ic_13n
//            return if (weather[0].icon == "50n") R.drawable.ic_50n else R.drawable.ic_report_problem
//        }