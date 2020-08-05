package com.example.myapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.myapp.Utils.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView txtPoint;
    private TextView txtTemperature;
    private TextView txtDownFallType;
    private TextView txtDownFallProbability;
    private TextView txtWindDirection;
    private TextView txtWindForce;

    // внутренний класс для сохранения параметров активити
    private static class DataContainer {
        private static Object instance;

        public CharSequence csPoint;
        public CharSequence csTemperature;
        public CharSequence csDownFallType;
        public CharSequence csDownFallProbability;
        public CharSequence csWindDirection;
        public CharSequence csWindForce;

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
        findViewsById();
        setSettingsActivity();
        setCurrentWeather();
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
    protected void onPause() {
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
        outState.putCharSequence("txtPoint", txtPoint.getText());
        outState.putCharSequence("txtTemperature", txtTemperature.getText());
        outState.putCharSequence("txtDownFallType", txtDownFallType.getText());
        outState.putCharSequence("txtDownFallProbability", txtDownFallProbability.getText());
        outState.putCharSequence("txtWindDirection", txtWindDirection.getText());
        outState.putCharSequence("txtWindForce", txtWindForce.getText());
        // второй способ. Singleton.
        DataContainer dataContainer = DataContainer.getInstance();
        dataContainer.csPoint = txtPoint.getText();
        dataContainer.csTemperature = txtTemperature.getText();
        dataContainer.csDownFallType = txtDownFallType.getText();
        dataContainer.csDownFallProbability = txtDownFallProbability.getText();
        dataContainer.csWindDirection = txtWindDirection.getText();
        dataContainer.csWindForce = txtWindForce.getText();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogStackTraceElement(getApplicationContext());
        // первый способ сохранения
        txtPoint.setText(savedInstanceState.getCharSequence("txtPoint"));
        txtTemperature.setText(savedInstanceState.getCharSequence("txtTemperature"));
        txtDownFallType.setText(savedInstanceState.getCharSequence("txtDownFallType"));
        txtDownFallProbability.setText(savedInstanceState.getCharSequence("txtDownFallProbability"));
        txtWindDirection.setText(savedInstanceState.getCharSequence("txtWindDirection"));
        txtWindForce.setText(savedInstanceState.getCharSequence("txtWindForce"));
        // второй способ сохранения с использованием Singleton
        DataContainer dataContainer = DataContainer.getInstance();
        txtPoint.setText(dataContainer.csPoint);
        txtTemperature.setText(dataContainer.csTemperature);
        txtDownFallType.setText(dataContainer.csDownFallType);
        txtDownFallProbability.setText(dataContainer.csDownFallProbability);
        txtWindDirection.setText(dataContainer.csWindDirection);
        txtWindForce.setText(dataContainer.csWindForce);
    }

    private void setSettingsActivity() {
        txtPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            }
        });
    }

    private void findViewsById() {
        txtPoint = findViewById(R.id.txtPoint);
        txtTemperature = findViewById(R.id.txtTemperature);
        txtDownFallType = findViewById(R.id.txtDownFallType);
        txtDownFallProbability = findViewById(R.id.txtDownFallProbability);
        txtWindDirection = findViewById(R.id.txtWindDirection);
        txtWindForce = findViewById(R.id.txtWindForce);
    }

    private void setCurrentWeather() {
        txtPoint.setText(getResources().getString(R.string.DebugPoint));
        txtTemperature.setText(getResources().getString(R.string.DebugTemperature));
        txtDownFallType.setText(getResources().getString(R.string.DebugDownFallType));
        txtDownFallProbability.setText(getResources().getString(R.string.DebugDawnFallProbability));
        txtWindDirection.setText(getResources().getString(R.string.DebugWindDirection));
        txtWindForce.setText(getResources().getString(R.string.DebugWindForce));
    }
}