package com.chplalex.shaman.Auth;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.chplalex.shaman.R;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.chplalex.shaman.Common.Utils.LOGCAT_TAG;

public class ActivityAuth extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private final String serverClientId =
            "243403678652-skh52jh7ogj3ddf4n4ap3ivok2hf7030.apps.googleusercontent.com";
            //"243403678652-7ufmm0gi80025dhbml8gp6afqgupc480.apps.googleusercontent.com";

    private final int RC_SIGN_IN = 100;
    private final String TAG = "Google SignIn";

    private SignInButton btnSignIn;
    private SignInButton btnSignOut;
    private EditText txtUserInfo;
    private ImageView imgUserAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_auth);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(serverClientId)
                .requestServerAuthCode(serverClientId, false)
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        btnSignIn = findViewById(R.id.btnSingIn);
        btnSignOut = findViewById(R.id.btnSingOut);
        txtUserInfo = findViewById(R.id.txtUserInfo);
        imgUserAvatar = findViewById(R.id.imgUserAvatar);

        btnSignIn.setSize(SignInButton.SIZE_WIDE);
        btnSignIn.setOnClickListener(v -> singIn());

        btnSignOut.setSize(SignInButton.SIZE_WIDE);
        btnSignOut.setOnClickListener(v -> singOut());
        for (int i = 0; i < btnSignOut.getChildCount(); i++) {
            View view = btnSignOut.getChildAt(i);
            if (view instanceof TextView) {
                TextView txtView = (TextView) view;
                txtView.setText(R.string.label_sing_out);
                break;
            }
        }
    }

    private void singIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void singOut() {
        googleSignInClient.signOut()
                .addOnCompleteListener(this, task -> updateUI(null));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String token = account.getIdToken();
            Log.d(LOGCAT_TAG, "signInResult:success code=" + token);

            // Signed in successfully, show authenticated UI.
            if(!TextUtils.isEmpty(token)) {
                updateUI(account);
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.d(LOGCAT_TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(GoogleSignInAccount account) {
        if (account == null) {
            btnSignIn.setVisibility(VISIBLE);
            btnSignOut.setVisibility(GONE);
            txtUserInfo.setVisibility(GONE);
            imgUserAvatar.setVisibility(GONE);
        } else {
            btnSignIn.setVisibility(GONE);
            btnSignOut.setVisibility(VISIBLE);
            txtUserInfo.setVisibility(VISIBLE);
            imgUserAvatar.setVisibility(VISIBLE);

            txtUserInfo.setText(new StringBuilder()
                    .append("Display name is ")
                    .append(account.getDisplayName())
                    .append("\n\n")
                    .append("Email is ")
                    .append(account.getEmail())
                    .append("\n\n")
                    .append("Family name is ")
                    .append(account.getFamilyName())
                    .append("\n\n")
                    .append("Given name is ")
                    .append(account.getGivenName()));

            Uri uri = account.getPhotoUrl();
            if (uri == null) {
                imgUserAvatar.setImageResource(R.drawable.ic_avatar);
            } else {
                Glide.with(this).load(uri.toString()).into(imgUserAvatar);
            }
        }
    }
}
