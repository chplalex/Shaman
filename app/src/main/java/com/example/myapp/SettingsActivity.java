package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import static com.example.myapp.Utils.*;

public class SettingsActivity extends AppCompatActivity {
    Spinner spnWeatherPoint;
    CheckBox checkBoxPressure;
    CheckBox checkBoxWind;
    CheckBox checkBoxSun;
    CheckBox checkBoxMoon;
    Switch switchDarkMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.app_name_settings);
        findViewsById();
        initSpnWeatherPoint();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.copySettings((SettingsContainer) (Objects.requireNonNull(bundle.getSerializable(SETTINGS_KEY))));
        }

        restoreSettings();
    }

    private void findViewsById() {
        spnWeatherPoint = findViewById(R.id.spnWeatherPoint);
        checkBoxPressure = findViewById(R.id.checkBoxPressure);
        checkBoxWind = findViewById(R.id.checkBoxWind);
        checkBoxSun = findViewById(R.id.checkBoxSun);
        checkBoxMoon = findViewById(R.id.checkBoxMoon);
        switchDarkMode = findViewById(R.id.switchDarkMode);
    }

    private void initSpnWeatherPoint() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.points_array, R.layout.activiti_settings_spinner_item);
        adapter.setDropDownViewResource(R.layout.activity_settings_spinner_dropdown);
        spnWeatherPoint.setAdapter(adapter);
    }

    public void onSaveSettingsBtnClick(View view) {
        saveSettings();
        Intent intent = new Intent();
        intent.putExtra(SETTINGS_KEY, SettingsContainer.getInstance());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void saveSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        sc.selectedItemWeatherPoint = spnWeatherPoint.getSelectedItemPosition();
        sc.isChkBoxPressure = checkBoxPressure.isChecked();
        sc.isChkBoxWind = checkBoxWind.isChecked();
        sc.isChkBoxSun = checkBoxSun.isChecked();
        sc.isChkBoxMoon = checkBoxMoon.isChecked();
        sc.isChkDarkMode = switchDarkMode.isChecked();
    }

    private void restoreSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        spnWeatherPoint.setSelection(sc.selectedItemWeatherPoint);
        checkBoxPressure.setChecked(sc.isChkBoxPressure);
        checkBoxWind.setChecked(sc.isChkBoxWind);
        checkBoxSun.setChecked(sc.isChkBoxSun);
        checkBoxMoon.setChecked(sc.isChkBoxMoon);
        switchDarkMode.setChecked(sc.isChkDarkMode);
    }
}

