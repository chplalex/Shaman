package com.chplalex.shaman.utils

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import androidx.core.content.PermissionChecker.checkSelfPermission

const val SP_NAME = "shaman.prefs"
const val TAG = "SHMN"
const val HPAS_IN_ONE_MMHG = 133.3224f / 100 // кПа в одном мм рт.ст.

const val LOCATION_ARG_NAME = "location_name"
const val LOCATION_ARG_COUNTRY = "location_country"
const val LOCATION_ARG_LONGITUDE = "location_longitude"
const val LOCATION_ARG_LATITUDE = "location_latitude"

fun Context.showToast(msg: String) {
    if (Looper.getMainLooper() == Looper.myLooper()) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    } else {
        Handler(Looper.getMainLooper()).post { Toast.makeText(this, msg, Toast.LENGTH_SHORT).show() }
    }
    Log.d(TAG, msg)
}

fun checkLocationPermission(context: Context) =
        (checkSelfPermission(context, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED ||
        checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED)
