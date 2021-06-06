package com.chplalex.shaman.mvp.presenter

import android.content.SharedPreferences
import android.view.View
import androidx.navigation.NavController
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.view.IViewSettings
import com.google.android.material.checkbox.MaterialCheckBox
import moxy.MvpPresenter
import javax.inject.Inject

class PresenterSettings @Inject constructor(
    private val sp: SharedPreferences,
    private val navController: NavController
) :
    MvpPresenter<IViewSettings>() {

    override fun attachView(view: IViewSettings?) {
        super.attachView(view)
        initListeners(byNull = true)
        initFromSharedPreferences()
        initListeners(byNull = false)
    }

    private fun initFromSharedPreferences() {
        viewState.setPressure(sp.getBoolean("pref_pressure", true))
        viewState.setWind(sp.getBoolean("pref_wind", true))
        viewState.setSunMoving(sp.getBoolean("pref_sun_moving", true))
        viewState.setHumidity(sp.getBoolean("pref_humidity", true))
        viewState.setTheme(
                when (sp.getInt("pref_theme", 1)) {
                    1 -> R.id.rbThemeSystem
                    2 -> R.id.rbThemeLight
                    3 -> R.id.rbThemeDark
                    else -> R.id.rbThemeSystem
                }
        )
    }

    private val themeListener = fun(_: View, checkedId: Int) {
        var themeId = 0
        when (checkedId) {
            R.id.rbThemeSystem -> themeId = 1
            R.id.rbThemeLight -> themeId = 2
            R.id.rbThemeDark -> themeId = 3
        }
        with(sp.edit()) {
            putInt("pref_theme", themeId)
            apply()
        }
        initListeners(byNull = true)
        viewState.recreateActivity()
        initListeners(byNull = false)
    }

    private val clearListener = fun(_: View) {
        with(sp.edit()) {
            clear()
            apply()
        }
        viewState.recreateActivity()
    }

    private val boxListenerPressure = fun(view: View) {
        view as MaterialCheckBox
        with(sp.edit()) {
            putBoolean("pref_pressure", view.isChecked)
            apply()
        }
    }

    private val boxListenerWind = fun(view: View) {
        view as MaterialCheckBox
        with(sp.edit()) {
            putBoolean("pref_wind", view.isChecked)
            apply()
        }
    }

    private val boxListenerSunMoving = fun(view: View) {
        view as MaterialCheckBox
        with(sp.edit()) {
            putBoolean("pref_sun_moving", view.isChecked)
            apply()
        }
    }

    private val boxListenerHumidity = fun(view: View) {
        view as MaterialCheckBox
        with(sp.edit()) {
            putBoolean("pref_humidity", view.isChecked)
            apply()
        }
    }

    private fun initListeners(byNull: Boolean) {
        if (byNull) {
            viewState.setListenerHumidity(null)
            viewState.setListenerPressure(null)
            viewState.setListenerSunMoving(null)
            viewState.setListenerWind(null)
            viewState.setListenerTheme(null)
            viewState.setListenerClear(null)
        } else {
            viewState.setListenerHumidity(boxListenerHumidity)
            viewState.setListenerPressure(boxListenerPressure)
            viewState.setListenerSunMoving(boxListenerSunMoving)
            viewState.setListenerWind(boxListenerWind)
            viewState.setListenerTheme(themeListener)
            viewState.setListenerClear(clearListener)
        }
    }

    fun onActionStart() {
        navController.navigate(R.id.actionStart, null)
    }
}