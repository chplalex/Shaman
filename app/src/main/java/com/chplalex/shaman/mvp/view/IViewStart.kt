package com.chplalex.shaman.mvp.view

import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewStart : MvpView {
    fun setLocationName(value: String)
    fun setLocationCountry(value: String)
    fun setTemp(value: Float)
    fun setUncertainTemp()
    fun setWeatherDescription(value: String)
    fun setPressure(value: String)
    fun setWind(value: String)
    fun setSunMoving(value: String)
    fun setHumidity(value: String)

    fun setFavoriteState(state: Boolean)

    fun setPressureVisibility(value: Int)
    fun setWindVisibility(value: Int)
    fun setSunMovingVisibility(value: Int)
    fun setHumidityVisibility(value: Int)
}