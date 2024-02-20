package com.example.notes_app.ui.fragment;


import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes_app.R;
import com.example.notes_app.database.MyDatabaseHelper;
import com.example.notes_app.databinding.FragmentAddBinding;
import com.example.notes_app.utils.Toaster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class AddFragment extends Fragment {

    // declaring variable

    private String date_time;
    private Toaster toaster;
    private FragmentAddBinding addBinding;

    public AddFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBinding = FragmentAddBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();


        // getting current data to store in database
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date_time = dateFormat.format(date);
        toaster = new Toaster();

        addBinding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(getContext(), getUserId());
                String userId = getUserId();

                //validation of data
                if (addBinding.title.getText().toString().trim().isEmpty()) {
                    toaster.showToast(getContext(), "Please give title ");
                    return;
                }

                // adding data in database
                myDB.addNote(userId, addBinding.title.getText().toString().trim(),
                        addBinding.content.getText().toString().trim(),
                        date_time);

                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();

            }
        });

        addBinding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();

            }
        });

        return view;
    }

    //getting userid from shared preference
    private String getUserId() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        return preferences.getString("userId", "");
    }
}