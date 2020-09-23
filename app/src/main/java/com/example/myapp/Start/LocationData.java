package com.example.myapp.Start;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.example.myapp.R;

import java.io.IOException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;
import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class LocationData extends Observable {
    public String name;
    public String country;
    public float lon;
    public float lat;

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
        return (name == null || name.length() == 0 || name.equals("No data") || name.equals("Нет данных"));
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

    public void initFromSharedPreferences(@Nullable SharedPreferences sp) {
        if (sp == null) {
            initByNull();
        } else {
            name = sp.getString("pref_loc_name", null);
            country = sp.getString("pref_loc_country", null);
            if (isEmpty()) initByNull();
        }
    }

    public void initFromCurrentLocation(@Nullable Context context, Observer observer) {
        initByNull();

        getCurrentLocation(context, new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                decodeLocation(context, (Location) arg, observer);
            }
        });

    }

    // определение координат текущего местоположения
    public void getCurrentLocation(@Nullable Context context, Observer observer) {
        if (context == null) {
            Log.d(LOGCAT_TAG, Thread.currentThread().toString());
            observer.update(this, null);
            return;
        }

        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = lm.getBestProvider(criteria, false);
        if (provider == null) {
            Toast.makeText(context, "Нет активного провайдера геоданных. Определение текущего местоположения невозможно", Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, Thread.currentThread().toString());
            observer.update(this, null);
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Нет разрешения на доступ к геоданным. Определение текущего местоположения невозможно", Toast.LENGTH_SHORT).show();
            Log.d(LOGCAT_TAG, Thread.currentThread().toString());
            observer.update(this, null);
            return;
        }

        // вначале выполняем более быструю, но менее точную операцию getLastKnownLocation()
        observer.update(this, lm.getLastKnownLocation(provider));

        // потом выполнем более медленную, но и более точную requestLocationUpdates()
        Observable thisInstance = this;
        lm.requestLocationUpdates(provider, 1000, 3, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.d(LOGCAT_TAG, "lm.requestLocationUpdates() -> onLocationChanged(" + location.toString() + ") -> decodeLocation()");
                LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                lm.removeUpdates(this);
                observer.update(thisInstance, location);
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d(LOGCAT_TAG, "lm.requestLocationUpdates() -> onStatusChanged(" + provider + ")");
            }
        });

    };

    // декодирование координат в название местоположения и страны
    public void decodeLocation(Context context, Location location, Observer observer) {
        initByNull();

        final Observable thisInstance = this;
        final Geocoder geocoder = new Geocoder(context);

        new Thread(() -> {
            try {
                final List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                if (addresses == null || addresses.size() == 0) {
                    Toast.makeText(context, "Текущее местоположение неопределено", Toast.LENGTH_SHORT).show();
                } else {
                    name = addresses.get(0).getLocality();            // location name
                    country = addresses.get(0).getCountryCode();      // location country;
                }
            } catch (IOException e) {
                Toast.makeText(context, "Ошибка при обращении к геокодеру. Текущее местоположение неопределено", Toast.LENGTH_SHORT).show();
            }
            Log.d(LOGCAT_TAG, Thread.currentThread().toString());
            observer.update(thisInstance, null);
        }).start();
    }
}