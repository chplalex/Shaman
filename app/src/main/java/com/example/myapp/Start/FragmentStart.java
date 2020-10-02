package com.example.myapp.Start;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;

public class FragmentStart extends Fragment implements
        SearchView.OnQueryTextListener,
        MenuItem.OnMenuItemClickListener {

    // эти поля всегда на экране
    private TextView txtLocationName;
    private TextView txtLocationCountry;
    private TempView tempView;
    private TextView txtWeatherDescription;

    // эти поля видны при выборе пользователем установок
    private TableRow rowPressure;
    private TextView txtPressure;

    private TableRow rowWind;
    private TextView txtWind;

    private TableRow rowSunMoving;
    private TextView txtSunMoving;

    private TableRow rowHumidity;
    private TextView txtHumidity;

    private MenuItem searchItem;
    private MenuItem favoriteItem;

    private ViewModelStart vmStart;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MainActivity activity = (MainActivity) getActivity();
        sharedPreferences = activity.sharedPreferences;

        // создаем модель для фрагмента
        vmStart = new ViewModelProvider(this).get(ViewModelStart.class);
        // устанавливаем местоположение по цепочке:
        // 1. getArguments() (переход из Favorites и History)
        // 2. (если нет, то) SharedPreferences
        // 3. (если нет, то) Current Location
        // 4. (если нет, то) null
        // Если местоположение != null, то -> аснихронная загрузка данных loadData()
        vmStart.initLocationData(getArguments());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_start);

        findViewsById(view);
        initRowsFromSharedPreferences();

        LifecycleOwner owner = getViewLifecycleOwner();

        // Подписываем модель саму на себя на изменения данных о местоположении
        vmStart.getLiveLocationData().observe(owner, vmStart);
        // Подписываемся на изменения LiveData с погодными данными
        vmStart.getLiveWeatherData().observe(owner, this::initViewsByWeatherData);
        // Подписываемся на изменения LiveData с данными Избранное/Неизбранное
        vmStart.getLiveFavoriteData().observe(owner, this::initViewsByFavoriteData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_start, menu);

        menu.findItem(R.id.action_my_location).setOnMenuItemClickListener(this);

        searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        favoriteItem = menu.findItem(R.id.action_favorite);
        favoriteItem.setOnMenuItemClickListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchItem.collapseActionView();
        if (query == null || query.trim().length() == 0) return false;
        vmStart.initLocationDataByQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void findViewsById(View view) {
        txtLocationName = view.findViewById(R.id.txtStartName);
        txtLocationCountry = view.findViewById(R.id.txtStartCountry);
        tempView = view.findViewById(R.id.tempView);

        txtWeatherDescription = view.findViewById(R.id.txtWeather);

        rowPressure = view.findViewById(R.id.rowPressure);
        txtPressure = view.findViewById(R.id.txtPressure);

        rowWind = view.findViewById(R.id.rowWind);
        txtWind = view.findViewById(R.id.txtWind);

        rowSunMoving = view.findViewById(R.id.rowSunMoving);
        txtSunMoving = view.findViewById(R.id.txtSunMoving);

        rowHumidity = view.findViewById(R.id.rowHumidity);
        txtHumidity = view.findViewById(R.id.txtHumidity);
    }

    private void initRowsFromSharedPreferences() {
        if (sharedPreferences.getBoolean(getString(R.string.pref_pressure), true)) {
            rowPressure.setVisibility(View.VISIBLE);
        } else {
            rowPressure.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(getString(R.string.pref_wind), true)) {
            rowWind.setVisibility(View.VISIBLE);
        } else {
            rowWind.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(getString(R.string.pref_sun_moving), true)) {
            rowSunMoving.setVisibility(View.VISIBLE);
        } else {
            rowSunMoving.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(getString(R.string.pref_humidity), true)) {
            rowHumidity.setVisibility(View.VISIBLE);
        } else {
            rowHumidity.setVisibility(View.GONE);
        }
    }

    private void initViewsByWeatherData(WeatherData wd) {
        if (wd == null) {
            initViewsByFailResponse();
        } else {
            wd.setResources(getResources());
            txtLocationName.setText(wd.getName());
            txtLocationCountry.setText(wd.getCountry());
            tempView.setTemp(wd.getTemp());
            txtWeatherDescription.setText(wd.getDescription());
            txtPressure.setText(wd.getPressure());
            txtWind.setText(wd.getWind());
            txtSunMoving.setText(wd.getSunMoving());
            txtHumidity.setText(wd.getHumidity());

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("pref_loc_name", wd.getName());
            editor.putString("pref_loc_country", wd.getCountry());
            editor.apply();
        }
    }

    private void initViewsByFailResponse() {
        txtLocationName.setText(getString(R.string.not_found_location_name));
        txtLocationCountry.setText("--");
        tempView.setUncertain();
        txtWeatherDescription.setText("--");
        txtPressure.setText("--");
        txtWind.setText("--");
        txtSunMoving.setText("--");
        txtHumidity.setText("--");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("pref_loc_name");
        editor.remove("pref_loc_country");
        editor.apply();
    }

    private void initViewsByFavoriteData(Boolean isFavorite) {
        if (favoriteItem == null) return;
        if (isFavorite == null) {
            favoriteItem.setVisible(false);
        } else {
            favoriteItem.setVisible(true);
            favoriteItem.setIcon(isFavorite ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
        }
    }

    private void showAlert(String... msg) {
        new AlertDialog.Builder(getContext())
                .setTitle("Проблема")
                .setItems(msg, null)
                .setIcon(R.drawable.ic_error)
                .setCancelable(true)
                .setPositiveButton("Понятно", null)
                .create()
                .show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_location: {
                vmStart.initLocationDataByCurrentLocation();
                return true;
            }
            case R.id.action_favorite: {
                vmStart.reverseFavorite();
                return true;
            }
            default:
                return false;
        }
    }
}