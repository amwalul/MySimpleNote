package com.example.mynote;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.mynote.adapter.NoteAdapter;
import com.example.mynote.db.NoteHelper;
import com.example.mynote.entity.Note;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements LoadNotesCallback {
    @BindView(R.id.rv_notes) RecyclerView rvNotes;
    @BindView(R.id.progressbar) ProgressBar progressBar;
    @BindView(R.id.fab_add) FloatingActionButton fabAdd;

    private NoteAdapter adapter;
    private NoteHelper noteHelper;

    private static final String EXTRA_STATE = "EXTRA_STATE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Notes");

        rvNotes.setLayoutManager(new LinearLayoutManager(this));
        rvNotes.setHasFixedSize(true);

        noteHelper = NoteHelper.getInstance(getApplicationContext());
        noteHelper.open();

        adapter = new NoteAdapter(this);
        rvNotes.setAdapter(adapter);

        if (savedInstanceState == null) new LoadNotesAsync(noteHelper, this).execute();
        else {
            ArrayList<Note> list = savedInstanceState.getParcelableArrayList(EXTRA_STATE);
            if (list != null) adapter.setListNotes(list);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EXTRA_STATE, adapter.getListNotes());
    }

    @OnClick(R.id.fab_add)
    public void onAddClick() {
        Intent intent = new Intent(MainActivity.this, FormAddUpdateActivity.class);
        startActivityForResult(intent, FormAddUpdateActivity.REQUEST_ADD);
    }

    @Override
    public void preExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void postExecute(ArrayList<Note> notes) {
        progressBar.setVisibility(View.INVISIBLE);
        adapter.setListNotes(notes);
    }

    private static class LoadNotesAsync extends AsyncTask<Void, Void, ArrayList<Note>> {
        private final WeakReference<NoteHelper> weakNoteHelper;
        private final WeakReference<LoadNotesCallback> weakCallback;

        private LoadNotesAsync(NoteHelper noteHelper, LoadNotesCallback callback) {
            weakNoteHelper = new WeakReference<>(noteHelper);
            weakCallback = new WeakReference<>(callback);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            weakCallback.get().preExecute();
        }

        @Override
        protected ArrayList<Note> doInBackground(Void... voids) {
            return weakNoteHelper.get().getAllNotes();
        }

        @Override
        protected void onPostExecute(ArrayList<Note> notes) {
            super.onPostExecute(notes);
            weakCallback.get().postExecute(notes);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            if (requestCode == FormAddUpdateActivity.REQUEST_ADD) {
                if (resultCode == FormAddUpdateActivity.RESULT_ADD) {
                    Note note = data.getParcelableExtra(FormAddUpdateActivity.EXTRA_NOTE);

                    adapter.addItem(note);
                    rvNotes.smoothScrollToPosition(adapter.getItemCount() - 1);

                    showSnackbarMessage("Satu item berhasil ditambahkan");
                }
            } else if (requestCode == FormAddUpdateActivity.REQUEST_UPDATE) {
                if (resultCode == FormAddUpdateActivity.RESULT_UPDATE) {
                    Note note = data.getParcelableExtra(FormAddUpdateActivity.EXTRA_NOTE);
                    int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);
                    adapter.updateItem(position, note);
                    rvNotes.smoothScrollToPosition(position);

                    showSnackbarMessage("Satu item berhasil diubah");
                } else if (resultCode == FormAddUpdateActivity.RESULT_DELETE) {
                    int position = data.getIntExtra(FormAddUpdateActivity.EXTRA_POSITION, 0);

                    adapter.removeItem(position);

                    showSnackbarMessage("Satu item berhasil dihapus");
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        noteHelper.close();
    }

    private void showSnackbarMessage(String message){
        Snackbar.make(rvNotes, message, Snackbar.LENGTH_SHORT).show();
    }
}
