package com.example.myapp;

import android.content.Context;
import android.util.Log;

public class Utils {
    final static boolean DEBUG = true;
    final static String LOGCAT_TAG = "mtvd";
    final static int REQUEST_FOR_SETTINGS = 1001;
    final static String SETTINGS_KEY = "settings_key";
    final static String WEATHER_UPDATE_KEY = "weather_update_key";
    final static String LOG_D_TAG = "mtvd";

    public static void LogStackTraceElement(Context context) {
        if (BuildConfig.DEBUG) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            String msg = stackTraceElements[3].toString();
            Log.d(LOGCAT_TAG, msg);
        }
    }
}
