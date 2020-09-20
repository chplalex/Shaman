package com.example.myapp.Start;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapp.WeatherData.WeatherData;

public class ViewModelStart extends ViewModel {

    MutableLiveData<WeatherData> data;

    public LiveData<WeatherData> getData() {
        if (data == null) {
            data = new MutableLiveData<>();
            loadData();
        }
        return data;
    }

    private void loadData() {
        dataRepository
    };

}
