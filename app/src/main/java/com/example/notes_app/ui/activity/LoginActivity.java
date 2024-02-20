package com.example.notes_app.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes_app.R;
import com.example.notes_app.databinding.LoginMainBinding;
import com.example.notes_app.model.UserData;
import com.example.notes_app.ui.fragment.HomeFragment;
import com.example.notes_app.utils.Toaster;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    // Declaring all the variable used in authentication
    private GoogleSignInOptions mGoogleSignInOptions;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 1;


    private Toaster toaster = new Toaster();

    // view binding variable
    private LoginMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        //Making status bar color as main color
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.mainColor));


        SharedPreferences preferences = getSharedPreferences("userDetail", MODE_PRIVATE);
        String userId = preferences.getString("userId", null);

        // Checking if there is data exist then move to Home fragment page
        if (userId != null) {
            HomeFragment homeFragment = HomeFragment.newInstance(userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
            fragmentTransaction.commit();
            binding.relativeLayout.setVisibility(View.GONE);
        }


        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.progressBar.setVisibility(View.VISIBLE);
                signIn();
            }

        });
    }


    private void signIn() {
        Intent signIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                openHomeFragment(account);
            } catch (ApiException e) {
                int status = e.getStatusCode();
                // Show Toast according to exception
                if (status == 7) {
                    toaster.showToast(this, getString(R.string.networkMsg));

                } else if (status == 12501) {
                    toaster.showToast(this, getString(R.string.cancel));

                } else {
                    toaster.showToast(this, getString(R.string.signIn) + e.getStatusCode());

                }
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void openHomeFragment(GoogleSignInAccount account) {
        if (account != null) {
            String userId = account.getId();
            String name = account.getDisplayName();
            String email = account.getEmail();
            String pic = String.valueOf(account.getPhotoUrl());
            binding.relativeLayout.setVisibility(View.GONE);

            UserData userData = new UserData(name, email, pic, userId);

            //Store user information in shared preference
            SharedPreferences.Editor editor = getSharedPreferences("userDetail", MODE_PRIVATE).edit();
            userData.saveToSharedPreferences(editor);
            editor.apply();

            // open home fragment
            HomeFragment homeFragment = new HomeFragment(userId);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
            fragmentTransaction.commit();
            binding.progressBar.setVisibility(View.GONE);
        }

    }
}