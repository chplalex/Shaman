package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import static com.example.myapp.Utils.*;

public class SettingsActivity extends AppCompatActivity {
    Spinner spnWeatherPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.app_name_settings);
        findViewsById();
        setSpnWeatherPoint();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected  void onPause() {
        super.onPause();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        LogStackTraceElement(getApplicationContext());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void findViewsById() {
        spnWeatherPoint = findViewById(R.id.spnWeatherPoint);
    }

    private void setSpnWeatherPoint() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.points_array, R.layout.activiti_settings_spinner_item);
        adapter.setDropDownViewResource(R.layout.activity_settings_spinner_dropdown);
        spnWeatherPoint.setAdapter(adapter);
    }

    public void onSaveSettingsBtnClick(View view) {
        finish();
    }
}

