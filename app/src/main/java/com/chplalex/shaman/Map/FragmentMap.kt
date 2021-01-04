package com.chplalex.shaman.Map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
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
import com.chplalex.shaman.R
import com.chplalex.shaman.Start.LocationData
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Observable
import java.util.Observer

class FragmentMap : Fragment(), OnMyLocationButtonClickListener, OnMyLocationClickListener, OnMapReadyCallback {

    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Utils.showToast(activity, "У приложения нет права доступа к геолокации")
            return
        }

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.isMyLocationEnabled = true

        googleMap.setOnMapLongClickListener { latLng: LatLng ->
            Log.d(Utils.TAG, "setOnMapLongClickListener(), latLng = $latLng")
            val lm = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            val provider = lm.getBestProvider(criteria, false)
            val location = Location(provider)
            location.latitude = latLng.latitude
            location.longitude = latLng.longitude
//            LocationData().decodeLocation(context, location, Observer { o, _ ->
//                val locationData = o as LocationData
//                val bundle = Bundle()
//                bundle.putString(Utils.LOCATION_ARG_NAME, locationData.name)
//                bundle.putString(Utils.LOCATION_ARG_COUNTRY, locationData.country)
//
//                activity?.runOnUiThread({
//                    NavHostFragment.findNavController((parentFragment)!!)
//                        .navigate(R.id.fragmentStart, bundle)
//                })
//
//            })
        }
//        LocationData().getCurrentLocation(getContext()) { _: Observable?, arg: Any? ->
//            val location = arg as Location?
//            if (arg == null) {
//                Utils.showToast(context, "У приложения нет права доступа к геолокации")
//            } else {
//                val myLatLng = LatLng(location!!.latitude, location.longitude)
//                googleMap.addMarker(MarkerOptions().position(myLatLng).title("My current location"))
//                googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng))
//            }
//        }
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