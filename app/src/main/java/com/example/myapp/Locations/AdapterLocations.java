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
import com.example.myapp.WeatherData.Main;
import com.example.myapp.WeatherData.Weather;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;
import com.example.myapp.WeatherService.OpenWeatherService;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOCATION_ARG;

// Адаптер
public class AdapterLocations extends RecyclerView.Adapter<AdapterLocations.ViewHolder> {

    private List<String> locations;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private SharedPreferences sharedPreferences;

    public AdapterLocations(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.locations = new ArrayList<String>(sharedPreferences.getStringSet("favorites", null));
        initRetrofit();
    }

    private void initRetrofit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeatherRetrofit = retrofit.create(OpenWeatherRetrofit.class);
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
        viewHolder.setLocation(locations.get(i));
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

        public void setLocation(String location) {
            final String appId = "bb18dcd129bad0dd351cdb2816a1aa9b";
            final String lang = "RU";
            final String units = "metric";

            openWeatherRetrofit.loadWeather(location, appId, lang, units).enqueue(new Callback<WeatherData>() {
                @Override
                public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                    WeatherData wd = response.body();
                    if (wd != null) {
                        wd.setResources(view.getResources());

                        final String name = wd.getName();
                        final int imageResource = wd.getImageResource();
                        final String temperature = wd.getTemperature();

                        txtLocationName.setText(name);
                        imgWeatherIcon.setImageResource(imageResource);
                        txtTemperature.setText(temperature);
                    }
                }

                @Override
                public void onFailure(Call<WeatherData> call, Throwable t) {
                    txtLocationName.setText(view.getResources().getString(R.string.not_found_location_name));
                    imgWeatherIcon.setImageResource(R.drawable.ic_report_problem);
                    txtTemperature.setText(view.getResources().getString(R.string.not_found_location_temp));
                }
            });
        }
    }
}
