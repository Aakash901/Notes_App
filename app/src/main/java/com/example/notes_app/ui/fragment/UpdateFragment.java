package com.example.notes_app.ui.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.notes_app.R;
import com.example.notes_app.database.MyDatabaseHelper;
import com.example.notes_app.databinding.FragmentUpdateBinding;
import com.example.notes_app.utils.Toaster;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class UpdateFragment extends Fragment {

    // declaring  variable
    private FragmentUpdateBinding updateBinding;
    private Toaster toaster;
    private String id, title, content, date_time, preDate;

    public UpdateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        updateBinding = FragmentUpdateBinding.inflate(inflater, container, false);
        View view = updateBinding.getRoot();


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        date_time = dateFormat.format(date);
        toaster = new Toaster();

        Bundle args = getArguments();
        if (args != null) {
            id = args.getString("id", "");
            title = args.getString("title", "");
            content = args.getString("content", "");
            preDate = args.getString("date", "");
            updateBinding.titleED.setText(title);
            updateBinding.authorED.setText(content);
            updateBinding.titleTv.setText(title);
            updateBinding.dateTv.setText(preDate);

        } else {
            Toast.makeText(getContext(), "No data", Toast.LENGTH_SHORT).show();
        }

        updateBinding.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //updating data in db
                String userId = getUserId();
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(requireContext(), userId);
                if (updateBinding.titleED.getText().toString().isEmpty()) {
                    toaster.showToast(getContext(), "Please give title ");
                } else {
                    myDatabaseHelper.updateNote(id, updateBinding.titleED.getText().toString().trim(), updateBinding.authorED.getText().toString().trim(), date_time);
                }

                // return to home page
                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();
            }
        });

        updateBinding.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmDialog();
            }
        });


        return view;
    }

    private String getUserId() {
        SharedPreferences preferences = requireActivity().getSharedPreferences("userDetail", MODE_PRIVATE);
        return preferences.getString("userId", "");
    }

    void confirmDialog() {
        // showing dialog to the user for confirmation
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete " + title + " ?");
        builder.setMessage("Are you sure you want to delete " + title + " ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //delete current note from db
                String userId = getUserId();
                MyDatabaseHelper myDatabaseHelper = new MyDatabaseHelper(requireContext(), userId);
                myDatabaseHelper.deleteOneNote(id);

                //return to home page
                HomeFragment homeFragment = HomeFragment.newInstance(userId);
                FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, homeFragment);
                fragmentTransaction.commit();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.create().show();

    }

}