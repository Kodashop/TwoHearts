package com.synthlabs.twohearts.ui.dates;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.data.repo.ProfileRepository;

import java.util.ArrayList;
import java.util.List;

public class ImportantDatesActivity extends AppCompatActivity implements ImportantDateAdapter.Callback {

    private static final int REQ_ADD = 5001;
    private static final int REQ_EDIT = 5002;

    private RecyclerView rv;
    private ImportantDateAdapter adapter;
    private List<ImportantDate> items = new ArrayList<>();
    private ProfileRepository repo = new ProfileRepository();
    private TextView tvEmpty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_dates);

        rv = findViewById(R.id.rv_dates);
        tvEmpty = findViewById(R.id.tv_empty);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImportantDateAdapter(items, this);
        rv.setAdapter(adapter);

        findViewById(R.id.fab_add_date).setOnClickListener(v -> {
            Intent i = new Intent(this, ImportantDateEditActivity.class);
            startActivityForResult(i, REQ_ADD);
        });

        load();
    }

    private void load() {
        items.clear();
        items.addAll(repo.listDates());
        adapter.notifyDataSetChanged();
        tvEmpty.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        if (items.isEmpty()) {
            Toast.makeText(this, "No important dates yet. Tap + to add one.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onOpen(ImportantDate d) {
        Intent i = new Intent(this, ImportantDateDetailActivity.class);
        i.putExtra("date_id", d.id);
        startActivityForResult(i, REQ_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQ_ADD || requestCode == REQ_EDIT) && resultCode == Activity.RESULT_OK) {
            load();
        }
    }
}
