package com.example.myapp.History;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapp.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapp.Common.Utils.LOGCAT_TAG;

public class FragmentHistory extends Fragment implements View.OnClickListener {

    EditText searchText;

    public FragmentHistory() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.rvHistory);

//        SharedPreferences sharedPreferences = fragmentActivity.getPreferences(Context.MODE_PRIVATE);
//        List<String> searchHistory  = new ArrayList<>(sharedPreferences.getStringSet("search_history", null));
//        recyclerView.setAdapter(new AdapterHistory(searchHistory));

        return view;
    }

    @Override
    public void onClick(View view) {
        String s = searchText.getText().toString();
        Log.d(LOGCAT_TAG, s);
    }
}