package com.chplalex.shaman.ui.activity

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.SharedPreferences
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.ui.NavigationUI
import com.chplalex.shaman.R
import com.chplalex.shaman.mvp.presenter.PresenterMain
import com.chplalex.shaman.mvp.view.IViewMain
import com.chplalex.shaman.ui.App.Companion.instance
import com.chplalex.shaman.utils.checkLocationPermission
import com.google.android.material.navigation.NavigationView
import dagger.Lazy
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject

private const val PERMISSION_REQUEST_CODE = 10

class MainActivity : MvpAppCompatActivity(), IViewMain {

    @Inject lateinit var sp: SharedPreferences
    @Inject lateinit var navController : Lazy<NavController>

    private val presenter by moxyPresenter {
        PresenterMain()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance.createActivityComponent(this)
        instance.activityComponent?.inject(this)

        initTheme()
        setContentView(R.layout.activity_main)
        checkAppPermissions()
        initViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        instance.destroyActivityComponent()
    }

    private fun initViews() {
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)

        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(navigationView, navController.get() )
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

    private fun initTheme() = when (sp.getInt("pref_theme", 1)) {
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