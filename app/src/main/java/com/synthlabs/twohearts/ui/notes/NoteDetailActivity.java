package com.synthlabs.twohearts.ui.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Note;
import com.synthlabs.twohearts.data.repo.NoteRepository;

public class NoteDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvBody;
    private Button btnEdit, btnDelete;
    private NoteRepository repo = new NoteRepository();
    private long noteId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        tvTitle = findViewById(R.id.tv_note_detail_title);
        tvBody = findViewById(R.id.tv_note_detail_body);
        btnEdit = findViewById(R.id.btn_edit_note);
        btnDelete = findViewById(R.id.btn_delete_note);

        noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId <= 0) finish();

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, NoteEditActivity.class);
            i.putExtra("note_id", noteId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete note")
                    .setMessage("Delete this note?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repo.delete(noteId);
                        Toast.makeText(this, "Note deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void load() {
        Note n = repo.get(noteId);
        if (n == null) {
            finish();
            return;
        }
        tvTitle.setText(n.title != null ? n.title : "Untitled");
        tvBody.setText(n.body != null ? n.body : "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
