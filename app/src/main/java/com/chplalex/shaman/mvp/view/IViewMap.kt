package com.chplalex.shaman.mvp.view

import com.google.android.gms.maps.GoogleMap
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewMap : MvpView {
    fun showErrorLocationService()
    fun showErrorLocation(error: Throwable?)
    fun setMap(onMapReady: (googleMap: GoogleMap) -> Unit)
}