package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.*
import com.chplalex.shaman.mvp.presenter.PresenterMap
import com.chplalex.shaman.mvp.view.IViewMap
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FragmentMap : MvpAppCompatFragment(), IViewMap {

    private val presenter by moxyPresenter {
        PresenterMap()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_no_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_start) {
            presenter.onActionStart()
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
        activity?.setTitle(R.string.label_map)
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun showErrorLocationService() {
        context?.showToast("У приложения нет доступа к геолокации")
    }

    override fun showErrorLocation(error: Throwable?) {
        context?.showToast("Ошибка определения текущего местоположения: $error")
    }

    override fun setMap(onMapReady: (googleMap: GoogleMap) -> Unit) {
        val mapFragment = childFragmentManager.findFragmentById(R.id.fragmentMap) as SupportMapFragment
        mapFragment.getMapAsync(onMapReady)
    }
}