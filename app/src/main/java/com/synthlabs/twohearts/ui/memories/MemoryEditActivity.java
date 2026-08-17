package com.synthlabs.twohearts.ui.memories;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Memory;
import com.synthlabs.twohearts.data.repo.MemoryRepository;

import java.util.Calendar;

public class MemoryEditActivity extends AppCompatActivity {

    private static final int REQ_PICK_PHOTO = 2001;

    private EditText etTitle;
    private EditText etLocation;
    private EditText etStory;
    private ImageView ivPhoto;
    private Button btnPickPhoto;
    private Button btnSave;

    private Uri pickedUri;
    private MemoryRepository repo = new MemoryRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_edit);

        etTitle = findViewById(R.id.et_title);
        etLocation = findViewById(R.id.et_location);
        etStory = findViewById(R.id.et_story);
        ivPhoto = findViewById(R.id.iv_photo_preview);
        btnPickPhoto = findViewById(R.id.btn_pick_photo);
        btnSave = findViewById(R.id.btn_save_memory);

        btnPickPhoto.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "Select photo"), REQ_PICK_PHOTO);
        });

        btnSave.setOnClickListener(v -> onSave());
    }

    private void onSave() {
        String title = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String story = etStory.getText().toString().trim();
        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Please enter a title");
            etTitle.requestFocus();
            return;
        }
        Memory m = new Memory();
        m.title = title;
        m.location = TextUtils.isEmpty(location) ? null : location;
        m.story = TextUtils.isEmpty(story) ? null : story;
        m.date = System.currentTimeMillis();
        m.photoUri = pickedUri != null ? pickedUri.toString() : null;

        repo.save(m);
        Toast.makeText(this, "Memory saved", Toast.LENGTH_SHORT).show();
        setResult(Activity.RESULT_OK);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_PICK_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            pickedUri = data.getData();
            if (pickedUri != null) {
                ivPhoto.setImageURI(pickedUri);
            }
        }
    }
}
