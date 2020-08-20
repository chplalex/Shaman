package com.example.myapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static com.example.myapp.Utils.SETTINGS_KEY;
import static com.example.myapp.Utils.WEATHER_UPDATE_KEY;

public class FragmentDetails extends Fragment {

    // эти поля всегда на экране
    private TextView txtDownFallType;
    private TextView txtDownFallProbability;

    // эти поля видны при выборе пользователем установок
    private TableRow rowPressure;
    private TextView txtPressureValue;
    private TextView txtPressureUnit;

    private TableRow rowWind;
    private TextView txtWindDirection;
    private TextView txtWindForce;

    private TableRow rowSun;
    private TextView txtSunrise;
    private TextView txtSunset;

    private TableRow rowMoon;
    private TextView txtMoonrise;
    private TextView txtMoonset;

    // внутренний класс для сохранения данных активити
    private static class DataContainer {

        private static DataContainer instance;

        public CharSequence csDownFallType;
        public CharSequence csDownFallProbability;
        public CharSequence csPressureValue;
        public CharSequence csPressureUnit;
        public CharSequence csWindDirection;
        public CharSequence csWindForce;
        public CharSequence csSunrise;
        public CharSequence csSunset;
        public CharSequence csMoonrise;
        public CharSequence csMoonset;

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
        return inflater.inflate(R.layout.fragment_main_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initContainers();
        findViewsById(view);
        initViews();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initContainers() {
        CurrentWeatherData wd = CurrentWeatherContainer.getInstance().getData();
        DataContainer dc = DataContainer.getInstance();
        dc.csDownFallType = wd.weather[0].main;
        dc.csDownFallProbability = wd.weather[0].description;
        dc.csPressureValue = String.valueOf(wd.main.pressure);
        dc.csPressureUnit = getResources().getString(R.string.DebugPressureUnit);
        dc.csWindForce = String.valueOf(wd.wind.speed);
        dc.csWindDirection = String.valueOf(wd.wind.deg);
        dc.csSunrise = getResources().getString(R.string.DebugSunrise);
        dc.csSunset = getResources().getString(R.string.DebugSunset);
        dc.csMoonrise = getResources().getString(R.string.DebugMoonrise);
        dc.csMoonset = getResources().getString(R.string.DebugMoonset);
    }

    @SuppressLint("ResourceType")
    private void findViewsById(View view) {
        txtDownFallType = view.findViewById(R.id.txtDownFallType);
        txtDownFallProbability = view.findViewById(R.id.txtDownFallProbability);

        rowPressure = view.findViewById(R.id.rowPressure);
        txtPressureValue = view.findViewById(R.id.txtPressureValue);
        txtPressureUnit = view.findViewById(R.id.txtPressureUnit);

        rowWind = view.findViewById(R.id.rowWind);
        txtWindDirection = view.findViewById(R.id.txtWindDirection);
        txtWindForce = view.findViewById(R.id.txtWindForce);

        rowSun = view.findViewById(R.id.rowSun);
        txtSunrise = view.findViewById(R.id.txtSunrise);
        txtSunset = view.findViewById(R.id.txtSunset);

        rowMoon = view.findViewById(R.id.rowMoon);
        txtMoonrise = view.findViewById(R.id.txtMoonrise);
        txtMoonset = view.findViewById(R.id.txtMoonset);
    }

    void initViews() {
        DataContainer dc = DataContainer.getInstance();
        txtDownFallType.setText(dc.csDownFallType);
        txtDownFallProbability.setText(dc.csDownFallProbability);
        txtPressureValue.setText(dc.csPressureValue);
        txtPressureUnit.setText(dc.csPressureUnit);
        txtWindForce.setText(dc.csWindForce);
        txtWindDirection.setText(dc.csWindDirection);
        txtSunrise.setText(dc.csSunrise);
        txtSunset.setText(dc.csSunset);
        txtMoonrise.setText(dc.csMoonrise);
        txtMoonset.setText(dc.csMoonset);

        SettingsContainer sc = SettingsContainer.getInstance();
        if (sc.isChkBoxPressure) {
            rowPressure.setVisibility(View.VISIBLE);
        } else {
            rowPressure.setVisibility(View.GONE);
        }
        if (sc.isChkBoxWind) {
            rowWind.setVisibility(View.VISIBLE);
        } else {
            rowWind.setVisibility(View.GONE);
        }
        if (sc.isChkBoxSun) {
            rowSun.setVisibility(View.VISIBLE);
        } else {
            rowSun.setVisibility(View.GONE);
        }
        if (sc.isChkBoxMoon) {
            rowMoon.setVisibility(View.VISIBLE);
        } else {
            rowMoon.setVisibility(View.GONE);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMsgEvent(MsgEvent msgEvent) {
        if (msgEvent.msg.equals(SETTINGS_KEY)) {
            initViews();
        }
        if (msgEvent.msg.equals(WEATHER_UPDATE_KEY)) {
            initContainers();
            initViews();
        }

    }
}
