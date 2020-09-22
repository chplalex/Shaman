package com.example.myapp.Start;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapp.DBService.Location;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FragmentStart extends Fragment implements
        SearchView.OnQueryTextListener,
        MenuItem.OnMenuItemClickListener,
        Observer<WeatherData> {

    // эти поля всегда на экране
    private TextView txtLocationName;
    private TextView txtLocationCountry;
    private TextView txtTemperature;
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

    private Location location;

    LocationViewModel locationViewModel;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_start);

        findViewsById(view);
        initRowsFromSharedPreferences();

        WeatherViewModel weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);
        MutableLiveData<WeatherData> liveWeatherData = weatherViewModel.getLiveData();
        liveWeatherData.observe(getViewLifecycleOwner(), this);

        WeatherData wd = new WeatherData();
        wd.name = "Буйск";

        liveWeatherData.setValue(wd);

//        locationViewModel = new ViewModelProvider(this,
//                new LocationViewModelFactory(getActivity().getApplication(), weatherViewModel))
//                .get(LocationViewModel.class);
//        locationViewModel.initData(savedInstanceState);
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("pref_loc_name", txtLocationName.getText().toString());
//        editor.putString("pref_loc_country", txtLocationCountry.getText().toString());
//        editor.apply();
//    }

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
        initFavoriteItem();
    }

    private void initFavoriteItem() {
        if (favoriteItem == null) return;
        favoriteItem.setVisible(location != null);
        if (location == null) return;
        favoriteItem.setIcon(location.favorite ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        searchItem.collapseActionView();
        if (query == null) return false;
        query = query.trim();
        if (query.length() == 0) return false;
        locationViewModel.initDataByQuery(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_start, container, false);
    }

    private void initLocationFromDB(String locationName, String locationCountry) {
//        List<Location> locations = shamanDao.getLocationByNameAndCountry(locationName, locationCountry);

        List<Location> locations = null;

        if (locations == null || locations.size() == 0) {
            location = null;
        } else {
            location = locations.get(0);
        }
    }

    private void findViewsById(View view) {
        txtLocationName = view.findViewById(R.id.txtStartName);
        txtLocationCountry = view.findViewById(R.id.txtStartCountry);
        txtTemperature = view.findViewById(R.id.txtTemperature);

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
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
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
            txtTemperature.setText(wd.getTemperature());
            txtWeatherDescription.setText(wd.getDescription());
            txtPressure.setText(wd.getPressure());
            txtWind.setText(wd.getWind());
            txtSunMoving.setText(wd.getSunMoving());
            txtHumidity.setText(wd.getHumidity());
        }
    }

    private void initViewsByFailResponse() {
        txtLocationName.setText(getString(R.string.not_found_location_name));
        txtLocationCountry.setText(getString(R.string.not_found_location_country));
        txtTemperature.setText(getString(R.string.not_found_location_temp));
        txtWeatherDescription.setText("");
        txtPressure.setText("");
        txtWind.setText("");
        txtSunMoving.setText("");
        txtHumidity.setText("");
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

    private void insertResponseIntoDB(@NotNull WeatherData wd) {
//        shamanDao.insertLocation(new Location(
//                wd.id,
//                wd.name,
//                wd.sys.country,
//                wd.coord.lon,
//                wd.coord.lat));
//        shamanDao.insertRequest(new Request(
//                wd.id,
//                wd.main.temp
//        ));
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_my_location: {
                return true;
            }
            case R.id.action_favorite: {
                location.favorite = !location.favorite;
//                shamanDao.updateLocation(location);
                initFavoriteItem();
                return true;
            }
            default:
                return false;
        }
    }

    @Override
    public void onChanged(WeatherData weatherData) {
        getActivity().runOnUiThread(() -> initViewsByWeatherData(weatherData));
    }
}