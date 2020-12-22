package com.example.myapp.Start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.example.myapp.DBService.Location;
import com.example.myapp.DBService.Request;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.ui.MainApp;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOGCAT_TAG;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.HTTP;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.HTTPS;

public class ViewModelStart extends AndroidViewModel implements androidx.lifecycle.Observer<LocationData> {

    private MyMutableLiveData<LocationData> liveLocationData;
    private MyMutableLiveData<WeatherData> liveWeatherData;
    private MyMutableLiveData<Boolean> liveFavoriteData;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private SharedPreferences sharedPreferences;
    private ShamanDao shamanDao;

    public ViewModelStart(@NonNull Application application) {
        super(application);
        shamanDao = MainApp.getInstance().getShamanDao();
        sharedPreferences = application.getSharedPreferences(
                application.getResources().getString(R.string.file_name_prefs),
                Context.MODE_PRIVATE);

        Log.d(LOGCAT_TAG, "Build.VERSION.SDK_INT = " + Build.VERSION.SDK_INT);
        String baseURL;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            baseURL = HTTPS + BASE_URL;
        } else {
            baseURL = HTTP + BASE_URL;
        }
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
        liveLocationData = new MyMutableLiveData<>();
        liveWeatherData = new MyMutableLiveData<>();
        liveFavoriteData = new MyMutableLiveData<>();
    }

    public MyMutableLiveData<LocationData> getLiveLocationData() {
        return liveLocationData;
    }

    public MyMutableLiveData<WeatherData> getLiveWeatherData() {
        return liveWeatherData;
    }

    public MyMutableLiveData<Boolean> getLiveFavoriteData() {
        return liveFavoriteData;
    }

    @Override
    public void onChanged(LocationData locationData) {
        Log.d(LOGCAT_TAG, "ViewModelStart.onChanged(" + locationData + ") -> loadData()");
        loadData();
    }

    public void initLocationData(@Nullable Bundle arguments) {
        LocationData locationData = new LocationData();
        locationData.initFromBundle(arguments);
        if (locationData.isEmpty()) {
            locationData.initFromSharedPreferences(sharedPreferences);
        }
        if (locationData.isEmpty()) {
            initLocationDataByCurrentLocation();
        } else {
            liveLocationData.updateValue(locationData);
        }
    }

    public void initLocationDataByQuery(String query) {
        LocationData locationData = new LocationData(query.trim(), null);

        if (locationData.isEmpty()) {
            liveLocationData.updateValue(null);
        } else {
            liveLocationData.updateValue(locationData);
        }
    }

    public void initLocationDataByCurrentLocation() {
        new LocationData().initFromCurrentLocation(getApplication(), new Observer() {
            @Override
            public void update(Observable observable, Object o) {
                Log.d(LOGCAT_TAG, "new LocationData().initFromCurrentLocation() -> update(" + observable.toString() + ") -> liveLocationData.updateValue(observable)");
                liveLocationData.updateValue(observable);
            }
        });
    }

    private void loadData() {
        LocationData locationData = liveLocationData.getValue();

        Log.d(LOGCAT_TAG, "loadData(). locationData = " + locationData);

        if (locationData == null || locationData.isEmpty()) {
            liveWeatherData.updateValue(null);
            liveFavoriteData.updateValue(null);
            return;
        }

        final String lang = sharedPreferences.getString("pref_lang", "RU");
        final String units = sharedPreferences.getString("pref_units", "metric");

        openWeatherRetrofit.loadWeather(locationData.toString(), APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherData wd = response.body();
                    Log.d(LOGCAT_TAG, "openWeatherRetrofit.loadWeather() -> onResponse(" + wd.getName() + ", " + wd.getCountry() + ") -> liveWeatherData.updateValue(wd)");
                    liveWeatherData.updateValue(wd);
                    dbDataExchange(wd);
                } else {
                    Log.d(LOGCAT_TAG, "openWeatherRetrofit.loadWeather() -> onResponse( NULL ) -> liveWeatherData.updateValue(null)");
                    liveWeatherData.updateValue(null);
                    liveFavoriteData.updateValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                Log.d(LOGCAT_TAG, "openWeatherRetrofit.loadWeather() -> onFailure() -> liveWeatherData.updateValue(null)");
                Log.d(LOGCAT_TAG, "Throwable t = " + t.getMessage());
                liveWeatherData.updateValue(null);
                liveFavoriteData.updateValue(null);
            }
        });
    }

    private void dbDataExchange(@NotNull WeatherData wd) {
        new Thread(() -> {
            List<Location> locations = shamanDao.getLocationByNameAndCountry(wd.getName(), wd.getCountry());
            if (locations == null || locations.size() == 0) {
                shamanDao.insertLocation(new Location(
                        wd.id,
                        wd.name,
                        wd.sys.country,
                        wd.coord.lon,
                        wd.coord.lat));
                liveFavoriteData.postValue(false);
            } else {
                liveFavoriteData.postValue(locations.get(0).favorite);
            }
            shamanDao.insertRequest(new Request(
                    wd.id,
                    wd.main.temp));
        }).start();
    }

    public void reverseFavorite() {
        new Thread(() -> {
            WeatherData weatherData = liveWeatherData.getValue();
            if (weatherData == null) return;
            Boolean favoriteData = liveFavoriteData.getValue();
            favoriteData = (favoriteData == null) ? true : !favoriteData;
            shamanDao.updateLocationFavoriteByNameAndCountry(weatherData.name, weatherData.sys.country, favoriteData);
            liveFavoriteData.postValue(favoriteData);
        }).start();
    }
}