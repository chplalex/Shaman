package com.example.myapp.Map;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import com.example.myapp.Start.LocationData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Observable;
import java.util.Observer;

import static android.content.Context.LOCATION_SERVICE;
import static com.example.myapp.Common.Utils.LOCATION_ARG_COUNTRY;
import static com.example.myapp.Common.Utils.LOCATION_ARG_NAME;
import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class FragmentMap extends Fragment
        implements OnMyLocationButtonClickListener, OnMyLocationClickListener, OnMapReadyCallback {

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

        Context context = getContext();

        googleMap.setOnMapLongClickListener((LatLng latLng) -> {
            Log.d(LOGCAT_TAG, "setOnMapLongClickListener(), latLng = " + latLng.toString());

            LocationManager lm = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = lm.getBestProvider(criteria, false);
            Location location = new Location(provider);
            location.setLatitude(latLng.latitude);
            location.setLongitude(latLng.longitude);

            new LocationData().decodeLocation(context, location, new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    LocationData locationData = (LocationData) o;
                    Bundle bundle = new Bundle();
                    bundle.putString(LOCATION_ARG_NAME, locationData.name);
                    bundle.putString(LOCATION_ARG_COUNTRY, locationData.country);
                    getActivity().runOnUiThread(() ->
                            NavHostFragment.findNavController(getParentFragment())
                                    .navigate(R.id.fragmentStart, bundle));
                }
            });
        });

        new LocationData().getCurrentLocation(getContext(), (o, arg) -> {
            Location location = (Location) arg;
            LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
            googleMap.addMarker(new MarkerOptions().position(myLatLng).title("My current location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
        });
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.d(LOGCAT_TAG, "Current location:\n" + location);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Log.d(LOGCAT_TAG, "MyLocation button clicked");
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_no_start, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(LOGCAT_TAG, "onOptionsItemSelected(), MenuItem = " + item.toString());
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
            mapFragment.getMapAsync(this::onMapReady);
        }
    }
}