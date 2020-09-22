package com.example.myapp.Start;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

public class WeatherViewModel extends AndroidViewModel {

    private MutableLiveData<WeatherData> liveData;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private SharedPreferences sharedPreferences;

    public WeatherViewModel(@NonNull Application application) {
        super(application);
        sharedPreferences = application.getSharedPreferences(
                application.getResources().getString(R.string.file_name_prefs),
                Context.MODE_PRIVATE);
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
    }

    public MutableLiveData<WeatherData> getLiveData() {
        if (liveData == null) {
            this.liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void initData(LocationData locationData) {
        final String lang = sharedPreferences.getString("pref_lang", "RU");
        final String units = sharedPreferences.getString("pref_units", "metric");

        openWeatherRetrofit.loadWeather(locationData.toString(), APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(response.body());
                } else {
                    liveData.postValue(null);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                liveData.postValue(null);
            }
        });
    }
}