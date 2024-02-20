package com.example.notes_app.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.notes_app.R;
import com.example.notes_app.database.MyDatabaseHelper;
import com.example.notes_app.databinding.FragmentHomeBinding;
import com.example.notes_app.model.UserData;
import com.example.notes_app.ui.activity.LoginActivity;
import com.example.notes_app.ui.adapter.NotesAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class HomeFragment extends Fragment {

    // Declaring all the variables
    private GoogleSignInClient mSignInClient;
    private GoogleSignInOptions gso;
    private GoogleSignInAccount account;

    private FragmentHomeBinding homeBinding;

    // declaring database and adapter related variable
    private MyDatabaseHelper myDb;
    private ArrayList<String> note_id, note_title, note_content, note_date, user_id;
    private NotesAdapter notesAdapter;
    private UserData userData;
    private String userId;

    public HomeFragment() {
    }

    public HomeFragment(String userId) {
        this.userId = userId;
    }

    public static HomeFragment newInstance(String userId) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = homeBinding.getRoot();

        //initialising all the GSI variable
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mSignInClient = GoogleSignIn.getClient(getContext(), gso);
        account = GoogleSignIn.getLastSignedInAccount(getContext());

        //getting userid
        Bundle args = getArguments();
        if (args != null) {
            userId = args.getString("userId", null);
        }

        myDb = new MyDatabaseHelper(getContext(), userId);

        // check weather user is authenticated or not
        if (!isUserAuthenticated()) {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            requireActivity().finish();
        }

        // changing status bar color into main color
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.mainColor));


        myDb = new MyDatabaseHelper(getContext(), userId);
        note_id = new ArrayList<>();
        note_content = new ArrayList<>();
        note_date = new ArrayList<>();
        note_title = new ArrayList<>();
        user_id = new ArrayList<>();

        userData = new UserData(account.getDisplayName(), account.getEmail(), String.valueOf(account.getPhotoUrl()), userId);
        String url = userData.getUrl();
        Glide.with(this).load(url).centerCrop().placeholder(R.drawable.accoun_logo).into(homeBinding.userPic);

        homeBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AddFragment addFragment = new AddFragment();
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, addFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
//

        homeBinding.userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showUserDetail();
            }
        });

        //welcoming the user with first name
        String fullName = userData.getName();
        String[] parts = fullName.split(" ");
        String firstName = parts[0];
        homeBinding.greetingTv.setText("Hi " + firstName+" ..");

        // getting data and displaying in recycler view
        storeDataInArrays();

        notesAdapter = new NotesAdapter(getContext(), note_id, note_title, note_content, note_date);
        homeBinding.recyclerView.setAdapter(notesAdapter);
        homeBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }


    // getting data from db
    void storeDataInArrays() {
        Cursor cursor = myDb.readAllData(userId);
        if (cursor == null) {
            Log.d("HomeFragment", "Cursor is null");
            return;
        }

        if (cursor.getCount() == 0) {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        } else {
            while (cursor.moveToNext()) {
                note_id.add(cursor.getString(0));
                user_id.add(cursor.getString(1));
                note_title.add(cursor.getString(2));
                note_content.add(cursor.getString(3));
                note_date.add(cursor.getString(4));
            }
        }
    }


    // checking user info for authentication
    private boolean isUserAuthenticated() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        return preferences.getString("userId", null) != null;
    }


    //showing user detail
    private void showUserDetail() {
        if (userData != null) {
            openDialogBox(userData.getName(), userData.getEmail(), userData.getUrl());
        }

    }

    private void openDialogBox(String name, String email, String profilePic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_user_detail, null);
        TextView nameTextView = dialogView.findViewById(R.id.dialogName);
        TextView emailTextView = dialogView.findViewById(R.id.dialogEmail);
        TextView deleteBtn = dialogView.findViewById(R.id.deleteAllDB);
        ShapeableImageView userImageView = dialogView.findViewById(R.id.dialogUserPic);
        TextView signOutBtn = dialogView.findViewById(R.id.signOutBtn);

        nameTextView.setText(name);
        emailTextView.setText(email);
        Glide.with(this).load(profilePic).centerCrop().placeholder(R.drawable.accoun_logo).into(userImageView);

        builder.setView(dialogView).setTitle("User Details");

        AlertDialog dialog = builder.create();
        dialog.show();
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDb.deleteAllNotes();
                dialog.dismiss();
                signOut();
            }
        });


    }

    // function for signOut or logout
    private void signOut() {

        mSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SharedPreferences.Editor editor = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE).edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}