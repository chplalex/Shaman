package com.example.myapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils {
    final static boolean DEBUG = true;
    final static String LOGCAT_TAG = "mtvd";

    public static void LogStackTraceElement(Context context) {
        if (BuildConfig.DEBUG) {
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            String msg = stackTraceElements[3].toString();
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, msg);
        }
    }
}
