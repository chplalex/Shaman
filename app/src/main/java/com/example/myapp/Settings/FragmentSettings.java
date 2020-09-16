package com.example.myapp.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class FragmentSettings extends Fragment {

    MaterialCheckBox checkBoxPressure;
    MaterialCheckBox checkBoxWind;
    MaterialCheckBox checkBoxSunMoving;
    MaterialCheckBox checkBoxHumidity;
    MaterialRadioButton rbThemeSystem;
    MaterialRadioButton rbThemeLight;
    MaterialRadioButton rbThemeDark;
    MaterialTextView txtToken;
    MaterialButton btnClearPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_settings);
        findViewsById(view);
        restoreViewsValueFromSharedPreferences();
        initToken();
        initListenerForCheckButtons();
        initListenerForThemesButtons();
        initListenerForClearPreferencesButton();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_my_location).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
    }

    private void initToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("PushMessage", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        txtToken.setText(token);
                    }
                });
    }


    private void findViewsById(View view) {
        checkBoxPressure = view.findViewById(R.id.checkBoxPressure);
        checkBoxWind = view.findViewById(R.id.checkBoxWind);
        checkBoxSunMoving = view.findViewById(R.id.checkBoxSunMoving);
        checkBoxHumidity = view.findViewById(R.id.checkBoxHumidity);
        rbThemeSystem = view.findViewById(R.id.rbThemeSystem);
        rbThemeLight = view.findViewById(R.id.rbThemeLight);
        rbThemeDark = view.findViewById(R.id.rbThemeDark);
        txtToken = view.findViewById(R.id.txtToken);
        btnClearPreferences = view.findViewById(R.id.btnClearPreferences);
    }

    private void initListenerForCheckButtons() {

        View.OnClickListener chkClickListener = view -> {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_pressure), checkBoxPressure.isChecked());
            editor.putBoolean(getString(R.string.pref_wind), checkBoxWind.isChecked());
            editor.putBoolean(getString(R.string.pref_sun_moving), checkBoxSunMoving.isChecked());
            editor.putBoolean(getString(R.string.pref_humidity), checkBoxHumidity.isChecked());
            editor.apply();
        };

        checkBoxHumidity.setOnClickListener(chkClickListener);
        checkBoxPressure.setOnClickListener(chkClickListener);
        checkBoxSunMoving.setOnClickListener(chkClickListener);
        checkBoxWind.setOnClickListener(chkClickListener);
    }

    private void initListenerForThemesButtons() {

        View.OnClickListener rbClickListener = view -> {
            FragmentActivity fragmentActivity = getActivity();
            SharedPreferences sharedPreferences = fragmentActivity.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putBoolean(getString(R.string.pref_theme_system), rbThemeSystem.isChecked());
            editor.putBoolean(getString(R.string.pref_theme_light), rbThemeLight.isChecked());
            editor.putBoolean(getString(R.string.pref_theme_dark), rbThemeDark.isChecked());
            editor.apply();

            fragmentActivity.recreate();
        };

        rbThemeSystem.setOnClickListener(rbClickListener);
        rbThemeLight.setOnClickListener(rbClickListener);
        rbThemeDark.setOnClickListener(rbClickListener);
    }

    private void initListenerForClearPreferencesButton() {
        btnClearPreferences.setOnClickListener((View view) -> {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            initToken();
            restoreViewsValueFromSharedPreferences();
        });
    }

    private void restoreViewsValueFromSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        checkBoxPressure.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_pressure), true));
        checkBoxWind.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_wind), true));
        checkBoxSunMoving.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_sun_moving), true));
        checkBoxHumidity.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_humidity), true));
        rbThemeSystem.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_theme_system), true));
        rbThemeLight.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_theme_light), false));
        rbThemeDark.setChecked(sharedPreferences.getBoolean(getString(R.string.pref_theme_dark), false));
        txtToken.setText(sharedPreferences.getString(getString(R.string.pref_token), ""));
    }

}