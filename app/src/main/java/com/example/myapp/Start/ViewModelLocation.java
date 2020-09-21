package com.example.myapp.Start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapp.R;

import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;

public class ViewModelLocation extends AndroidViewModel {

    MutableLiveData<LocationData> data;

    public ViewModelLocation(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<LocationData> getData (@Nullable Bundle arguments) {
        if (data == null) {
            data = new MutableLiveData<>();
        }
        LocationData locationData = new LocationData();

        String locationName;
        String locationCountry;
        if (argumnts != null) {
            locationName = arguments.getString(LOCATION_ARG_NAME);
            locationCountry = arguments.getString(LOCATION_ARG_COUNTRY);
        } else {
            Application application = getApplication();
            SharedPreferences sp = application.getSharedPreferences(
                    application.getResources().getString(R.string.file_name_prefs),
                    Context.MODE_PRIVATE);
            locationName = sp.getString("pref_loc_name", null);
            locationCountry = sp.getString("pref_loc_country", null);
        }
    }
}
