package com.chplalex.shaman.ui.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.chplalex.shaman.utils.SP_NAME
import com.chplalex.shaman.R
import com.chplalex.shaman.utils.checkLocationPermission
import com.google.android.material.navigation.NavigationView

private const val PERMISSION_REQUEST_CODE = 10

class MainActivity : AppCompatActivity() {

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences(SP_NAME, MODE_PRIVATE)
        initTheme()
        setContentView(R.layout.activity_main)
        checkAppPermissions()
        initViews()
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
        if (checkLocationPermission(this)) return
        requestPermissions(arrayOf(ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
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
}