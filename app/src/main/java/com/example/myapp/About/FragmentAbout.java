package com.example.myapp.About;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.myapp.R;

public class FragmentAbout extends Fragment {

    ImageView imageFacebook;
    ImageView imageWhatsapp;
    ImageView imageEmail;
    ImageView imageTelegram;
    ImageView imageInstagram;
    ImageView imageLinkedin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FragmentActivity fa = getActivity();
        if (fa != null) {
            fa.setTitle(R.string.label_about);
        }
        findViewsById(view);
        initListenerForImages();
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