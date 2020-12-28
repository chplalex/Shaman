package com.chplalex.shaman.ui

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.chplalex.shaman.Common.Utils.SP_NAME
import com.chplalex.shaman.Common.Utils.TAG
import com.chplalex.shaman.R
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        initTheme()
        setContentView(R.layout.activity_main)
        initToken()
        checkAppPermissions()
        initViews()
    }

    private fun initToken() = FirebaseInstanceId.getInstance().instanceId
        .addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }
            task.result?.let { ir ->
                with(sharedPreferences.edit()) {
                    putString("pref_token", ir.token)
                    apply()
                }
            }
        }

    private fun initViews() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(navigationView, navController)
        with(
            ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
            )
        )
        {
            drawerLayout.addDrawerListener(this)
            isDrawerIndicatorEnabled = true
            syncState()
        }
    }

    private fun initTheme() = when (sharedPreferences.getInt("pref_theme", 1)) {
        1 -> setTheme(R.style.AppThemeSystem)
        2 -> setTheme(R.style.AppThemeLight)
        3 -> setTheme(R.style.AppThemeDark)
        else -> setTheme(R.style.AppThemeSystem)
    }

    private fun checkAppPermissions() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) return

        ActivityCompat.requestPermissions(
            this, arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.size == 2 &&
                (grantResults[0] == PERMISSION_GRANTED || grantResults[1] == PERMISSION_GRANTED))
                recreate()
            else
                finish()
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 10
    }
}