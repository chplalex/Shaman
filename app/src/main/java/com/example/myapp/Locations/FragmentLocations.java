package com.example.myapp.Locations;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

public class FragmentLocations extends Fragment {

    NestedScrollView scrollView;
    RecyclerView rvLocations;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_locations, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.setTitle(R.string.label_locations);
        }
        findViewsById(view);
        initLocations();
    }

    private void findViewsById(@NonNull View view) {
        scrollView = view.findViewById(R.id.scrollView);
        rvLocations = view.findViewById(R.id.rvLocations);
        String[] locations = getResources().getStringArray(R.array.locations);
        AdapterLocations adapter = new AdapterLocations(locations);
        rvLocations.setAdapter(adapter);
    }

    private void initLocations() {

    }

}