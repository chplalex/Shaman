package com.chplalex.shaman.mvp.presenter

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.utils.LOCATION_ARG_NAME
import com.chplalex.shaman.utils.checkLocationPermission
import com.chplalex.shaman.service.location.LocationService
import com.chplalex.shaman.mvp.view.IViewMap
import com.chplalex.shaman.utils.TAG
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import moxy.MvpPresenter
import javax.inject.Inject
import javax.inject.Named

class PresenterMap @Inject constructor(
    private val sp: SharedPreferences,
    private val disposable: CompositeDisposable,
    private val navController: NavController,
    @Named("actContext")
    private val context: Context,
    @Named("UI")
    private val uiScheduler: Scheduler,
    @Named("IO")
    private val ioScheduler: Scheduler
) :
    MvpPresenter<IViewMap>() {

    override fun onDestroy() {
        super.onDestroy()
        disposable.dispose()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setMap(this::onMapReady)
    }

    fun onActionStart() {
        navController.navigate(R.id.actionStart, null)
    }

    private fun onMapReady(googleMap: GoogleMap) {
        if (!checkLocationPermission(context)) {
            viewState.showErrorLocationService()
            return
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            disposable.add(
                LocationService.decodeLocation(context, LocationService.getLocation(context, latLng))
                    .subscribeOn(ioScheduler)
                    .observeOn(uiScheduler)
                    .subscribe(
                        { locationData ->
                            with(sp.edit()) {
                                putString(LOCATION_ARG_NAME, locationData.name)
                                putString(LOCATION_ARG_COUNTRY, locationData.country)
                                apply()

                            }
                            navController.navigate(R.id.actionStart, null)
                        },
                        { error ->
                            viewState.showErrorLocation(error)
                        }
                    ))
        }

        disposable.add(
            LocationService.getFromCurrentLocation(context)
                .subscribeOn(ioScheduler)
                .observeOn(uiScheduler)
                .subscribe(
                    { locationData ->
                        Log.d(TAG, "LocationService.getFromCurrentLocation(context), locationData = $locationData")
                        val myLatLng = LatLng(locationData.lat.toDouble(), locationData.lon.toDouble())
                        googleMap.addMarker(MarkerOptions().position(myLatLng).title("My current location"))
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
                    },
                    { error ->
                        Log.d(TAG, "LocationService.getFromCurrentLocation(context), error = $error")
                        viewState.showErrorLocation(error)
                    }
                ))
    }
}