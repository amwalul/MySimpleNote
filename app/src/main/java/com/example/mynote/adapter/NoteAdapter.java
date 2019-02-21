package com.example.mynote.adapter;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mynote.CustomOnClickListener;
import com.example.mynote.FormAddUpdateActivity;
import com.example.mynote.R;
import com.example.mynote.entity.Note;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private ArrayList<Note> listNotes;
    private Activity activity;

    public NoteAdapter(Activity activity) {
        this.activity = activity;
    }

    public ArrayList<Note> getListNotes() {
        return listNotes;
    }

    public void setListNotes(ArrayList<Note> listNotes) {
        this.listNotes = listNotes;

        notifyDataSetChanged();
    }

    public void addItem(Note note) {
        this.listNotes.add(note);
        notifyItemInserted(listNotes.size() - 1);
    }

    public void updateItem(int position, Note note) {
        this.listNotes.set(position, note);
        notifyItemChanged(position, note);
    }

    public void removeItem(int position) {
        this.listNotes.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listNotes.size());
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_note, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int i) {
        final Note note = listNotes.get(i);
        noteViewHolder.tvTitle.setText(note.getTitle());
        noteViewHolder.tvDate.setText(note.getDate());
        noteViewHolder.tvDescription.setText(note.getDescription());
        noteViewHolder.cvNote.setOnClickListener(new CustomOnClickListener(i, new CustomOnClickListener.OnItemClickCallback() {
            @Override
            public void OnItemClicked(View view, int position) {
                Intent intent = new Intent(activity, FormAddUpdateActivity.class);
                intent.putExtra(FormAddUpdateActivity.EXTRA_POSITION, position);
                intent.putExtra(FormAddUpdateActivity.EXTRA_NOTE, note);
                activity.startActivityForResult(intent, FormAddUpdateActivity.REQUEST_UPDATE);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return getListNotes().size();
    }

    public class NoteViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_item_title) TextView tvTitle;
        @BindView(R.id.tv_item_description) TextView tvDescription;
        @BindView(R.id.tv_item_date) TextView tvDate;
        @BindView(R.id.cv_item_note) CardView cvNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
