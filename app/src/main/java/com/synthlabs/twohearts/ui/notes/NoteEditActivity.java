package com.synthlabs.twohearts.ui.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Note;
import com.synthlabs.twohearts.data.repo.NoteRepository;

public class NoteEditActivity extends AppCompatActivity {

    private EditText etTitle, etBody;
    private Button btnSave, btnDelete;
    private NoteRepository repo = new NoteRepository();
    private long noteId = -1;
    private Note note;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_edit);

        etTitle = findViewById(R.id.et_note_title);
        etBody = findViewById(R.id.et_note_body);
        btnSave = findViewById(R.id.btn_save_note);
        btnDelete = findViewById(R.id.btn_delete_note);

        noteId = getIntent().getLongExtra("note_id", -1);
        if (noteId > 0) loadNote();

        btnSave.setOnClickListener(v -> onSave());
        btnDelete.setOnClickListener(v -> onDelete());
    }

    private void loadNote() {
        note = repo.get(noteId);
        if (note == null) return;
        etTitle.setText(note.title);
        etBody.setText(note.body);
    }

    private void onSave() {
        String title = etTitle.getText().toString().trim();
        String body = etBody.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Please enter a title");
            etTitle.requestFocus();
            return;
        }
        Note n = note != null ? note : new Note();
        n.title = title;
        n.body = TextUtils.isEmpty(body) ? null : body;
        repo.save(n);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void onDelete() {
        if (note == null) return;
        repo.delete(note.id);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
