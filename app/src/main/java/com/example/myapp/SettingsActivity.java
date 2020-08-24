package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;

import static com.example.myapp.Utils.SETTINGS_KEY;

public class SettingsActivity extends AppCompatActivity {
    ScrollView scrollView;
    MaterialTextView txtPoint;
    RecyclerView rvPoints;
    MaterialCheckBox checkBoxPressure;
    MaterialCheckBox checkBoxWind;
    MaterialCheckBox checkBoxSun;
    MaterialCheckBox checkBoxMoon;
    MaterialRadioButton rbThemeSystem;
    MaterialRadioButton rbThemeLight;
    MaterialRadioButton rbThemeDark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SettingsContainer sc = SettingsContainer.getInstance();

        if (sc.isThemeSystem) {
            setTheme(R.style.AppThemeSystem);
        }
        if (sc.isThemeLight) {
            setTheme(R.style.AppThemeLight);
        }
        if (sc.isThemeDark) {
            setTheme(R.style.AppThemeDark);
        }

        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.app_name_settings);
        findViewsById();
        initPoints();
        initThemes();
        restoreSettings();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            saveSettings();
            Intent intent = new Intent();
            intent.putExtra(SETTINGS_KEY, SettingsContainer.getInstance());
            setResult(RESULT_OK, intent);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void findViewsById() {
        scrollView = findViewById(R.id.scrollView);
        txtPoint = findViewById(R.id.txtPoint);
        rvPoints = findViewById(R.id.rvPoints);
        checkBoxPressure = findViewById(R.id.checkBoxPressure);
        checkBoxWind = findViewById(R.id.checkBoxWind);
        checkBoxSun = findViewById(R.id.checkBoxSun);
        checkBoxMoon = findViewById(R.id.checkBoxMoon);
        rbThemeSystem = findViewById(R.id.rbThemeSystem);
        rbThemeLight = findViewById(R.id.rbThemeLight);
        rbThemeDark = findViewById(R.id.rbThemeDark);
    }

    private void initPoints() {
        // Эта установка служит для повышения производительности системы
        rvPoints.setHasFixedSize(true);
        // Будем работать со встроенным менеджером
        rvPoints.setLayoutManager(new LinearLayoutManager(this));
        // Установим адаптер
        PointsAdapter adapter = new PointsAdapter(getResources().getStringArray(R.array.points_array));
        // Установим слушателя
        adapter.SetOnItemClickListener((view, position) -> {
            txtPoint.setText(((TextView) view).getText());
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.selectedItemWeatherPoint = position;
        });
        rvPoints.setAdapter(adapter);
    }

    private void initThemes() {

        View.OnClickListener rbClickListener = view -> {
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.isThemeSystem = rbThemeSystem.isChecked();
            sc.isThemeLight = rbThemeLight.isChecked();
            sc.isThemeDark = rbThemeDark.isChecked();
            recreate();
        };

        rbThemeSystem.setOnClickListener(rbClickListener);
        rbThemeLight.setOnClickListener(rbClickListener);
        rbThemeDark.setOnClickListener(rbClickListener);
    }

    private void saveSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        sc.isChkBoxPressure = checkBoxPressure.isChecked();
        sc.isChkBoxWind = checkBoxWind.isChecked();
        sc.isChkBoxSun = checkBoxSun.isChecked();
        sc.isChkBoxMoon = checkBoxMoon.isChecked();
        sc.isThemeSystem = rbThemeSystem.isChecked();
        sc.isThemeLight = rbThemeLight.isChecked();
        sc.isThemeDark = rbThemeDark.isChecked();
    }

    private void restoreSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        String[] arrPoints = getResources().getStringArray(R.array.points_array);
        txtPoint.setText(arrPoints[sc.selectedItemWeatherPoint]);
        checkBoxPressure.setChecked(sc.isChkBoxPressure);
        checkBoxWind.setChecked(sc.isChkBoxWind);
        checkBoxSun.setChecked(sc.isChkBoxSun);
        checkBoxMoon.setChecked(sc.isChkBoxMoon);
        rbThemeSystem.setChecked(sc.isThemeSystem);
        rbThemeLight.setChecked(sc.isThemeLight);
        rbThemeDark.setChecked(sc.isThemeDark);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("txtPoint", txtPoint.getText());
        outState.putInt("scrollY", scrollView.getScrollY());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        txtPoint.setText(savedInstanceState.getCharSequence("txtPoint"));
        scrollView.setScrollY(savedInstanceState.getInt("scrollY"));
    }
}

