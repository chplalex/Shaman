package com.example.myapp.About;

import android.app.ActionBar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.myapp.R;

public class FragmentAbout extends Fragment {

    private ImageView imageFacebook;
    private ImageView imageWhatsapp;
    private ImageView imageEmail;
    private ImageView imageTelegram;
    private ImageView imageInstagram;
    private ImageView imageLinkedin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(R.string.label_about);
        findViewsById(view);
        initListenerForImages();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.action_search).setVisible(false);
        menu.findItem(R.id.action_my_location).setVisible(false);
        menu.findItem(R.id.action_favorite).setVisible(false);
        menu.findItem(R.id.action_start).setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_start) {
            NavHostFragment.findNavController(this).navigate(R.id.actionStart, null);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findViewsById(View view) {
        imageFacebook = view.findViewById(R.id.image_facebook);
        imageWhatsapp = view.findViewById(R.id.image_whatsapp);
        imageEmail = view.findViewById(R.id.image_email);
        imageTelegram = view.findViewById(R.id.image_telegram);
        imageInstagram = view.findViewById(R.id.image_instagram);
        imageLinkedin = view.findViewById(R.id.image_linkedin);
    }

    private void initListenerForImages() {
        View.OnClickListener clickListener = view -> Toast.makeText(getContext(), "Обработка нажатия будет добавлена в следующем релизе", Toast.LENGTH_SHORT).show();
        imageFacebook.setOnClickListener(clickListener);
        imageWhatsapp.setOnClickListener(clickListener);
        imageEmail.setOnClickListener(clickListener);
        imageTelegram.setOnClickListener(clickListener);
        imageInstagram.setOnClickListener(clickListener);
        imageLinkedin.setOnClickListener(clickListener);
    }
}