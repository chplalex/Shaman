package com.example.myapp.Start;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.SearchRecentSuggestions;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;
import com.example.myapp.WeatherService.OpenWeatherService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOCATION_ARG;

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
        return false;
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

        suggestions = new SearchRecentSuggestions(
                fragmentActivity,
                FragmentStartSuggestionProvider.AUTHORITY,
                FragmentStartSuggestionProvider.MODE);

        findViewsById(view);
        initRetrofit();
        initViews(getWeatherLocation());
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeatherRetrofit = retrofit.create(OpenWeatherRetrofit.class);
    }

    private void requestRetrofit(String location) {
        final String appId = "bb18dcd129bad0dd351cdb2816a1aa9b";
        final String lang = "RU";
        final String units = "metric";

        openWeatherRetrofit.loadWeather(location, appId, lang, units).enqueue(new Callback<WeatherData>() {
            @Override
            public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                WeatherData wd = response.body();
                if (wd != null) {
                    wd.setResources(getResources());

                    final String name = wd.getName();
                    final String temperature = wd.getTemperature();
                    final String description = wd.getDescription();
                    final String pressure = wd.getPressure();
                    final String wind = wd.getWind();
                    final String sunMoving = wd.getSunMoving();
                    final String humidity = wd.getHumidity();

                    txtPoint.setText(name);
                    txtTemperature.setText(temperature);
                    txtWeatherDescription.setText(description);
                    txtPressure.setText(pressure);
                    txtWind.setText(wind);
                    txtSunMoving.setText(sunMoving);
                    txtHumidity.setText(humidity);
                }
            }

            @Override
            public void onFailure(Call<WeatherData> call, Throwable t) {
                txtPoint.setText(getResources().getString(R.string.not_found_location_name));
                txtTemperature.setText(getResources().getString(R.string.not_found_location_temp));
                txtWeatherDescription.setText("");
                txtPressure.setText("");
                txtWind.setText("");
                txtSunMoving.setText("");
                txtHumidity.setText("");
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
        requestRetrofit(location);

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        Resources resources = getResources();
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_pressure), true)) {
            rowPressure.setVisibility(View.VISIBLE);
        } else {
            rowPressure.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_wind), true)) {
            rowWind.setVisibility(View.VISIBLE);
        } else {
            rowWind.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_sun_moving), true)) {
            rowSunMoving.setVisibility(View.VISIBLE);
        } else {
            rowSunMoving.setVisibility(View.GONE);
        }
        if (sharedPreferences.getBoolean(resources.getString(R.string.pref_humidity), true)) {
            rowHumidity.setVisibility(View.VISIBLE);
        } else {
            rowHumidity.setVisibility(View.GONE);
        }
    }
}