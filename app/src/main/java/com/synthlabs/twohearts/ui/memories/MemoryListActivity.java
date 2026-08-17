package com.synthlabs.twohearts.ui.memories;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Memory;
import com.synthlabs.twohearts.data.repo.MemoryRepository;

import java.util.ArrayList;
import java.util.List;

public class MemoryListActivity extends AppCompatActivity implements MemoryAdapter.Callback {

    private static final int REQ_ADD = 1001;
    private RecyclerView rv;
    private MemoryAdapter adapter;
    private List<Memory> items = new ArrayList<>();
    private MemoryRepository repo = new MemoryRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_list);

        rv = findViewById(R.id.rv_memories);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemoryAdapter(items, this);
        rv.setAdapter(adapter);

        findViewById(R.id.fab_add_memory).setOnClickListener(v -> {
            startActivityForResult(new Intent(this, MemoryEditActivity.class), REQ_ADD);
        });

        load();
    }

    private void load() {
        items.clear();
        items.addAll(repo.list(MemoryRepository.FILTER_ALL, null));
        adapter.notifyDataSetChanged();
        if (items.isEmpty()) {
            Toast.makeText(this, "No memories yet. Tap + to add one.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOpen(Memory m) {
        Intent i = new Intent(this, MemoryDetailActivity.class);
        i.putExtra("memory_id", m.id);
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
