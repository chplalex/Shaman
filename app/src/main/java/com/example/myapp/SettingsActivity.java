package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

import static com.example.myapp.Utils.*;

public class SettingsActivity extends AppCompatActivity {
    TextView txtPoint;
    RecyclerView rvPoints;
    CheckBox checkBoxPressure;
    CheckBox checkBoxWind;
    CheckBox checkBoxSun;
    CheckBox checkBoxMoon;
    Switch switchDarkMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.app_name_settings);
        findViewsById();
        initPoints();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.copySettings((SettingsContainer) (Objects.requireNonNull(bundle.getSerializable(SETTINGS_KEY))));
        }

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
        txtPoint = findViewById(R.id.txtPoint);
        rvPoints = findViewById(R.id.rvPoints);
        checkBoxPressure = findViewById(R.id.checkBoxPressure);
        checkBoxWind = findViewById(R.id.checkBoxWind);
        checkBoxSun = findViewById(R.id.checkBoxSun);
        checkBoxMoon = findViewById(R.id.checkBoxMoon);
        switchDarkMode = findViewById(R.id.switchDarkMode);
    }

    private void initPoints() {
        // Эта установка служит для повышения производительности системы
        rvPoints.setHasFixedSize(true);
        // Будем работать со встроенным менеджером
        rvPoints.setLayoutManager(new LinearLayoutManager(this));
        // Установим адаптер
        PointsAdapter adapter = new PointsAdapter(getResources().getStringArray(R.array.points_array));
        // Установим слушателя
        adapter.SetOnItemClickListener(new PointsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                txtPoint.setText(((TextView)view).getText());
                SettingsContainer sc = SettingsContainer.getInstance();
                sc.selectedItemWeatherPoint = position;
            }
        });
        rvPoints.setAdapter(adapter);
    }

    private void saveSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        sc.isChkBoxPressure = checkBoxPressure.isChecked();
        sc.isChkBoxWind = checkBoxWind.isChecked();
        sc.isChkBoxSun = checkBoxSun.isChecked();
        sc.isChkBoxMoon = checkBoxMoon.isChecked();
        sc.isChkDarkMode = switchDarkMode.isChecked();
    }

    private void restoreSettings() {
        SettingsContainer sc = SettingsContainer.getInstance();
        String[] arrPoints = getResources().getStringArray(R.array.points_array);
        txtPoint.setText(arrPoints[sc.selectedItemWeatherPoint]);
        checkBoxPressure.setChecked(sc.isChkBoxPressure);
        checkBoxWind.setChecked(sc.isChkBoxWind);
        checkBoxSun.setChecked(sc.isChkBoxSun);
        checkBoxMoon.setChecked(sc.isChkBoxMoon);
        switchDarkMode.setChecked(sc.isChkDarkMode);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("txtPoint", txtPoint.getText());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        txtPoint.setText(savedInstanceState.getCharSequence("txtPoint"));
    }
}

