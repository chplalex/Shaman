package com.chplalex.shaman.mvp.model

import java.util.Observable

class LocationData(var name: String, var country: String, var lon: Float, var lat: Float) : Observable() {

    constructor() : this("", "", 0f, 0f)

    override fun toString(): String {
        if (isEmpty) {
            return ""
        }
        val fullString = StringBuilder(name)
        if (country.isNotEmpty()) {
            fullString.append(",").append(country)
        }
        return fullString.toString()
    }

    val isEmpty: Boolean
        get() = name.isEmpty() || name == "No data" || name == "Нет данных" || name == "null"

}