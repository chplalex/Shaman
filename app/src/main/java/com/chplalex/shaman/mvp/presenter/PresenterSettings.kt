package com.chplalex.shaman.mvp.presenter

import android.content.SharedPreferences
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.view.IViewSettings
import com.google.android.material.checkbox.MaterialCheckBox
import moxy.MvpPresenter

class PresenterSettings(private val sharedPreferences: SharedPreferences) : MvpPresenter<IViewSettings>() {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        initFromSharedPreferences()
        initListeners()
    }

    private fun initFromSharedPreferences() {
        viewState.setPressure(sharedPreferences.getBoolean("pref_pressure", true))
        viewState.setWind(sharedPreferences.getBoolean("pref_wind", true))
        viewState.setSunMoving(sharedPreferences.getBoolean("pref_sun_moving", true))
        viewState.setHumidity(sharedPreferences.getBoolean("pref_humidity", true))
        viewState.setTheme(
                when (sharedPreferences.getInt("pref_theme", 1)) {
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
        with(sharedPreferences.edit()) {
            putInt("pref_theme", themeId)
            apply()
        }
    }

    private val clearListener = fun(_: View) {
        with(sharedPreferences.edit()) {
            clear()
            apply()
        }
        initFromSharedPreferences()
    }

    private val boxListenerPressure = fun(view: View) {
        view as MaterialCheckBox
        with(sharedPreferences.edit()) {
            putBoolean("pref_pressure", view.isChecked)
            apply()
        }
    }

    private val boxListenerWind = fun(view: View) {
        view as MaterialCheckBox
        with(sharedPreferences.edit()) {
            putBoolean("pref_wind", view.isChecked)
            apply()
        }
    }

    private val boxListenerSunMoving = fun(view: View) {
        view as MaterialCheckBox
        with(sharedPreferences.edit()) {
            putBoolean("pref_sun_moving", view.isChecked)
            apply()
        }
    }

    private val boxListenerHumidity = fun(view: View) {
        view as MaterialCheckBox
        with(sharedPreferences.edit()) {
            putBoolean("pref_humidity", view.isChecked)
            apply()
        }
    }

    private fun initListeners() {
        viewState.setListenerHumidity(boxListenerHumidity)
        viewState.setListenerPressure(boxListenerPressure)
        viewState.setListenerSunMoving(boxListenerSunMoving)
        viewState.setListenerWind(boxListenerWind)
        viewState.setListenerTheme(themeListener)
        viewState.setListenerClear(clearListener)
    }

    fun onActionStart(fragment: Fragment) {
        NavHostFragment.findNavController(fragment).navigate(R.id.actionStart, null)
    }
}