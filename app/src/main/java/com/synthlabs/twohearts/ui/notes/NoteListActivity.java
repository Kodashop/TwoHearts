package com.synthlabs.twohearts.ui.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Note;
import com.synthlabs.twohearts.data.repo.NoteRepository;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity implements NoteAdapter.Callback {

    private static final int REQ_ADD = 3001;
    private RecyclerView rv;
    private NoteAdapter adapter;
    private List<Note> items = new ArrayList<>();
    private NoteRepository repo = new NoteRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        rv = findViewById(R.id.rv_notes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(items, this);
        rv.setAdapter(adapter);

        findViewById(R.id.fab_add_note).setOnClickListener(v -> startActivityForResult(new Intent(this, NoteEditActivity.class), REQ_ADD));

        load();
    }

    private void load() {
        items.clear();
        items.addAll(repo.list(null));
        adapter.notifyDataSetChanged();
        if (items.isEmpty()) {
            Toast.makeText(this, "No notes yet. Tap + to add one.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOpen(Note n) {
        Intent i = new Intent(this, NoteDetailActivity.class);
        i.putExtra("note_id", n.id);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && resultCode == Activity.RESULT_OK) {
            load();
        }
    }
}
