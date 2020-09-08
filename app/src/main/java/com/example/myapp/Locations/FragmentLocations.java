package com.example.myapp.Locations;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

public class FragmentLocations extends Fragment {

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

        FragmentActivity fragmentActivity = getActivity();
        fragmentActivity.setTitle(R.string.label_locations);

        RecyclerView rvLocations = view.findViewById(R.id.rvLocations);
        rvLocations.setAdapter(new AdapterLocations(fragmentActivity.getPreferences(Context.MODE_PRIVATE)));
    }

}