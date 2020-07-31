package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    Spinner spnWeatherPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.app_name_settings);
        findViewsById();
        setSpnWeatherPoint();
    }

    private void findViewsById() {
        spnWeatherPoint = findViewById(R.id.spnWeatherPoint);
    }

    private void setSpnWeatherPoint() {
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.points_array, R.layout.activiti_settings_spinner_item);
        adapter.setDropDownViewResource(R.layout.activity_settings_spinner_dropdown);
        spnWeatherPoint.setAdapter(adapter);
    }

    public void onSaveSettingsBtnClick(View view) {
        finish();
    }
}

