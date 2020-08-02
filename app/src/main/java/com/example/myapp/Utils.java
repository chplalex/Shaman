package com.example.myapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class Utils {
    final static boolean DEBUG = true;
    final static String LOGCAT_TAG = "mtvd";

    public static void MyLog(Context context, String msg) {
        if (BuildConfig.DEBUG) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, msg);
        }
    }
}
