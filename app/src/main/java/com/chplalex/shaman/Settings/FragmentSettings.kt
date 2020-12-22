package com.chplalex.shaman.Settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.chplalex.shaman.MainActivity
import com.chplalex.shaman.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.iid.FirebaseInstanceId

class FragmentSettings : Fragment() {

    private var checkBoxPressure: MaterialCheckBox? = null
    private var checkBoxWind: MaterialCheckBox? = null
    private var checkBoxSunMoving: MaterialCheckBox? = null
    private var checkBoxHumidity: MaterialCheckBox? = null
    private var rbThemeSystem: MaterialRadioButton? = null
    private var rbThemeLight: MaterialRadioButton? = null
    private var rbThemeDark: MaterialRadioButton? = null
    private var txtToken: MaterialTextView? = null
    private var btnClearPreferences: MaterialButton? = null
    private var sharedPreferences: SharedPreferences? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val activity = activity as MainActivity?
        sharedPreferences = activity!!.sharedPreferences
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
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("PushMessage", "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new Instance ID token
                val token = task.result!!.token
                txtToken!!.text = token
            })
    }

    private fun findViewsById(view: View) {
        checkBoxPressure = view.findViewById(R.id.checkBoxPressure)
        checkBoxWind = view.findViewById(R.id.checkBoxWind)
        checkBoxSunMoving = view.findViewById(R.id.checkBoxSunMoving)
        checkBoxHumidity = view.findViewById(R.id.checkBoxHumidity)
        rbThemeSystem = view.findViewById(R.id.rbThemeSystem)
        rbThemeLight = view.findViewById(R.id.rbThemeLight)
        rbThemeDark = view.findViewById(R.id.rbThemeDark)
        txtToken = view.findViewById(R.id.txtToken)
        btnClearPreferences = view.findViewById(R.id.btnClearPreferences)
    }

    private fun initListenerForCheckButtons() {
        val chkClickListener = View.OnClickListener {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(getString(R.string.pref_pressure), checkBoxPressure!!.isChecked)
            editor.putBoolean(getString(R.string.pref_wind), checkBoxWind!!.isChecked)
            editor.putBoolean(getString(R.string.pref_sun_moving), checkBoxSunMoving!!.isChecked)
            editor.putBoolean(getString(R.string.pref_humidity), checkBoxHumidity!!.isChecked)
            editor.apply()
        }
        checkBoxHumidity!!.setOnClickListener(chkClickListener)
        checkBoxPressure!!.setOnClickListener(chkClickListener)
        checkBoxSunMoving!!.setOnClickListener(chkClickListener)
        checkBoxWind!!.setOnClickListener(chkClickListener)
    }

    private fun initListenerForThemesButtons() {
        val rbClickListener = View.OnClickListener {
            val editor = sharedPreferences!!.edit()
            editor.putBoolean(getString(R.string.pref_theme_system), rbThemeSystem!!.isChecked)
            editor.putBoolean(getString(R.string.pref_theme_light), rbThemeLight!!.isChecked)
            editor.putBoolean(getString(R.string.pref_theme_dark), rbThemeDark!!.isChecked)
            editor.apply()
            activity?.recreate()
        }
        rbThemeSystem!!.setOnClickListener(rbClickListener)
        rbThemeLight!!.setOnClickListener(rbClickListener)
        rbThemeDark!!.setOnClickListener(rbClickListener)
    }

    private fun initListenerForClearPreferencesButton() {
        btnClearPreferences!!.setOnClickListener {
            val editor = sharedPreferences!!.edit()
            editor.clear()
            editor.apply()
            initToken()
            restoreViewsValueFromSharedPreferences()
        }
    }

    private fun restoreViewsValueFromSharedPreferences() {
        checkBoxPressure!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_pressure), true)
        checkBoxWind!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_wind), true)
        checkBoxSunMoving!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_sun_moving), true)
        checkBoxHumidity!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_humidity), true)
        rbThemeSystem!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_theme_system), true)
        rbThemeLight!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_theme_light), false)
        rbThemeDark!!.isChecked = sharedPreferences!!.getBoolean(getString(R.string.pref_theme_dark), false)
        txtToken!!.text = sharedPreferences!!.getString(getString(R.string.pref_token), "")
    }
}