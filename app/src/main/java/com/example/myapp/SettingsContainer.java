package com.example.myapp;

import java.io.Serializable;

public class SettingsContainer implements Serializable {
    private static SettingsContainer instance;

    public int selectedItemWeatherPoint;
    public boolean isChkBoxPressure;
    public boolean isChkBoxWind;
    public boolean isChkBoxSun;
    public boolean isChkBoxMoon;
    public boolean isChkDarkMode;

    public static SettingsContainer getInstance() {
        if (instance == null) {
            instance = new SettingsContainer();
            // по умолчанию выбирается пункт № 0 из списка
            // и все поля как необязательные находятся в режиме "невидимы"
        }
        return instance;
    }

    public void copySettings (SettingsContainer sc) {
        selectedItemWeatherPoint = sc.selectedItemWeatherPoint;
        isChkBoxPressure = sc.isChkBoxPressure;
        isChkBoxWind = sc.isChkBoxWind;
        isChkBoxSun = sc.isChkBoxSun;
        isChkBoxMoon = sc.isChkBoxMoon;
        isChkDarkMode = sc.isChkDarkMode;
    }
}

