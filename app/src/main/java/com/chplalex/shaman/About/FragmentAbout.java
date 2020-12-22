package com.chplalex.shaman.About;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chplalex.shaman.R;

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

    private void findViewsById(View view) {
        imageFacebook = view.findViewById(R.id.image_facebook);
        imageWhatsapp = view.findViewById(R.id.image_whatsapp);
        imageEmail = view.findViewById(R.id.image_email);
        imageTelegram = view.findViewById(R.id.image_telegram);
        imageInstagram = view.findViewById(R.id.image_instagram);
        imageLinkedin = view.findViewById(R.id.image_linkedin);
    }

    private void initListenerForImages() {
        imageFacebook.setOnClickListener(v -> onFacebook());
        imageWhatsapp.setOnClickListener(v -> onWhatsapp());
        imageEmail.setOnClickListener(v -> onEmail());
        imageTelegram.setOnClickListener(v -> onTelegram());
        imageInstagram.setOnClickListener(v -> onInstagram());
        imageLinkedin.setOnClickListener(v -> onLinkedin());
    }

    private void onTelegram() {
        final String uri = "http://telegram.me/chepel_alexander";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void onEmail() {
        final String uri = "mailto:chepel.alexander@gmail.com";
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
        startActivity(intent);
    }

    private void onFacebook() {
        final String uri = "https://web.facebook.com/alexander.chepel";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void onInstagram() {
        final String uri = "https://www.instagram.com/chepel.alexander/";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void onLinkedin() {
        final String uri = "https://www.linkedin.com/in/александр-чепель-08005a97";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    private void onWhatsapp() {
        final String uri = "https://api.whatsapp.com/send?phone=79037259610";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }

    /**
     * Indicates whether the specified app ins installed and can used as an intent. This
     * method checks the package manager for installed packages that can
     * respond to an intent with the specified app. If no suitable package is
     * found, this method returns false.
     *
     * @param appName The name of the package you want to check
     * @return True if app is installed
     */
    public boolean isAppAvailable(String appName) {
        PackageManager pm = getContext().getPackageManager();
        try {
            pm.getPackageInfo(appName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
}