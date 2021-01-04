package com.chplalex.shaman.mvp.service

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.chplalex.shaman.Common.Utils
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LATITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LONGITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_NAME
import com.chplalex.shaman.Common.Utils.showToast
import com.chplalex.shaman.Start.LocationData
import java.io.IOException
import java.util.Observer

object LocationService {

    //TODO: инжекция context

    fun getFromBundle(bundle: Bundle?): LocationData? {
        if (bundle == null) return null
        val locationData = LocationData(
            name = bundle.getString(LOCATION_ARG_NAME).toString(),
            country = bundle.getString(LOCATION_ARG_COUNTRY).toString(),
            lon = bundle.getFloat(LOCATION_ARG_LONGITUDE),
            lat = bundle.getFloat(LOCATION_ARG_LATITUDE)
        )
        if (locationData.isEmpty) {
            return null
        } else {
            return locationData
        }
    }

    fun getFromSharedPreferences(sp: SharedPreferences?): LocationData? {
        if (sp == null) return null
        val locationData = LocationData(
            name = sp.getString(LOCATION_ARG_NAME, "").toString(),
            country = sp.getString(LOCATION_ARG_COUNTRY, "").toString(),
            lon = sp.getFloat(LOCATION_ARG_LONGITUDE, 0.0f),
            lat = sp.getFloat(LOCATION_ARG_LATITUDE, 0.0f)
        )
        if (locationData.isEmpty) {
            return null
        } else {
            return locationData
        }
    }

    fun getFromCurrentLocation(context: Context?, observer: Observer): LocationData {
        val locationData = LocationData()
        getCurrentLocation(locationData, context) { o, arg ->
            if (arg == null) {
                observer.update(locationData, null)
            } else {
                decodeLocation(locationData, context, arg as Location, observer)
            }
        }
        return locationData
    }

    // определение координат текущего местоположения
    private fun getCurrentLocation(locationData: LocationData, context: Context?, observer: Observer) {
        if (context == null) {
            observer.update(locationData, null)
            return
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, false)
        if (provider == null) {
            showToast(context, "Нет активного провайдера геоданных. Определение текущего местоположения невозможно")
            observer.update(locationData, null)
            return
        }
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            showToast(context, "Нет разрешения на доступ к геоданным. Определение текущего местоположения невозможно")
            observer.update(locationData, null)
            return
        }
        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        val loc = lm.getLastKnownLocation(provider)
        observer.update(locationData, loc)

        // потом выполнем более медленную, но и более точную requestLocationUpdates()
        lm.requestLocationUpdates(provider, 1000, 3f, object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.removeUpdates(this)
                observer.update(locationData, loc)
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        })
    }

    // декодирование координат в название местоположения и страны
    fun decodeLocation(locationData: LocationData, context: Context?, location: Location?, observer: Observer) {
        if (location == null) {
            observer.update(locationData, null)
            return
        }
        val geocoder = Geocoder(context)
        Thread {
            try {
                val addresses = geocoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )
                if (addresses == null || addresses.isEmpty()) {
                    showToast(context, "Текущее местоположение неопределено")
                } else {
                    with(locationData) {
                        name = addresses[0].locality // location name
                        country = addresses[0].countryCode // location country;
                        lat = location.latitude.toFloat()
                        lon = location.longitude.toFloat()
                    }
                }
            } catch (e: IOException) {
                showToast(context, "Ошибка при обращении к геокодеру. Текущее местоположение неопределено")
            }
            observer.update(locationData, null)
        }.start()
    }
}