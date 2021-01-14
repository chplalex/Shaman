package com.chplalex.shaman.service.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import com.chplalex.shaman.utils.*
import com.chplalex.shaman.mvp.model.api.LocationData
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.io.IOException

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
            name = sp.getString(LOCATION_ARG_NAME, "Москва").toString(),
            country = sp.getString(LOCATION_ARG_COUNTRY, "RU").toString(),
            lon = sp.getFloat(LOCATION_ARG_LONGITUDE, 0.0f),
            lat = sp.getFloat(LOCATION_ARG_LATITUDE, 0.0f)
        )
        if (locationData.isEmpty) {
            return null
        } else {
            return locationData
        }
    }

    fun getFromCurrentLocation(context: Context?): Observable<LocationData> = Observable.create { emitter ->
        if (context == null) {
            emitter.onError(IllegalArgumentException())
            return@create
        }

        if (!checkLocationPermission(context)) {
            emitter.onError(LocationNotFoundException("Нет разрешения на доступ к геоданным."))
            return@create
        }

        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
        if (provider == null) {
            emitter.onError(LocationNotFoundException("Нет активного провайдера геоданных."))
            return@create
        }

        if (Looper.myLooper() == null) Looper.prepare()

        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        decodeLocation(context, lm.getLastKnownLocation(provider))
            .observeOn(Schedulers.io())
            .subscribeOn(Schedulers.io())
            .subscribe({ emitter.onNext(it) }, { emitter.onError(it) })

        kickStart(lm, LocationManager.NETWORK_PROVIDER)
        kickStart(lm, LocationManager.GPS_PROVIDER)

        // потом выполнем более медленную, но и более точную requestLocationUpdates()
        lm.requestLocationUpdates(provider, 0, 0f, object : LocationListener {
            @SuppressLint("CheckResult")
            override fun onLocationChanged(loc: Location) {
                lm.removeUpdates(this)
                decodeLocation(context, loc)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ emitter.onNext(it) }, { emitter.onError(it) })
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        })
    }

    fun getLocation(context: Context, latLng: LatLng): Location {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
        val location = Location(provider)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        return location
    }

    @SuppressLint("MissingPermission")
    private fun kickStart(lm: LocationManager, provider: String) {
        lm.requestLocationUpdates(provider, 0, 0f, object : LocationListener {
            override fun onLocationChanged(location: Location) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
    }

    fun decodeLocation(context: Context?, location: Location?) = Single.create<LocationData> {
        if (context == null || location == null) {
            it.onError(IllegalArgumentException())
            return@create
        }
        val geocoder = Geocoder(context)
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (addresses == null || addresses.isEmpty() || addresses[0].locality == null) {
                it.onError(LocationNotFoundException("Невозможно декодировать текущее местоположение"))
            } else {
                it.onSuccess(
                    LocationData(
                        name = addresses[0].locality, // location name
                        country = addresses[0].countryCode, // location country;
                        lat = location.latitude.toFloat(),
                        lon = location.longitude.toFloat()
                    )
                )
            }
        } catch (e: IOException) {
            it.onError(LocationNotFoundException(e.toString()))
        }
    }
}