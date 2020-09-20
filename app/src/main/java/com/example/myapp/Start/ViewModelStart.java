package com.example.myapp.Start;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

public class ViewModelStart extends ViewModel {

    private MutableLiveData<WeatherData> data;
    private OpenWeatherRetrofit openWeatherRetrofit;
//    private SharedPreferences sharedPreferences;

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

//        final String lang = sharedPreferences.getString("pref_lang", "RU");
//        final String units = sharedPreferences.getString("pref_units", "metric");

        StringBuilder argLocation = new StringBuilder(locationName);
        if (locationCountry != null && locationCountry.length() > 0) {
            argLocation.append(",").append(locationCountry);
        }
        openWeatherRetrofit.loadWeather(argLocation.toString(), APP_ID, "RU", "metric").enqueue(new Callback<WeatherData>() {
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
}