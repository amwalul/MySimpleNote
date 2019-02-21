package com.example.mynote;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mynote.db.NoteHelper;
import com.example.mynote.entity.Note;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FormAddUpdateActivity extends AppCompatActivity {
    @BindView(R.id.edt_title) EditText edtTitle;
    @BindView(R.id.edt_description) EditText edtDescription;
    @BindView(R.id.btn_submit) Button btnSubmit;

    public static String EXTRA_NOTE = "extra_note";
    public static String EXTRA_POSITION = "extra_position";

    private boolean isEdit = false;
    public static int REQUEST_ADD = 100;
    public static int RESULT_ADD = 101;
    public static int REQUEST_UPDATE = 200;
    public static int RESULT_UPDATE = 201;
    public static int RESULT_DELETE = 301;

    private Note note;
    private int position;
    private NoteHelper noteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_add_update);
        ButterKnife.bind(this);

        noteHelper = NoteHelper.getInstance(getApplicationContext());

        note = getIntent().getParcelableExtra(EXTRA_NOTE);

        if (note != null) {
            position = getIntent().getIntExtra(EXTRA_POSITION, 0);
            isEdit = true;
        } else note = new Note();

        String actionBarTitle;
        String btnTitle;

        if (isEdit) {
            actionBarTitle = "Ubah";
            btnTitle = "Update";

            if (note != null) {
                edtTitle.setText(note.getTitle());
                edtDescription.setText(note.getDescription());
            }
        } else {
            actionBarTitle = "Tambah";
            btnTitle = "Simpan";
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(actionBarTitle);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        btnSubmit.setText(btnTitle);
    }

    @OnClick(R.id.btn_submit)
    public void onSubmitClick() {
        onSubmit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isEdit) getMenuInflater().inflate(R.menu.menu_form, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                showAlertDialog();
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void onSubmit() {
        String title = edtTitle.getText().toString().trim();
        String description = edtDescription.getText().toString().trim();

        if (TextUtils.isEmpty(description)) {
            edtDescription.setError("Field can not be blank");
            return;
        }

        note.setTitle(title);
        note.setDescription(description);
        note.setDate(getCurrentDate());

        Intent intent = new Intent();
        intent.putExtra(EXTRA_NOTE, note);
        intent.putExtra(EXTRA_POSITION, position);

        if (isEdit) {
            long result = noteHelper.updateNote(note);
            if (result > 0) {
                setResult(RESULT_UPDATE, intent);
                finish();
            } else {
                Toast.makeText(FormAddUpdateActivity.this, "Gagal mengupdate data", Toast.LENGTH_SHORT).show();
            }
        } else {
            long result = noteHelper.insertNote(note);

            if (result > 0) {
                note.setId((int) result);
                setResult(RESULT_ADD, intent);
                finish();
            } else {
                Toast.makeText(FormAddUpdateActivity.this, "Gagal menambah data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAlertDialog() {
        String dialogTitle, dialogMessage;

        dialogTitle = "Hapus Note";
        dialogMessage = "Apakah anda yakin ingin menghapus item ini?";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        long result = noteHelper.deleteNote(note.getId());
                        if (result > 0) {
                            Intent intent = new Intent();
                            intent.putExtra(EXTRA_POSITION, position);
                            setResult(RESULT_DELETE, intent);
                            finish();
                        } else {
                            Toast.makeText(FormAddUpdateActivity.this, "Gagal menghapus data", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }
}
