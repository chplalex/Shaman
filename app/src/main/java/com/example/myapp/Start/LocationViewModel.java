package com.example.myapp.Start;

import android.app.Application;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

public class LocationViewModel extends AndroidViewModel implements LocationListener, Observer<LocationData> {

    private MutableLiveData<LocationData> liveData;
    private WeatherViewModel weatherViewModel;

    protected LocationViewModel(@NonNull Application application, WeatherViewModel weatherViewModel) {
        super(application);
        this.weatherViewModel = weatherViewModel;
        this.liveData = new MutableLiveData<>();
        this.liveData.observe(getApplication(), this);
    }

    public void initData(@Nullable Bundle arguments) {
        LocationData locationData = new LocationData();
        locationData.initFromBundle(arguments);
        if (locationData.isEmpty()) {
            locationData.initFromSharedPreferences(getApplication());
        }
        if (locationData.isEmpty()) {
            locationData.initFromCurrentLocation(getApplication(), this);
        } else {
            liveData.setValue(locationData);
        }
    }

    public void initDataForCurrentLocation() {
        LocationData locationData = new LocationData();
        locationData.initFromCurrentLocation(getApplication(), this);
    }

    public void initDataByQuery(String query) {
        liveData.setValue(new LocationData(query));
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LocationManager lm = (LocationManager) getApplication().getSystemService(LOCATION_SERVICE);
        lm.removeUpdates(this);

        final Geocoder geocoder = new Geocoder(getApplication());
        try {
            final List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    1);
            if (addresses == null || addresses.size() == 0) {
                liveData.postValue(null);
            } else {
                liveData.postValue(new LocationData(
                        addresses.get(0).getLocality(),           // location name
                        addresses.get(0).getCountryCode()));      // location country);
            }
        } catch (IOException e) {
            liveData.postValue(null);
        }
    }

    @Override
    public void onChanged(LocationData locationData) {
        weatherViewModel.initData(locationData);
    }
}
