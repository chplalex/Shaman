package com.chplalex.shaman.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterSettings
import com.chplalex.shaman.mvp.view.IViewSettings
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import moxy.MvpAppCompatFragment
import moxy.ktx.moxyPresenter

class FragmentSettings : MvpAppCompatFragment(), IViewSettings {

    private val presenter by moxyPresenter {
        PresenterSettings()
    }

    private lateinit var checkBoxPressure: MaterialCheckBox
    private lateinit var checkBoxWind: MaterialCheckBox
    private lateinit var checkBoxSunMoving: MaterialCheckBox
    private lateinit var checkBoxHumidity: MaterialCheckBox
    private lateinit var rgTheme: RadioGroup
    private lateinit var btnClearPreferences: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        activity?.setTitle(R.string.label_settings)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViewsById(view)
    }

    private fun findViewsById(view: View) = with(view) {
        checkBoxPressure = findViewById(R.id.checkBoxPressure)
        checkBoxWind = findViewById(R.id.checkBoxWind)
        checkBoxSunMoving = findViewById(R.id.checkBoxSunMoving)
        checkBoxHumidity = findViewById(R.id.checkBoxHumidity)
        rgTheme = findViewById(R.id.rgTheme)
        btnClearPreferences = findViewById(R.id.btnClearPreferences)
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

    override fun setListenerHumidity(listener: ((View) -> Unit)?) {
        checkBoxHumidity.setOnClickListener(listener)
    }

    override fun setListenerPressure(listener: ((View) -> Unit)?) {
        checkBoxPressure.setOnClickListener(listener)
    }

    override fun setListenerSunMoving(listener: ((View) -> Unit)?) {
        checkBoxSunMoving.setOnClickListener(listener)
    }

    override fun setListenerWind(listener: ((View) -> Unit)?) {
        checkBoxWind.setOnClickListener(listener)
    }

    override fun setListenerTheme(listener: ((View, Int) -> Unit)?) {
        rgTheme.setOnCheckedChangeListener(listener)
    }

    override fun setListenerClear(listener: ((View) -> Unit)?) {
        btnClearPreferences.setOnClickListener(listener)
    }

    override fun setPressure(isChecked: Boolean) {
        checkBoxPressure.isChecked = isChecked
    }

    override fun setWind(isChecked: Boolean) {
        checkBoxWind.isChecked = isChecked
    }

    override fun setSunMoving(isChecked: Boolean) {
        checkBoxSunMoving.isChecked = isChecked
    }

    override fun setHumidity(isChecked: Boolean) {
        checkBoxHumidity.isChecked = isChecked
    }

    override fun setTheme(id: Int) {
        rgTheme.check(id)
    }

    override fun recreateActivity() {
        activity?.recreate()
    }
}