package com.example.myapp.Start;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapp.DBService.Location;
import com.example.myapp.DBService.Request;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

public class FragmentStart extends Fragment implements SearchView.OnQueryTextListener {

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
    private MenuItem myLocationItem;

    private OpenWeatherRetrofit openWeatherRetrofit;
    private ShamanDao shamanDao;

    private Location location;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_start);

        findViewsById(view);

        ViewModelWeather vmStart = ViewModelProviders.of(this).get(ViewModelWeather.class);
        LiveData<WeatherData> dataStartWeather = vmStart.getData(getLocationName(), getLocationCountry());
        dataStartWeather.observe(getViewLifecycleOwner(), new Observer<WeatherData>() {
            @Override
            public void onChanged(WeatherData weatherData) {
                initViewsByWeatherData(weatherData);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("pref_loc_name", txtLocationName.getText().toString());
        editor.putString("pref_loc_country", txtLocationCountry.getText().toString());
        editor.apply();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_start, menu);

        searchItem = menu.findItem(R.id.action_search);
        favoriteItem = menu.findItem(R.id.action_favorite);
        myLocationItem = menu.findItem(R.id.action_my_location);

        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        favoriteItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                location.favorite = !location.favorite;
                shamanDao.updateLocation(location);
                initFavoriteItem();
                return true;
            }
        });
        initFavoriteItem();

        myLocationItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                initViewsByCurrentLocation();
                return true;
            }
        });

    }

    private void initViewsByCurrentLocation() {
        final MainActivity activity = (MainActivity) getActivity();

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(activity, "У приложения нет прав на запрос местоположения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (activity.myLocation == null) {
            LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String bestProvider = lm.getBestProvider(criteria, false);
            if (bestProvider != null) activity.myLocation = lm.getLastKnownLocation(bestProvider);
        }

        if (activity.myLocation == null) {
            showAlert("Нет доступа к текущей позиции");
            return;
        }

        final Geocoder geocoder = new Geocoder(activity);
        new Thread(() -> {
            try {
                final List<Address> addresses = geocoder.getFromLocation(activity.myLocation.getLatitude(), activity.myLocation.getLongitude(), 1);
                String locationCountry = addresses.get(0).getCountryCode();
                String locationName = addresses.get(0).getLocality();
                if (locationCountry == null || locationName == null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(activity, "Населённый пункт не определён", Toast.LENGTH_SHORT).show();
                        initViewsByFailResponse();
                        location = null;
                        initFavoriteItem();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        initViewsByLocation(locationName, locationCountry);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initFavoriteItem() {
        if (favoriteItem == null) return;
        favoriteItem.setVisible(location != null);
        if (location == null) return;
        favoriteItem.setIcon(location.favorite ? R.drawable.ic_favorite_yes : R.drawable.ic_favorite_no);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        initViewsByLocation(query, "");
        searchItem.collapseActionView();
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

    private void initOpenWeatherRetrofit() {
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
    }

    private void initLocationFromDB(String locationName, String locationCountry) {
        List<Location> locations = shamanDao.getLocationByNameAndCountry(locationName, locationCountry);

        if (locations == null || locations.size() == 0) {
            location = null;
        } else {
            location = locations.get(0);
        }
    }

    private String getLocationName() {
        String name;
        Bundle arguments = getArguments();
        if (arguments == null || arguments.getCharSequence(LOCATION_ARG_NAME) == null) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            name = sharedPreferences.getString("pref_loc_name", null);
        } else {
            name = arguments.getCharSequence(LOCATION_ARG_NAME).toString();
        }
        if (name == null || name.length() == 0)
            return null;
        else
            return name;
    }

    private String getLocationCountry() {
        String country;
        Bundle arguments = getArguments();
        if (arguments == null || arguments.getCharSequence(LOCATION_ARG_COUNTRY) == null) {
            SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
            country = sharedPreferences.getString("pref_loc_country", null);
        } else {
            country = arguments.getCharSequence(LOCATION_ARG_COUNTRY).toString();
        }
        if (country == null || country.length() == 0)
            return null;
        else
            return country;
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

    private void initViewsByLocation(String locationName, String locationCountry) {
        initLocationFromDB(locationName, locationCountry);
        initFavoriteItem();

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

    private void initViewsByGoodResponse(@NotNull WeatherData wd) {
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

    private void initViewsByWeatherData(WeatherData wd) {
        if (wd == null) {
            txtLocationName.setText(getString(R.string.not_found_location_name));
            txtLocationCountry.setText(getString(R.string.not_found_location_country));
            txtTemperature.setText(getString(R.string.not_found_location_temp));
            txtWeatherDescription.setText("");
            txtPressure.setText("");
            txtWind.setText("");
            txtSunMoving.setText("");
            txtHumidity.setText("");
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
        shamanDao.insertLocation(new Location(
                wd.id,
                wd.name,
                wd.sys.country,
                wd.coord.lon,
                wd.coord.lat));
        shamanDao.insertRequest(new Request(
                wd.id,
                wd.main.temp
        ));
    }

}