package com.example.notes_app.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes_app.R;
import com.example.notes_app.ui.fragment.UpdateFragment;

import java.util.ArrayList;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> note_id, note_title, note_content, note_date;
    private Animation translate_anim;

    public NotesAdapter(Context context, ArrayList<String> note_id, ArrayList<String> note_title, ArrayList<String> note_content, ArrayList<String> note_date) {
        this.context = context;
        this.note_id = note_id;
        this.note_title = note_title;
        this.note_content = note_content;
        this.note_date = note_date;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recvcler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        int pos = holder.getAdapterPosition();

        // setting data in views
        holder.title.setText(String.valueOf(note_title.get(pos)));
        holder.content.setText(String.valueOf(note_content.get(pos)));
        holder.timeTv.setText(String.valueOf(note_date.get(pos)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openUpdateFragment(note_id.get(pos), note_title.get(pos), note_content.get(pos), note_date.get(pos));
            }
        });

    }

    @Override
    public int getItemCount() {
        return note_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, content, timeTv;
        RelativeLayout mainLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleTv);
            content = itemView.findViewById(R.id.authorTv);
            mainLayout = itemView.findViewById(R.id.mainLayout);
            timeTv = itemView.findViewById(R.id.timeTv);

            //setting animation to seem user attractive
            translate_anim = AnimationUtils.loadAnimation(context, R.anim.translate_anim);
            mainLayout.setAnimation(translate_anim);

        }
    }

    private void openUpdateFragment(String id, String title, String content, String date) {

        UpdateFragment updateFragment = new UpdateFragment();
        Bundle args = new Bundle();
        args.putString("id", id);
        args.putString("title", title);
        args.putString("content", content);
        args.putString("date", date);
        updateFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, updateFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
