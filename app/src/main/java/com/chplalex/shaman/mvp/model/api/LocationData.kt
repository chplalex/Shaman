package com.chplalex.shaman.mvp.model.api

import java.util.Observable

class LocationData(var name: String, var country: String, var lon: Float, var lat: Float) : Observable() {

    fun fullName(): String {
        if (isEmpty) return ""
        val result = StringBuilder(name)
        if (country.isNotEmpty()) result.append(",$country")
        return result.toString()
    }

    override fun toString(): String {
        return "name = \"$name\", country = \"$country\", lon = $lon, lat = $lat"
    }

    val isEmpty: Boolean
        get() = name.isEmpty() || name == "No data" || name == "Нет данных" || name == "null"

}