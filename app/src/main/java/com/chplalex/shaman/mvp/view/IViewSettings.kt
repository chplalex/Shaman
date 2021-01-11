package com.chplalex.shaman.mvp.view

import android.view.View
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle

@AddToEndSingle
interface IViewSettings : MvpView {
    fun setListenerHumidity(listener: ((View) -> Unit)?)
    fun setListenerPressure(listener: ((View) -> Unit)?)
    fun setListenerSunMoving(listener: ((View) -> Unit)?)
    fun setListenerWind(listener: ((View) -> Unit)?)
    fun setListenerClear(listener: ((View) -> Unit)?)
    fun setListenerTheme(listener: ((View, Int) -> Unit)?)
    fun setPressure(isChecked: Boolean)
    fun setWind(isChecked: Boolean)
    fun setSunMoving(isChecked: Boolean)
    fun setHumidity(isChecked: Boolean)
    fun setTheme(id: Int)
    fun recreateActivity()
}