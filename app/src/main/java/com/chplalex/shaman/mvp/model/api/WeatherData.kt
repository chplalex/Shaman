package com.chplalex.shaman.mvp.model.api

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

        return timezone == other.timezone &&
            id == other.id &&
            name == other.name &&
            cod == other.cod &&
            coord == other.coord &&
            weather.contentEquals(other.weather) &&
            main == other.main &&
            wind == other.wind &&
            clouds == other.clouds &&
            sys == other.sys
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