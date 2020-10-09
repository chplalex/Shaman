package com.example.myapp.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.myapp.MainActivity;
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

    private MaterialCheckBox checkBoxPressure;
    private MaterialCheckBox checkBoxWind;
    private MaterialCheckBox checkBoxSunMoving;
    private MaterialCheckBox checkBoxHumidity;
    private MaterialRadioButton rbThemeSystem;
    private MaterialRadioButton rbThemeLight;
    private MaterialRadioButton rbThemeDark;
    private MaterialTextView txtToken;
    private MaterialButton btnClearPreferences;
    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);

        MainActivity activity = (MainActivity) getActivity();
        sharedPreferences = activity.sharedPreferences;

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
        inflater.inflate(R.menu.menu_no_start, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_start) {
            NavHostFragment.findNavController(this).navigate(R.id.actionStart, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(getString(R.string.pref_theme_system), rbThemeSystem.isChecked());
            editor.putBoolean(getString(R.string.pref_theme_light), rbThemeLight.isChecked());
            editor.putBoolean(getString(R.string.pref_theme_dark), rbThemeDark.isChecked());
            editor.apply();
            getActivity().recreate();
        };

        rbThemeSystem.setOnClickListener(rbClickListener);
        rbThemeLight.setOnClickListener(rbClickListener);
        rbThemeDark.setOnClickListener(rbClickListener);
    }

    private void initListenerForClearPreferencesButton() {
        btnClearPreferences.setOnClickListener((View view) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            initToken();
            restoreViewsValueFromSharedPreferences();
        });
    }

    private void restoreViewsValueFromSharedPreferences() {
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