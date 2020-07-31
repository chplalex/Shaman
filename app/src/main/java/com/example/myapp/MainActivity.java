package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private TextView txtPoint;
    private TextView txtTemperature;
    private TextView txtDownFallType;
    private TextView txtDownFallProbability;
    private TextView txtWindDirection;
    private TextView txtWindForce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewsById();
        setSettingsActivity();
        setCurrentWeather();
        Toast.makeText(getApplicationContext(), "MainActivity.onCreate()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Toast.makeText(getApplicationContext(), "MainActivity.onStart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(getApplicationContext(), "MainActivity.onStop()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "MainActivity.onRestart()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected  void onPause() {
        super.onPause();
        Toast.makeText(getApplicationContext(), "MainActivity.onPause()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "MainActivity.onResume()", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "MainActivity.onDestroy()", Toast.LENGTH_SHORT).show();
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