package com.example.myapp.Start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.example.myapp.DBService.Location;
import com.example.myapp.DBService.Request;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.MainApp;
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

import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

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
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
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
        loadData();
    }

    public void initLocationData(@Nullable Bundle arguments) {
        LocationData locationData = new LocationData();
        locationData.initFromBundle(arguments);
        if (locationData.isEmpty()) {
            locationData.initFromSharedPreferences(getApplication());
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
                liveLocationData.updateValue(observable);
            }
        });
    }

    public void loadData() {
        LocationData locationData = liveLocationData.getValue();

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
                    liveWeatherData.updateValue(wd);
                    dbDataExchange(wd);
                } else {
                    liveWeatherData.updateValue(null);
                    liveFavoriteData.updateValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
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