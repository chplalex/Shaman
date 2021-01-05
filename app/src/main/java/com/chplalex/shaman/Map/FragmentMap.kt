package com.chplalex.shaman.Map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.Common.Utils
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_COUNTRY
import com.chplalex.shaman.Common.Utils.LOCATION_ARG_NAME
import com.chplalex.shaman.Common.Utils.showToast
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.service.LocationService
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FragmentMap : Fragment(), OnMyLocationButtonClickListener, OnMyLocationClickListener, OnMapReadyCallback {

    @SuppressLint("CheckResult")
    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(requireContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            showToast(context, "У приложения нет права доступа к геолокации")
            return
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            LocationService.decodeLocation(context, getLocation(latLng))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                            { locationData ->
                                Bundle().also {
                                    it.putString(LOCATION_ARG_NAME, locationData.name)
                                    it.putString(LOCATION_ARG_COUNTRY, locationData.country)
                                    NavHostFragment.findNavController(this).navigate(R.id.fragmentStart, it)
                                }

                            },
                            { error ->
                                showToast(context, "Ошибка определения текущего местоположения: $error")
                            }
                    )
        }

        LocationService.getFromCurrentLocation(context)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        { locationData ->
                            val myLatLng = LatLng(locationData.lat.toDouble(), locationData.lon.toDouble())
                            googleMap.addMarker(MarkerOptions().position(myLatLng).title("My current location"))
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
                        },
                        { error ->
                            showToast(context, "Ошибка определения текущего местоположения: $error")
                        }
                )
    }

    private fun getLocation(latLng: LatLng): Location {
        val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        val provider = lm.getBestProvider(criteria, true)
        val location = Location(provider)
        location.latitude = latLng.latitude
        location.longitude = latLng.longitude
        return location
    }

    override fun onMyLocationClick(location: Location) {
        Log.d(Utils.TAG, "Current location:\n$location")
    }

    override fun onMyLocationButtonClick(): Boolean {
        Log.d(Utils.TAG, "MyLocation button clicked")
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_no_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        Log.d(Utils.TAG, "onOptionsItemSelected(), MenuItem = $item")
        if (item.itemId == R.id.action_start) {
            NavHostFragment.findNavController(this).navigate(R.id.actionStart, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_map)
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment?
        mapFragment?.getMapAsync { googleMap: GoogleMap -> onMapReady(googleMap) }
    }
}