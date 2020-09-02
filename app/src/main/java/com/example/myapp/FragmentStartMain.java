package com.example.myapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.WeatherData.CurrentWeatherContainer;
import com.example.myapp.WeatherData.CurrentWeatherData;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static com.example.myapp.Utils.REQUEST_FOR_SETTINGS;
import static com.example.myapp.Utils.SETTINGS_KEY;
import static com.example.myapp.Utils.WEATHER_UPDATE_KEY;

public class FragmentStartMain extends Fragment {

    private TextView txtPoint;
    private ImageView imgYandexWheather;
    private TextView txtTemperature;
    private RecyclerView rvForecasts;

    // внутренний класс для сохранения данных активити
    private static class DataContainer {

        private static DataContainer instance;

        public CharSequence csPoint;
        public CharSequence csTemperature;

        public static DataContainer getInstance() {
            if (instance == null) {
                instance = new DataContainer();
            }
            return instance;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initContainers(savedInstanceState);
        findViewsById(view);
        initViews();
        setSettingsActivity();
        setYandexWeatherActivity();
    }

    private void initContainers(@Nullable Bundle savedInstanceState) {
        DataContainer dc = DataContainer.getInstance();
        if (savedInstanceState == null) {
            String[] arrPoints = getResources().getStringArray(R.array.points_array);
            int index = getResources().getInteger(R.integer.DebugPointIndex);
            dc.csPoint = arrPoints[index];
            CurrentWeatherData wd = CurrentWeatherContainer.getData();
            dc.csTemperature = String.format(Locale.getDefault(), "%+.0f°C", wd.main.temp);
        } else {
            dc.csPoint = savedInstanceState.getCharSequence("txtPoint");
            dc.csTemperature = savedInstanceState.getCharSequence("txtTemperature");
        }
    }

    private void updateContainers() {
        SettingsContainer sc = SettingsContainer.getInstance();
        DataContainer dc = DataContainer.getInstance();
        CurrentWeatherData wd = CurrentWeatherContainer.getData();
        String[] arrPoints = getResources().getStringArray(R.array.points_array);
        dc.csPoint = arrPoints[sc.selectedItemWeatherPoint];
        dc.csTemperature = String.format(Locale.getDefault(),"%+.0f°C", wd.main.temp);
    }

    @SuppressLint("ResourceType")
    private void findViewsById(View view) {
        txtPoint = view.findViewById(R.id.txtPoint);
        imgYandexWheather = view.findViewById(R.id.imgYandexWeather);
        txtTemperature = view.findViewById(R.id.txtTemperature);
        rvForecasts = view.findViewById(R.id.rvForecasts);
    }

    private void initViews() {
        DataContainer dc = DataContainer.getInstance();
        txtPoint.setText(dc.csPoint);
        txtTemperature.setText(dc.csTemperature);
        // Эта установка служит для повышения производительности системы
        rvForecasts.setHasFixedSize(true);
        // Будем работать со встроенным менеджером
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        rvForecasts.setLayoutManager(layoutManager);
        // Создадим адаптер
        ForecastsAdapter adapter = new ForecastsAdapter(getResources().getStringArray(R.array.debug_forecasts_array));
        // Установим адаптер
        rvForecasts.setAdapter(adapter);
    }

    private void setSettingsActivity() {
        txtPoint.setOnClickListener(view -> {
            Intent intent = new Intent(getContext(), SettingsActivity.class);
            SettingsContainer sc = SettingsContainer.getInstance();
            intent.putExtra(SETTINGS_KEY, sc);
            startActivityForResult(intent, REQUEST_FOR_SETTINGS);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode != REQUEST_FOR_SETTINGS) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if ((resultCode != RESULT_OK) || (data == null)) {
            return;
        }
        SettingsContainer sc = SettingsContainer.getInstance();
        sc.copySettings((SettingsContainer) Objects.requireNonNull(data.getSerializableExtra(SETTINGS_KEY)));
        updateContainers();
        initViews();
        EventBus.getDefault().post(new MsgEvent(SETTINGS_KEY));
        Activity activity = getActivity();
        if (activity != null) {
            activity.recreate();
        }
    }

    private void setYandexWeatherActivity() {
        imgYandexWheather.setOnClickListener(view -> {
            String[] arrPoints = getResources().getStringArray(R.array.points_array_for_http);
            SettingsContainer sc = SettingsContainer.getInstance();
            String url = getResources().getString(R.string.YandexWheatherURL) +
                    arrPoints[sc.selectedItemWeatherPoint];
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("txtPoint", txtPoint.getText());
        outState.putCharSequence("txtTemperature", txtTemperature.getText());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent msgEvent) {
        if (msgEvent.msg.equals(WEATHER_UPDATE_KEY)) {
            updateContainers();
            initViews();
        }
    }



}
