package com.example.myapp.Start;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ViewModelWeather extends AndroidViewModel implements Consumer<Location> {

    private MutableLiveData<WeatherData> data;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private SharedPreferences sharedPreferences;

    public ViewModelWeather(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(
                application.getResources().getString(R.string.file_name_prefs),
                Context.MODE_PRIVATE);
    }


    public LiveData<WeatherData> getData(String locationName, String locationCountry) {
        if (data == null) {
            initData();
            loadWeatherData(locationName, locationCountry);
        }
        return data;
    }

    private void initData() {
        data = new MutableLiveData<>();
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
    }

    private void loadWeatherData(String locationName, String locationCountry) {

        if (locationName == null || locationName.length() == 0) {
            locationName = sharedPreferences.getString("pref_loc_name", null);
            locationCountry = sharedPreferences.getString("pref_loc_country", null);

            if (locationName == null || locationName.length() == 0) {
                requestCurrentLocation();
                return;
            }
        }

        final String lang = sharedPreferences.getString("pref_lang", "RU");
        final String units = sharedPreferences.getString("pref_units", "metric");

        StringBuilder locationFull = new StringBuilder(locationName);
        if (locationCountry != null && locationCountry.length() > 0) {
            locationFull.append(",").append(locationCountry);
        }

        openWeatherRetrofit.loadWeather(locationFull.toString(), APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    data.postValue(response.body());
                } else {
                    data.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                data.postValue(null);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    private void requestCurrentLocation() {

        Context context = getApplication().getApplicationContext();

        LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        String provider = lm.getBestProvider(criteria, true);
        if (provider == null) {
            Toast.makeText(context, "Нет возможности определить текущее местоположение", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "Нет достаточных прав для опредения текущее местоположение", Toast.LENGTH_SHORT).show();
            return;
        }
        lm.getCurrentLocation(provider, null, context.getMainExecutor(), this);

    }

    @Override
    public void accept(Location location) {
        Context context = getApplication().getApplicationContext();

        if (location == null) {
            Toast.makeText(context, "Нет возможности определить текущее местоположение", Toast.LENGTH_SHORT).show();
            return;
        }

        final Geocoder geocoder = new Geocoder(context);
        new Thread(() -> {
            try {
                final List<Address> addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        1);
                String locationCountry = addresses.get(0).getCountryCode();
                String locationName = addresses.get(0).getLocality();
                if (locationCountry == null || locationName == null) {
                        Toast.makeText(context, "Текущее местоположение не определено", Toast.LENGTH_SHORT).show();
                } else {
                    context.runOnUiThread(() -> {
                        initViewsByLocation(locationName, locationCountry);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();


    }
}