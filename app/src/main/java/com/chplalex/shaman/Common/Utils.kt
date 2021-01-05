package com.chplalex.shaman.Common

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

object Utils {

    const val SP_NAME = "shaman.prefs"
    const val TAG = "SHMN"
    const val HPAS_IN_ONE_MMHG = 133.3224f / 100 // кПа в одном мм рт.ст.

    // 10 секунд = время ожидания отклика сервера в миллисекундах
    const val READ_TIMEOUT = 10 * 1000
    const val LOCATION_ARG_NAME = "location_name"
    const val LOCATION_ARG_COUNTRY = "location_country"
    const val LOCATION_ARG_LONGITUDE = "location_longitude"
    const val LOCATION_ARG_LATITUDE = "location_latitude"

    fun showToast(context: Context?, msg: String) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        } else {
            Handler(Looper.getMainLooper()).post { Toast.makeText(context, msg, Toast.LENGTH_SHORT).show() }
        }
        Log.d(TAG, msg)
    }
}