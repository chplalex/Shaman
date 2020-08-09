package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }

        FragmentMain frgMain = new FragmentMain();
        FragmentDetails frgDetails = new FragmentDetails();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.containerMain, frgMain);
        fragmentTransaction.add(R.id.containerDetails, frgDetails);
        fragmentTransaction.commit();
    }

}