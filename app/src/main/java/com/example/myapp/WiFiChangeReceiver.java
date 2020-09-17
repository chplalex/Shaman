package com.example.myapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.widget.Toast;

public class WiFiChangeReceiver {

    ConnectivityManager cm;
    ConnectivityManager.NetworkCallback callback;
    NetworkRequest request;

    public WiFiChangeReceiver(Context context) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                Toast.makeText(context, "wifi ON", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLost(Network network) {
                Toast.makeText(context, "wifi OFF", Toast.LENGTH_SHORT).show();
            }
        };
    }

    public void registerNetworkCallback() {
        cm.registerNetworkCallback(request, callback);
    }

    public void unregisterNetworkCallback() {
        cm.unregisterNetworkCallback(callback);
    }
}
