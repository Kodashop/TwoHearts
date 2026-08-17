package com.synthlabs.twohearts.ui.memories;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Memory;
import com.synthlabs.twohearts.data.repo.MemoryRepository;
import com.synthlabs.twohearts.core.DateUtils;

public class MemoryDetailActivity extends AppCompatActivity {

    private ImageView ivPhoto;
    private TextView tvTitle, tvDate, tvLocation, tvStory;
    private Button btnEdit, btnDelete;

    private MemoryRepository repo = new MemoryRepository();
    private long memoryId;
    private Memory memory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_detail);

        ivPhoto = findViewById(R.id.iv_memory_photo);
        tvTitle = findViewById(R.id.tv_memory_title);
        tvDate = findViewById(R.id.tv_memory_date);
        tvLocation = findViewById(R.id.tv_memory_location);
        tvStory = findViewById(R.id.tv_memory_story);
        btnEdit = findViewById(R.id.btn_edit_memory);
        btnDelete = findViewById(R.id.btn_delete_memory);

        memoryId = getIntent().getLongExtra("memory_id", -1);
        if (memoryId <= 0) {
            finish();
            return;
        }

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, MemoryEditActivity.class);
            i.putExtra("memory_id", memoryId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete memory")
                    .setMessage("Are you sure you want to delete this memory? This cannot be undone.")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repo.delete(memoryId);
                        Toast.makeText(this, "Memory deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void load() {
        memory = repo.get(memoryId);
        if (memory == null) {
            finish();
            return;
        }
        tvTitle.setText(memory.title != null ? memory.title : "Untitled");
        tvDate.setText(DateUtils.formatLong(memory.date > 0 ? memory.date : memory.createdAt));
        tvLocation.setText(memory.location != null ? memory.location : "");
        tvStory.setText(memory.story != null ? memory.story : "");
        if (memory.photoUri != null && !memory.photoUri.isEmpty()) {
            try {
                ivPhoto.setImageURI(Uri.parse(memory.photoUri));
            } catch (Exception e) {
                ivPhoto.setImageResource(R.mipmap.ic_launcher);
            }
        } else {
            ivPhoto.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
