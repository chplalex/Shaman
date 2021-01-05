package com.chplalex.shaman.mvp.service

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LATITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_LONGITUDE
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_NAME
import com.chplalex.shaman.Common.Utils.TAG
import com.chplalex.shaman.Common.Utils.showToast
import com.chplalex.shaman.mvp.model.LocationData
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    fun getFromCurrentLocationRX(context: Context?): Observable<LocationData> = Observable.create { emitter ->
        if (context == null) {
            emitter.onError(LocationNotFoundException())
            return@create
        }

        val lm = context.getSystemService(LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
        if (provider == null) {
            showToast(context, "Нет активного провайдера геоданных. Определение текущего местоположения невозможно")
            emitter.onError(LocationNotFoundException())
            return@create
        }

        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            showToast(context, "Нет разрешения на доступ к геоданным. Определение текущего местоположения невозможно")
            emitter.onError(LocationNotFoundException())
            return@create
        }

        if (Looper.myLooper() == null) Looper.prepare()

        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        decodeLocationRX(context, lm.getLastKnownLocation(provider))
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
                decodeLocationRX(context, loc)
                    .observeOn(Schedulers.io())
                    .subscribeOn(Schedulers.io())
                    .subscribe({ emitter.onNext(it) }, { emitter.onError(it) })
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        })
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

    // определение координат текущего местоположения
    private fun getCurrentLocation(locationData: LocationData, context: Context?, observer: Observer) {
        if (context == null) {
            observer.update(locationData, null)
            return
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
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
        Log.d(TAG, "1.")
        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        val loc = lm.getLastKnownLocation(provider)
        Log.d(TAG, "1. ${loc?.longitude}, ${loc?.latitude}")
        observer.update(locationData, loc)

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, object : LocationListener {
            override fun onLocationChanged(location: Location) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object : LocationListener {
            override fun onLocationChanged(location: Location) {}
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })

        Log.d(TAG, "2. provider = $provider")
        // потом выполнем более медленную, но и более точную requestLocationUpdates()
        lm.requestLocationUpdates(provider, 0, 0f, object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                Log.d(TAG, "2. ${loc.longitude}, ${loc.latitude}")
                lm.removeUpdates(this)
                observer.update(locationData, loc)
            }

            override fun onProviderEnabled(provider: String) {
                Log.d(TAG, "onProviderEnabled()")
            }

            override fun onProviderDisabled(provider: String) {
                Log.d(TAG, "onProviderDisabled()")
            }

            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                Log.d(TAG, "onStatusChanged()")
            }
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

    fun decodeLocationRX(context: Context?, location: Location?) = Single.create<LocationData> {
        if (context == null || location == null) {
            it.onError(LocationNotFoundException())
            return@create
        }
        val geocoder = Geocoder(context)
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                1
            )
            if (addresses == null || addresses.isEmpty()) {
                showToast(context, "Текущее местоположение неопределено")
                it.onError(LocationNotFoundException())
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
            showToast(context, "Ошибка при обращении к геокодеру. Текущее местоположение неопределено")
            it.onError(e)
        }
    }


}