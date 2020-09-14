package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOGCAT_TAG, "NetworkChangeReceiver.onReceive()");
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(context, "Network status is changed. Airplane mode is ON", Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, "Network status is changed. Airplane mode is ON");
            return;
        };
        if (networkInfo.isConnected()) {
            Toast.makeText(context, "Network status is changed. Connection is ON", Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, "Network status is changed. Connection is ON");
        } else {
            Log.d(LOGCAT_TAG, "");
            Toast.makeText(context, "Network status is changed. Connection is OFF", Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, "Network status is changed. Connection is OFF");

        };
    }
}
