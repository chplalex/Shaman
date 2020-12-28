package com.chplalex.shaman.Settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.Common.Utils.SP_NAME
import com.chplalex.shaman.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.iid.FirebaseInstanceId

class FragmentSettings : Fragment() {

    private lateinit var checkBoxPressure: MaterialCheckBox
    private lateinit var checkBoxWind: MaterialCheckBox
    private lateinit var checkBoxSunMoving: MaterialCheckBox
    private lateinit var checkBoxHumidity: MaterialCheckBox
    private lateinit var rgTheme: RadioGroup
    private lateinit var txtToken: MaterialTextView
    private lateinit var btnClearPreferences: MaterialButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        sharedPreferences = requireActivity().getSharedPreferences(SP_NAME, MODE_PRIVATE)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setTitle(R.string.label_settings)
        findViewsById(view)
        restoreViewsValueFromSharedPreferences()
        initToken()
        initListenerForCheckButtons()
        initListenerForThemesButtons()
        initListenerForClearPreferencesButton()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_no_start, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_start) {
            NavHostFragment.findNavController(this).navigate(R.id.actionStart, null)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("PushMessage", "getInstanceId failed", task.exception)
                    return@addOnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token
                txtToken.text = token
            }
    }

    private fun findViewsById(view: View) = with(view) {
        checkBoxPressure = findViewById(R.id.checkBoxPressure)
        checkBoxWind = findViewById(R.id.checkBoxWind)
        checkBoxSunMoving = findViewById(R.id.checkBoxSunMoving)
        checkBoxHumidity = findViewById(R.id.checkBoxHumidity)
        rgTheme = findViewById(R.id.rgTheme)
        txtToken = findViewById(R.id.txtToken)
        btnClearPreferences = findViewById(R.id.btnClearPreferences)
    }

    private fun initListenerForCheckButtons() {
        val chkClickListener = View.OnClickListener {
            with(sharedPreferences.edit()) {
                putBoolean("pref_pressure", checkBoxPressure.isChecked)
                putBoolean("pref_wind", checkBoxWind.isChecked)
                putBoolean("pref_sun_moving", checkBoxSunMoving.isChecked)
                putBoolean("pref_humidity", checkBoxHumidity.isChecked)
                apply()
            }
        }
        checkBoxHumidity.setOnClickListener(chkClickListener)
        checkBoxPressure.setOnClickListener(chkClickListener)
        checkBoxSunMoving.setOnClickListener(chkClickListener)
        checkBoxWind.setOnClickListener(chkClickListener)
    }

    private fun initListenerForThemesButtons() = rgTheme.setOnCheckedChangeListener { _, checkedId ->
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

    private fun initListenerForClearPreferencesButton() = btnClearPreferences.setOnClickListener {
        with(sharedPreferences.edit()) {
            clear()
            apply()
            initToken()
            restoreViewsValueFromSharedPreferences()
        }
    }

    private fun restoreViewsValueFromSharedPreferences() {
        clearListeners()

        checkBoxPressure.isChecked = sharedPreferences.getBoolean("pref_pressure", true)
        checkBoxWind.isChecked = sharedPreferences.getBoolean("pref_wind", true)
        checkBoxSunMoving.isChecked = sharedPreferences.getBoolean("pref_sun_moving", true)
        checkBoxHumidity.isChecked = sharedPreferences.getBoolean("pref_humidity", true)

        when (sharedPreferences.getInt("pref_theme", 1)) {
            1 -> rgTheme.check(R.id.rbThemeSystem)
            2 -> rgTheme.check(R.id.rbThemeLight)
            3 -> rgTheme.check(R.id.rbThemeDark)
        }

        initListenerForCheckButtons()
        initListenerForThemesButtons()

        txtToken.text = sharedPreferences.getString("pref_token", "")
    }

    private fun clearListeners() {
        checkBoxHumidity.setOnClickListener(null)
        checkBoxPressure.setOnClickListener(null)
        checkBoxSunMoving.setOnClickListener(null)
        checkBoxWind.setOnClickListener(null)
        rgTheme.setOnCheckedChangeListener(null)
    }
}