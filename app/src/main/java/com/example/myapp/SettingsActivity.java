package com.example.myapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import static com.example.myapp.Utils.*;

public class SettingsActivity extends AppCompatActivity {
    Spinner spnWeatherPoint;
    CheckBox checkBoxWindDirection;
    CheckBox checkBoxWindForce;
    CheckBox checkBoxPressure;
    CheckBox checkBoxSunMoving;
    CheckBox checkBoxMoonMoving;
    Switch switchDarkMode;

    private static class DataContainer {
        private static Object instance;

        public int selectedItemWeatherPoint;
        public boolean IsChkBoxWindDirection;
        public boolean IsChkBoxWindForce;
        public boolean IsChkBoxPressure;
        public boolean IsChkBoxSunMoving;
        public boolean IsChkBoxMoonMoving;
        public boolean IsChkDarkMode;

        public static DataContainer getInstance() {
            if (instance == null) {
                instance = new DataContainer();
            }
            return (DataContainer) instance;
        }
    }

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
        // первый способ
        outState.putBoolean("checkBoxWindDirection", checkBoxWindDirection.isChecked());
        outState.putBoolean("checkBoxWindForce", checkBoxWindForce.isChecked());
        outState.putBoolean("checkBoxPressure", checkBoxPressure.isChecked());
        outState.putBoolean("checkBoxSunMoving", checkBoxSunMoving.isChecked());
        outState.putBoolean("checkBoxMoonMoving", checkBoxMoonMoving.isChecked());
        outState.putBoolean("switchDarkMode", switchDarkMode.isChecked());
        // второй способ
        DataContainer dataContainer = DataContainer.getInstance();
        dataContainer.selectedItemWeatherPoint = spnWeatherPoint.getSelectedItemPosition();
        dataContainer.IsChkBoxWindDirection = checkBoxWindDirection.isChecked();
        dataContainer.IsChkBoxWindForce = checkBoxWindForce.isChecked();
        dataContainer.IsChkBoxPressure = checkBoxPressure.isChecked();
        dataContainer.IsChkBoxSunMoving = checkBoxSunMoving.isChecked();
        dataContainer.IsChkBoxMoonMoving = checkBoxMoonMoving.isChecked();
        dataContainer.IsChkDarkMode = switchDarkMode.isChecked();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogStackTraceElement(getApplicationContext());
        // первый способ
        checkBoxWindDirection.setChecked(savedInstanceState.getBoolean("checkBoxWindDirection"));
        checkBoxWindForce.setChecked(savedInstanceState.getBoolean("checkBoxWindForce"));
        checkBoxPressure.setChecked(savedInstanceState.getBoolean("checkBoxPressure"));
        checkBoxSunMoving.setChecked(savedInstanceState.getBoolean("checkBoxSunMoving"));
        checkBoxMoonMoving.setChecked(savedInstanceState.getBoolean("checkBoxMoonMoving"));
        switchDarkMode.setChecked(savedInstanceState.getBoolean("switchDarkMode"));
        // второй способ
        DataContainer dataContainer = DataContainer.getInstance();
        spnWeatherPoint.setSelection(dataContainer.selectedItemWeatherPoint);
        checkBoxWindDirection.setChecked(dataContainer.IsChkBoxWindDirection);
        checkBoxWindForce.setChecked(dataContainer.IsChkBoxWindForce);
        checkBoxPressure.setChecked(dataContainer.IsChkBoxPressure);
        checkBoxSunMoving.setChecked(dataContainer.IsChkBoxSunMoving);
        checkBoxMoonMoving.setChecked(dataContainer.IsChkBoxMoonMoving);
        switchDarkMode.setChecked(dataContainer.IsChkDarkMode);
    }

    private void findViewsById() {
        spnWeatherPoint = findViewById(R.id.spnWeatherPoint);
        checkBoxWindDirection = findViewById(R.id.checkBoxWindDirection);
        checkBoxWindForce = findViewById(R.id.checkBoxWindForce);
        checkBoxPressure = findViewById(R.id.checkBoxPressure);
        checkBoxSunMoving = findViewById(R.id.checkBoxSunMoving);
        checkBoxMoonMoving = findViewById(R.id.checkBoxMoonMoving);
        switchDarkMode = findViewById(R.id.switchDarkMode);
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

