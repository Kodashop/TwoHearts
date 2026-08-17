package com.synthlabs.twohearts.ui.timeline;

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
import com.synthlabs.twohearts.data.model.TimelineEvent;
import com.synthlabs.twohearts.data.repo.TimelineRepository;
import com.synthlabs.twohearts.core.DateUtils;

public class TimelineDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDate, tvNote;
    private Button btnEdit, btnDelete;
    private TimelineRepository repo = new TimelineRepository();
    private long eventId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_detail);

        tvTitle = findViewById(R.id.tv_detail_title);
        tvDate = findViewById(R.id.tv_detail_date);
        tvNote = findViewById(R.id.tv_detail_note);
        btnEdit = findViewById(R.id.btn_edit_event);
        btnDelete = findViewById(R.id.btn_delete_event);

        eventId = getIntent().getLongExtra("event_id", -1);
        if (eventId <= 0) finish();

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, TimelineEditActivity.class);
            i.putExtra("event_id", eventId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete event")
                    .setMessage("Delete this event?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repo.delete(eventId);
                        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void load() {
        TimelineEvent e = null;
        for (TimelineEvent te : repo.list()) if (te.id == eventId) { e = te; break; }
        if (e == null) { finish(); return; }
        tvTitle.setText(e.title != null ? e.title : "Untitled");
        tvDate.setText(DateUtils.formatLong(e.date));
        tvNote.setText(e.note != null ? e.note : "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
