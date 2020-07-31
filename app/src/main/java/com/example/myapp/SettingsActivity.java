package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
        Toast.makeText(getApplicationContext(), "SettingsActivity.onCreate()", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onStart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onRestart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onResume()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onDestroy()", Toast.LENGTH_SHORT).show();
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

