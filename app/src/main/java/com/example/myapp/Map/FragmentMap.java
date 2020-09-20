package com.example.myapp.Map;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapp.MainActivity;
import com.example.myapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;

public class FragmentMap extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        @Override
        public void onMapReady(GoogleMap googleMap) {

            MainActivity activity = (MainActivity) getActivity();
            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity, "Нет прав на доступ к картам", Toast.LENGTH_SHORT).show();
                return;
            }
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMapLongClickListener((LatLng latLng) -> {
                getAddressAndNavigateToStart(latLng);
            });

            if (activity.myLocation == null) {
                LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
                Criteria criteria = new Criteria();
                String bestProvider = lm.getBestProvider(criteria, false);
                if (bestProvider != null) activity.myLocation = lm.getLastKnownLocation(bestProvider);
            }

            if (activity.myLocation == null) {
                Toast.makeText(activity, "Текущее местоположение не определено", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng myLatLng = new LatLng(activity.myLocation.getLatitude(), activity.myLocation.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(myLatLng).title("My current location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        }
    };

    // Получаем адрес по координатам
    private void getAddressAndNavigateToStart(final LatLng latLng) {
        final Geocoder geocoder = new Geocoder(getContext());
        // Поскольку Geocoder работает по интернету, создаём отдельный поток
        new Thread(() -> {
            try {
                final List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                String country = addresses.get(0).getCountryCode();
                String location = addresses.get(0).getLocality();
                Activity activity = getActivity();
                if (country == null || location == null) {
                    activity.runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Населённый пункт не определён", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    activity.runOnUiThread(() -> {
                        Bundle bundle = new Bundle();
                        bundle.putCharSequence(LOCATION_ARG_NAME, location);
                        bundle.putCharSequence(LOCATION_ARG_COUNTRY, country);
                        NavHostFragment.findNavController(this).navigate(R.id.fragmentStart, bundle);
                    });

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_map);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}