package com.chplalex.shaman.Start

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_NAME
import com.chplalex.shaman.Common.Utils.showToast
import java.io.IOException
import java.util.Observable
import java.util.Observer

class LocationData : Observable {

    var name: String? = null
    var country: String? = null
    var lon = 0f
    var lat = 0f

    constructor() {}

    constructor(name: String?, country: String?) {
        this.name = name
        this.country = country
    }

    constructor(query: String?) {
        name = query
    }

    override fun toString(): String {
        if (isEmpty) return ""
        val locationFull = StringBuilder(name)
        if (country != null && country!!.length > 0) {
            locationFull.append(",").append(country)
        }
        return locationFull.toString()
    }

    val isEmpty: Boolean
        get() = name == null || name!!.length == 0 || name == "No data" || name == "Нет данных"

    private fun initByNull() {
        name = null
        country = null
    }

    fun initFromBundle(bundle: Bundle?) {
        if (bundle == null) {
            initByNull()
        } else {
            name = bundle.getString(LOCATION_ARG_NAME)
            country = bundle.getString(LOCATION_ARG_COUNTRY)
            if (isEmpty) initByNull()
        }
    }

    fun initFromSharedPreferences(sp: SharedPreferences?) {
        if (sp == null) {
            initByNull()
        } else {
            name = sp.getString("pref_loc_name", null)
            country = sp.getString("pref_loc_country", null)
            if (isEmpty) initByNull()
        }
    }

    fun initFromCurrentLocation(context: Context?, observer: Observer) {
        initByNull()
        val thisInstance: Observable = this
        getCurrentLocation(context) { o, arg ->
            if (arg == null) {
                observer.update(thisInstance, null)
            } else {
                decodeLocation(context, arg as Location, observer)
            }
        }
    }

    // определение координат текущего местоположения
    fun getCurrentLocation(context: Context?, observer: Observer) {
        if (context == null) {
            observer.update(this, null)
            return
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, false)
        if (provider == null) {
            showToast(context, "Нет активного провайдера геоданных. Определение текущего местоположения невозможно")
            observer.update(this, null)
            return
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            showToast(context, "Нет разрешения на доступ к геоданным. Определение текущего местоположения невозможно")
            observer.update(this, null)
            return
        }

        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        val loc = lm.getLastKnownLocation(provider)
        observer.update(this, loc)

        // потом выполнем более медленную, но и более точную requestLocationUpdates()
        val thisInstance: Observable = this
        lm.requestLocationUpdates(provider, 1000, 3f, object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.removeUpdates(this)
                observer.update(thisInstance, location)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        })
    }

    // декодирование координат в название местоположения и страны
    fun decodeLocation(context: Context?, location: Location?, observer: Observer) {
        initByNull()
        if (location == null) {
            observer.update(this, null)
            return
        }
        val thisInstance: Observable = this
        val geocoder = Geocoder(context)
        Thread {
            try {
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                if (addresses == null || addresses.size == 0) {
                    showToast(context, "Текущее местоположение неопределено")
                } else {
                    name = addresses[0].locality // location name
                    country = addresses[0].countryCode // location country;
                }
            } catch (e: IOException) {
                showToast(context, "Ошибка при обращении к геокодеру. Текущее местоположение неопределено")
            }
            observer.update(thisInstance, null)
        }.start()
    }
}