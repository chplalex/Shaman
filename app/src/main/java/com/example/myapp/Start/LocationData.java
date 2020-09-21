package com.example.myapp.Start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.example.myapp.R;

import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;

public class LocationData {
    public String name;
    public String country;

    private void setDataNull() {
        name = null;
        country = null;
    }

    private boolean isEmpty() {
        return (name == null || name.length() == 0);
    }

    public void setDataFromBundle(@Nullable Bundle bundle) {
        if (bundle == null) {
            setDataNull();
        } else {
            name = bundle.getString(LOCATION_ARG_NAME);
            country = bundle.getString(LOCATION_ARG_COUNTRY);
        }
        if (isEmpty()) setDataNull();
    }

    public void setDataFromSharedPreferences(@Nullable Context context) {
        if (context == null) {
            setDataNull();
        } else {
            String resFileName = context.getApplicationContext().getResources().getString(R.string.file_name_prefs);
            SharedPreferences sp = context.getSharedPreferences(resFileName, Context.MODE_PRIVATE);
            name = sp.getString("pref_loc_name", null);
            country = sp.getString("pref_loc_country", null);
        }
        if (isEmpty()) setDataNull();
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
}
