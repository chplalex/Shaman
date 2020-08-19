package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        setContentView(R.layout.activity_main);
    }

}