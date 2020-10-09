package com.example.myapp.Favorites;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.DBService.Location;
import com.example.myapp.DBService.ShamanDao;
import com.example.myapp.MainApp;
import com.example.myapp.R;
import com.example.myapp.WeatherData.WeatherData;
import com.example.myapp.WeatherService.OpenWeatherRetrofit;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.APP_ID;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.BASE_URL;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.HTTP;
import static com.example.myapp.WeatherService.OpenWeatherRetrofit.HTTPS;

public class AdapterFavorites extends RecyclerView.Adapter<AdapterFavorites.ViewHolder> {

    private ShamanDao shamanDao;
    private List<Location> locations;
    private OpenWeatherRetrofit openWeatherRetrofit;
    private String lang;
    private String units;
    private Activity activity;

    public AdapterFavorites(Activity activity, SharedPreferences sharedPreferences) {
        this.activity = activity;
        shamanDao = MainApp.getInstance().getShamanDao();
        locations = shamanDao.getFavoriteLocations();

        String baseURL;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            baseURL = HTTPS + BASE_URL;
        } else {
            baseURL = HTTP + BASE_URL;
        }

        openWeatherRetrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherRetrofit.class);
        lang = sharedPreferences.getString("pref_lang", "RU");
        units = sharedPreferences.getString("pref_units", "metric");
    }

    @NonNull
    @Override
    public AdapterFavorites.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.fragment_favorites_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterFavorites.ViewHolder viewHolder, int i) {
        viewHolder.location = locations.get(i);
        viewHolder.requestOpenWeatherRetrofit();
    }

    @Override
    public int getItemCount() { return locations.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Location location;
        private View view;
        private MaterialTextView txtFavoriteName;
        private MaterialTextView txtFavoriteCountry;
        private MaterialTextView txtTemperature;
        private ShapeableImageView imgWeatherIcon;
        private ImageButton btnFavoriteDelete;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            txtFavoriteName = itemView.findViewById(R.id.txtFavoriteName);
            txtFavoriteCountry = itemView.findViewById(R.id.txtFavoriteCountry);
            imgWeatherIcon = itemView.findViewById(R.id.imgWeatherIcon);
            txtTemperature = itemView.findViewById(R.id.txtTemperature);
            btnFavoriteDelete = itemView.findViewById(R.id.btnFavoriteDelete);

            view.setOnClickListener((View view) -> {
                Bundle bundle = new Bundle();
                bundle.putCharSequence(LOCATION_ARG_NAME, location.name);
                bundle.putCharSequence(LOCATION_ARG_COUNTRY, location.country);
                Navigation.findNavController(view).navigate(R.id.actionStart, bundle);
            });

            btnFavoriteDelete.setOnClickListener((View view) -> {
                location.favorite = false;
                new Thread(() -> {
                    shamanDao.updateLocation(location);
                }).start();
                locations.remove(location);
                notifyItemRemoved(getAdapterPosition());
            });
        }

        private void requestOpenWeatherRetrofit() {

            new Thread(() -> {
                openWeatherRetrofit.loadWeather(location.name + "," + location.country, APP_ID, lang, units).enqueue(new Callback<WeatherData>() {
                    @Override
                    public void onResponse(Call<WeatherData> call, Response<WeatherData> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            activity.runOnUiThread(() -> {initViewsByGoodResponse(response.body());});
                        } else {
                            activity.runOnUiThread(() -> {initViewsByFailResponse();});
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherData> call, Throwable t) {
                            activity.runOnUiThread(() -> {initViewsByFailResponse();});
                    }
                });
            }).start();

        }

        private void initViewsByGoodResponse(@NotNull WeatherData wd) {
            wd.setResources(view.getResources());
            txtFavoriteName.setText(wd.getName());
            txtFavoriteCountry.setText(wd.getCountry());
            imgWeatherIcon.setImageResource(wd.getImageResource());
            txtTemperature.setText(wd.getTempString());
        };

        private void initViewsByFailResponse() {
            txtFavoriteName.setText(view.getResources().getString(R.string.not_found_location_name));
            txtFavoriteCountry.setText(view.getResources().getString(R.string.not_found_location_country));
            imgWeatherIcon.setImageResource(R.drawable.ic_report_problem);
            txtTemperature.setText(view.getResources().getString(R.string.not_found_location_temp));
        }
    }
}
