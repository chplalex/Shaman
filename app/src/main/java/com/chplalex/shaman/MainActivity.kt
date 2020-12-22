package com.chplalex.shaman

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.chplalex.shaman.Common.Utils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.iid.FirebaseInstanceId

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var navigationView: NavigationView
    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var wifiChangeReceiver: WiFiChangeReceiver
    @JvmField var sharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiChangeReceiver = WiFiChangeReceiver(applicationContext)
        sharedPreferences = getSharedPreferences(resources.getString(R.string.file_name_prefs), MODE_PRIVATE)
        initTheme()
        setContentView(R.layout.activity_main)
        initToken()
        initNotificationChannel()
        requestPermissions()
        initViews()
    }

    override fun onResume() {
        super.onResume()
        wifiChangeReceiver.registerNetworkCallback()
    }

    override fun onPause() {
        super.onPause()
        wifiChangeReceiver.unregisterNetworkCallback()
    }

    private fun initToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.d(Utils.LOGCAT_TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                val token = task.result!!.token
                val editor = sharedPreferences!!.edit()
                editor.putString("pref_token", token)
                editor.apply()
            })
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel("2", "name", importance)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun initViews() {
        navigationView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.nav_view)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        drawerLayout = findViewById(R.id.drawer_layout)
        setSupportActionBar(toolbar)
        NavigationUI.setupWithNavController(navigationView, navController)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
    }

    private fun initTheme() {
        val resources = resources
        if (sharedPreferences!!.getBoolean(resources.getString(R.string.pref_theme_system), true)) {
            setTheme(R.style.AppThemeSystem)
        }
        if (sharedPreferences!!.getBoolean(resources.getString(R.string.pref_theme_light), false)) {
            setTheme(R.style.AppThemeLight)
        }
        if (sharedPreferences!!.getBoolean(resources.getString(R.string.pref_theme_dark), false)) {
            setTheme(R.style.AppThemeDark)
        }
    }

    private fun requestPermissions() {
        // Проверим, есть ли Permission’ы, и если их нет, запрашиваем их у пользователя
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            Log.d(Utils.LOGCAT_TAG, "Permissions is granted")
        } else {
            // Permission’ов нет, запрашиваем их у пользователя
            requestLocationPermissions()
        }
    }

    // Запрашиваем Permission’ы для геолокации
    private fun requestLocationPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
            Log.d(Utils.LOGCAT_TAG, "Can't request permissions")
        } else {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    // Результат запроса Permission’а у пользователя:
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Запрошенный нами Permission
            if (grantResults.size == 2 &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED ||
                    grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Log.d(Utils.LOGCAT_TAG, "Permissions is granted")
                recreate()
            } else {
                Log.d(Utils.LOGCAT_TAG, "Permissions is not granted")
                finish()
            }
        }
    }

    companion object {

        private const val PERMISSION_REQUEST_CODE = 10
    }
}