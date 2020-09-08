package com.example.myapp.Search;

import android.app.ActionBar;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import java.util.ArrayList;
import java.util.List;

public class FragmentSearch extends Fragment {

    public FragmentSearch() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_list, container, false);

        FragmentActivity fragmentActivity = getActivity();
//        Toolbar toolbar = fragmentActivity.findViewById(R.id.toolbar);
//        EditText searchText = fragmentActivity.findViewById(R.id.search_edit_text);
//        searchText.setVisibility(View.VISIBLE);

        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        SharedPreferences sharedPreferences = fragmentActivity.getPreferences(Context.MODE_PRIVATE);
        List<String> searchHistory  = new ArrayList<>(sharedPreferences.getStringSet("search_history", null));
        recyclerView.setAdapter(new AdapterSearch(searchHistory));
        return view;
    }

}