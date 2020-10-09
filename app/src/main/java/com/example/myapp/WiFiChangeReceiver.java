package com.example.myapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import static com.example.myapp.Common.Utils.showToast;

public class WiFiChangeReceiver {

    private ConnectivityManager cm;
    private ConnectivityManager.NetworkCallback callback;
    private NetworkRequest request;

    public WiFiChangeReceiver(Context context) {
        cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();

        callback = new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                showToast(context, "wifi ON");
            }

            @Override
            public void onLost(Network network) {
                showToast(context, "wifi OFF");
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
