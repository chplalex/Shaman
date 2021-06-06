package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterAbout
import com.chplalex.shaman.utils.*
import com.chplalex.shaman.mvp.presenter.PresenterMap
import com.chplalex.shaman.mvp.view.IViewMap
import com.chplalex.shaman.ui.App
import com.chplalex.shaman.ui.App.Companion.instance
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider

class FragmentMap : MvpAppCompatFragment(R.layout.fragment_map), IViewMap {

    @Inject
    lateinit var injectPresenter: Provider<PresenterMap>

    private val presenter by moxyPresenter {
        injectPresenter.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        instance.activityComponent?.inject(this)
        super.onCreate(savedInstanceState)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_map)
        setHasOptionsMenu(true)
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