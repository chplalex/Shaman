package com.example.myapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

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
        MyLog(getApplicationContext(), "SettingsActivity.onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        MyLog(getApplicationContext(), "SettingsActivity.onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onStop()", Toast.LENGTH_SHORT).show();
        MyLog(getApplicationContext(), "SettingsActivity.onStop()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onRestart()", Toast.LENGTH_SHORT).show();
        MyLog(getApplicationContext(), "SettingsActivity.onRestart()");
    }

    @Override
    protected  void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "SettingsActivity.onPause()", Toast.LENGTH_SHORT).show();
        MyLog(getApplicationContext(), "SettingsActivity.onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog(getApplicationContext(), "SettingsActivity.onResume()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog(getApplicationContext(), "SettingsActivity.onDestroy()");
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

