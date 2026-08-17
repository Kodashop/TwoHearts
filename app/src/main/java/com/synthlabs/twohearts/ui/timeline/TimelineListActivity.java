package com.synthlabs.twohearts.ui.timeline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.TimelineEvent;
import com.synthlabs.twohearts.data.repo.TimelineRepository;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimelineListActivity extends AppCompatActivity implements TimelineAdapter.Callback {

    private static final int REQ_ADD = 4001;
    private RecyclerView rv;
    private TimelineAdapter adapter;
    private List<TimelineEvent> items = new ArrayList<>();
    private TimelineRepository repo = new TimelineRepository();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_list);

        rv = findViewById(R.id.rv_timeline);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TimelineAdapter(items, this);
        rv.setAdapter(adapter);

        findViewById(R.id.fab_add_event).setOnClickListener(v -> startActivityForResult(new Intent(this, TimelineEditActivity.class), REQ_ADD));

        load();
    }

    private void load() {
        items.clear();
        items.addAll(repo.list());
        adapter.notifyDataSetChanged();
        if (items.isEmpty()) Toast.makeText(this, "No timeline events yet. Tap + to add one.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onOpen(TimelineEvent e) {
        Intent i = new Intent(this, TimelineDetailActivity.class);
        i.putExtra("event_id", e.id);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ADD && resultCode == Activity.RESULT_OK) load();
    }
}
