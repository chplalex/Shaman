package com.example.myapp.Start;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class LocationViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private Application application;
    private WeatherViewModel weatherViewModel;

    public LocationViewModelFactory(@NonNull Application application, WeatherViewModel weatherViewModel) {
        super();
        this.application = application;
        this.weatherViewModel = weatherViewModel;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == LocationViewModel.class) {
            return (T) new LocationViewModel(application, weatherViewModel);
        }
        return super.create(modelClass);
    }
}
