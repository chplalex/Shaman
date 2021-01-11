package com.chplalex.shaman.mvp.presenter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.utils.LOCATION_ARG_NAME
import com.chplalex.shaman.utils.checkLocationPermission
import com.chplalex.shaman.service.location.LocationService
import com.chplalex.shaman.mvp.view.IViewMap
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.MvpPresenter

class PresenterMap(private val fragment: Fragment) : MvpPresenter<IViewMap>() {

    private val context = fragment.requireContext()
    private val disposable = CompositeDisposable()

    override fun onDestroy() {
        super.onDestroy()
        disposable.clear()
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setMap(this::onMapReady)
    }

    fun onActionStart() {
        NavHostFragment.findNavController(fragment).navigate(R.id.actionStart, null)
    }

    private fun onMapReady(googleMap: GoogleMap) {
        if (!checkLocationPermission(context)) {
            viewState.showErrorLocationService()
            return
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            disposable.add(LocationService.decodeLocation(context, LocationService.getLocation(context, latLng))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { locationData ->
                                Bundle().also {
                                    it.putString(LOCATION_ARG_NAME, locationData.name)
                                    it.putString(LOCATION_ARG_COUNTRY, locationData.country)
                                    NavHostFragment.findNavController(fragment).navigate(R.id.fragmentStart, it)
                                }

                            },
                            { error ->
                                viewState.showErrorLocation(error)
                            }
                    ))
        }

        disposable.add(LocationService.getFromCurrentLocation(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { locationData ->
                            val myLatLng = LatLng(locationData.lat.toDouble(), locationData.lon.toDouble())
                            googleMap.addMarker(MarkerOptions().position(myLatLng).title("My current location"))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
                        },
                        { error ->
                            viewState.showErrorLocation(error)
                        }
                ))
    }
}