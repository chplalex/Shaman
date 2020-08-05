package com.example.myapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapp.Utils.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    // обязательные поля. всегда на экране
    private TextView txtPoint;
    private TextView txtTemperature;

    private TextView txtDownFallType;
    private TextView txtDownFallProbability;

    // необязательные поля. Видимы на экране в случае выбора пользователем в настройках
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

        public CharSequence csPoint;
        public CharSequence csTemperature;
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
            return (DataContainer) instance;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initContainers();
        findViewsById();
        initViews();
        setSettingsActivity();
    }

    private void initContainers() {
        SettingsContainer sc = SettingsContainer.getInstance();
        DataContainer dc = DataContainer.getInstance();
        String[] arrPoints = getResources().getStringArray(R.array.points_array);
        int index = getResources().getInteger(R.integer.DebugPointIndex);
        dc.csPoint = arrPoints[index];
        dc.csTemperature = getResources().getString(R.string.DebugTemperature);
        dc.csDownFallType = getResources().getString(R.string.DebugDownFallType);
        dc.csDownFallProbability = getResources().getString(R.string.DebugDawnFallProbability);
        dc.csPressureValue = getResources().getString(R.string.DebugPressureValue);
        dc.csPressureUnit = getResources().getString(R.string.DebugPressureUnit);
        dc.csWindForce = getResources().getString(R.string.DebugWindForce);
        dc.csWindDirection = getResources().getString(R.string.DebugWindDirection);
        dc.csSunrise = getResources().getString(R.string.DebugSunrise);
        dc.csSunset = getResources().getString(R.string.DebugSunset);
        dc.csMoonrise = getResources().getString(R.string.DebugMoonrise);
        dc.csMoonset = getResources().getString(R.string.DebugMoonset);
    }

    private void updateContainers() {
        SettingsContainer sc = SettingsContainer.getInstance();
        DataContainer dc = DataContainer.getInstance();
        String[] arrPoints = getResources().getStringArray(R.array.points_array);
        dc.csPoint = arrPoints[sc.selectedItemWeatherPoint];
    }

    private void findViewsById() {
        txtPoint = findViewById(R.id.txtPoint);
        txtTemperature = findViewById(R.id.txtTemperature);

        txtDownFallType = findViewById(R.id.txtDownFallType);
        txtDownFallProbability = findViewById(R.id.txtDownFallProbability);

        rowPressure = findViewById(R.id.rowPressure);
        txtPressureValue = findViewById(R.id.txtPressureValue);
        txtPressureUnit = findViewById(R.id.txtPressureUnit);

        rowWind = findViewById(R.id.rowWind);
        txtWindDirection = findViewById(R.id.txtWindDirection);
        txtWindForce = findViewById(R.id.txtWindForce);

        rowSun = findViewById(R.id.rowSun);
        txtSunrise = findViewById(R.id.txtSunrise);
        txtSunset = findViewById(R.id.txtSunset);

        rowMoon = findViewById(R.id.rowMoon);
        txtMoonrise = findViewById(R.id.txtMoonrise);
        txtMoonset = findViewById(R.id.txtMoonset);
    }

    private void initViews() {
        DataContainer dc = DataContainer.getInstance();
        txtPoint.setText(dc.csPoint);
        txtTemperature.setText(dc.csTemperature);
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
            rowPressure.setVisibility(View.INVISIBLE);
        }
        if (sc.isChkBoxWind) {
            rowWind.setVisibility(View.VISIBLE);
        } else {
            rowWind.setVisibility(View.INVISIBLE);
        }
        if (sc.isChkBoxSun) {
            rowSun.setVisibility(View.VISIBLE);
        } else {
            rowSun.setVisibility(View.INVISIBLE);
        }
        if (sc.isChkBoxMoon) {
            rowMoon.setVisibility(View.VISIBLE);
        } else {
            rowMoon.setVisibility(View.INVISIBLE);
        }
    }

    private void setSettingsActivity() {
        txtPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                SettingsContainer sc = SettingsContainer.getInstance();
                intent.putExtra(SETTINGS_KEY, sc);
                startActivityForResult(intent, REQUEST_FOR_SETTINGS);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
    }
}