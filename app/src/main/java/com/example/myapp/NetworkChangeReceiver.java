package com.example.myapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(context, "Network status is changed. Airplane mode is ON", Toast.LENGTH_SHORT).show();
            return;
        };
        if (networkInfo.isConnected()) {
            Toast.makeText(context, "Network status is changed. Connection is ON", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Network status is changed. Connection is OFF", Toast.LENGTH_SHORT).show();
        };
    }
}
