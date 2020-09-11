package com.example.myapp.Start;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.provider.SearchRecentSuggestions;
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
import com.example.myapp.MainApp;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOCATION_ARG;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

public class FragmentStart extends Fragment implements SearchView.OnQueryTextListener {

    // эти поля всегда на экране
    private TextView txtPoint;
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
    private SearchView searchView;
    private SearchRecentSuggestions suggestions;

    private OpenWeatherRetrofit openWeatherRetrofit;
    private ShamanDao shamanDao;

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        suggestions.saveRecentQuery(query, null);
        initViews(query);
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fragmentActivity = getActivity();
        fragmentActivity.setTitle(R.string.label_start);

        shamanDao = MainApp.getInstance().getShamanDao();
        suggestions = new SearchRecentSuggestions(
                fragmentActivity,
                FragmentStartSuggestionProvider.AUTHORITY,
                FragmentStartSuggestionProvider.MODE);

        findViewsById(view);
        initOpenWeatherRetrofit();
        initViews(getWeatherLocation());
    }

    private void initOpenWeatherRetrofit() {
        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
    }

    private void requestOpenWeatherRetrofit(String location) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String lang = sharedPreferences.getString("pref_lang", "RU");
        final String units = sharedPreferences.getString("pref_units", "metric");

        openWeatherRetrofit.loadWeather(location, APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                if (response.isSuccessful() && response.body() != null) {
                    initViewsByGoodResponse(response.body());
                    insertResponseIntoDB(response.body());
                } else {
                    initViewsByFailResponse();
                    try {
                        showAlert("Запрос завершён с ошибкой", response.errorBody().string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                initViewsByFailResponse();
                showAlert("Ошибка связи с погодным сервером");
            }

        });
    }

    private String getWeatherLocation() {
        Bundle arguments = getArguments();
        if (arguments == null || arguments.getCharSequence(LOCATION_ARG) == null) {
            return getResources().getString(R.string.DebugPoint);
        } else {
            return arguments.getCharSequence(LOCATION_ARG).toString();
        }
    }

    private void findViewsById(View view) {
        txtPoint = view.findViewById(R.id.txtPoint);
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

    private void initViews(String location) {
        requestOpenWeatherRetrofit(location);

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
        txtPoint.setText(wd.getName());
        txtTemperature.setText(wd.getTemperature());
        txtWeatherDescription.setText(wd.getDescription());
        txtPressure.setText(wd.getPressure());
        txtWind.setText(wd.getWind());
        txtSunMoving.setText(wd.getSunMoving());
        txtHumidity.setText(wd.getHumidity());
    }

    private void initViewsByFailResponse() {
        txtPoint.setText(getString(R.string.not_found_location_name));
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
                wd.coord.lat
        ));
        shamanDao.insertRequest(new Request(
                wd.id,
                wd.main.temp
        ));
    }

}