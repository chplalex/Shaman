package com.example.myapp.Start;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.myapp.R;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;

public class LocationData {
    public String name;
    public String country;

    public LocationData() {
    }

    public LocationData(String name, String country) {
        this.name = name;
        this.country = country;
    }

    public LocationData(String query) {
        this.name = query;
    }

    @Override
    public String toString() {
        if (isEmpty()) return "";
        StringBuilder locationFull = new StringBuilder(name);
        if (country != null && country.length() > 0) {
            locationFull.append(",").append(country);
        }
        return locationFull.toString();
    }

    public boolean isEmpty() {
        return (name == null || name.length() == 0);
    }

    private void initByNull() {
        name = null;
        country = null;
    }

    public void initFromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            initByNull();
        } else {
            name = bundle.getString(LOCATION_ARG_NAME);
            country = bundle.getString(LOCATION_ARG_COUNTRY);
            if (isEmpty()) initByNull();
        }
    }

    public void initFromSharedPreferences(@Nullable Context context) {
        if (context == null) {
            initByNull();
        } else {
            String resFileName = context.getApplicationContext().getResources().getString(R.string.file_name_prefs);
            SharedPreferences sp = context.getSharedPreferences(resFileName, Context.MODE_PRIVATE);
            name = sp.getString("pref_loc_name", null);
            country = sp.getString("pref_loc_country", null);
            if (isEmpty()) initByNull();
        }
    }

    public void initFromCurrentLocation(@Nullable Context context, LocationListener listener) {
        if (context == null) return;
        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            Toast.makeText(context, "Нет активного провайдера геоданных. Определение текущего местоположения невозможно", Toast.LENGTH_SHORT).show();
            initByNull();
            return;
        }
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Нет разрешения на доступ к геоданным. Определение текущего местоположения невозможно", Toast.LENGTH_SHORT).show();
            initByNull();return;
        }
        lm.requestLocationUpdates(provider, 10000, 1000, listener);
    }
}