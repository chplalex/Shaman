package com.example.myapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;

public class FragmentSettings extends Fragment {

    ScrollView scrollView;
    MaterialCheckBox checkBoxPressure;
    MaterialCheckBox checkBoxWind;
    MaterialCheckBox checkBoxSunMoving;
    MaterialCheckBox checkBoxHumidity;
    MaterialRadioButton rbThemeSystem;
    MaterialRadioButton rbThemeLight;
    MaterialRadioButton rbThemeDark;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.setTitle(R.string.label_settings);
        }
        findViewsById(view);
        restoreViewsValueFromSettingsContainer();
        initListenerForCheckButtons();
        initListenerForThemesButtons();
    }

    private void findViewsById(View view) {
        scrollView = view.findViewById(R.id.scrollView);
        checkBoxPressure = view.findViewById(R.id.checkBoxPressure);
        checkBoxWind = view.findViewById(R.id.checkBoxWind);
        checkBoxSunMoving = view.findViewById(R.id.checkBoxSunMoving);
        checkBoxHumidity = view.findViewById(R.id.checkBoxHumidity);
        rbThemeSystem = view.findViewById(R.id.rbThemeSystem);
        rbThemeLight = view.findViewById(R.id.rbThemeLight);
        rbThemeDark = view.findViewById(R.id.rbThemeDark);
    }

    private void initListenerForCheckButtons() {

        View.OnClickListener chkClickListener = view -> {
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.isChkBoxPressure = checkBoxPressure.isChecked();
            sc.isChkBoxWind = checkBoxWind.isChecked();
            sc.isChkBoxSunMoving = checkBoxSunMoving.isChecked();
            sc.isChkBoxHumidity = checkBoxHumidity.isChecked();
        };

        checkBoxHumidity.setOnClickListener(chkClickListener);
        checkBoxPressure.setOnClickListener(chkClickListener);
        checkBoxSunMoving.setOnClickListener(chkClickListener);
        checkBoxWind.setOnClickListener(chkClickListener);
    }

    private void initListenerForThemesButtons() {

        View.OnClickListener rbClickListener = view -> {
            SettingsContainer sc = SettingsContainer.getInstance();
            sc.isThemeSystem = rbThemeSystem.isChecked();
            sc.isThemeLight = rbThemeLight.isChecked();
            sc.isThemeDark = rbThemeDark.isChecked();
            FragmentActivity fa = getActivity();
            if (fa != null) {
                fa.recreate();
            }
        };

        rbThemeSystem.setOnClickListener(rbClickListener);
        rbThemeLight.setOnClickListener(rbClickListener);
        rbThemeDark.setOnClickListener(rbClickListener);
    }

    private void restoreViewsValueFromSettingsContainer() {
        SettingsContainer sc = SettingsContainer.getInstance();
        checkBoxPressure.setChecked(sc.isChkBoxPressure);
        checkBoxWind.setChecked(sc.isChkBoxWind);
        checkBoxSunMoving.setChecked(sc.isChkBoxSunMoving);
        checkBoxHumidity.setChecked(sc.isChkBoxHumidity);
        rbThemeSystem.setChecked(sc.isThemeSystem);
        rbThemeLight.setChecked(sc.isThemeLight);
        rbThemeDark.setChecked(sc.isThemeDark);
    }
}