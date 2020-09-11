package com.example.myapp.Locations;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOCATION_ARG;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;

// Адаптер
public class AdapterLocations extends RecyclerView.Adapter<AdapterLocations.ViewHolder> {

    private List<String> locations;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private SharedPreferences sharedPreferences;
    private String lang;
    private String units;

    public AdapterLocations(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.locations = new ArrayList<>(sharedPreferences.getStringSet("favorites", null));
        this.openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
        this.lang = sharedPreferences.getString("pref_lang", "RU");
        this.units = sharedPreferences.getString("pref_units", "metric");
    }

    @NonNull
    @Override
    public AdapterLocations.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_locations_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterLocations.ViewHolder viewHolder, int i) {
        viewHolder.requestOpenWeatherRetrofit(locations.get(i));
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View view;
        private TextView txtLocationName;
        private ImageView imgWeatherIcon;
        private TextView txtTemperature;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            txtLocationName = itemView.findViewById(R.id.txtLocationName);
            imgWeatherIcon = itemView.findViewById(R.id.imgWeatherIcon);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            view.setOnClickListener((View v) -> {
                Bundle bundle = new Bundle();
                bundle.putCharSequence(LOCATION_ARG, txtLocationName.getText());
                Navigation.findNavController(view).navigate(R.id.actionStart, bundle);
            });
        }

        private void requestOpenWeatherRetrofit(String location) {

            openWeatherRetrofit.loadWeather(location, APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        initViewsByGoodResponse(response.body());
                    } else {
                        initViewsByFailResponse();
                    }
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    initViewsByFailResponse();
                }
            });
        }

        private void initViewsByGoodResponse(@NotNull WeatherData wd) {
            wd.setResources(view.getResources());
            txtLocationName.setText(wd.getName());
            imgWeatherIcon.setImageResource(wd.getImageResource());
            txtTemperature.setText(wd.getTemperature());
        };

        private void initViewsByFailResponse() {
            txtLocationName.setText(view.getResources().getString(R.string.not_found_location_name));
            imgWeatherIcon.setImageResource(R.drawable.ic_report_problem);
            txtTemperature.setText(view.getResources().getString(R.string.not_found_location_temp));
        }
    }
}
