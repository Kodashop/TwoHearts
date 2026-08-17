package com.synthlabs.twohearts.ui.dates;

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
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.data.repo.ProfileRepository;
import com.synthlabs.twohearts.core.DateUtils;

public class ImportantDateDetailActivity extends AppCompatActivity {

    private TextView tvTitle, tvDate, tvType, tvRecurring, tvReminder;
    private Button btnEdit, btnDelete;
    private ProfileRepository repo = new ProfileRepository();
    private long dateId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_date_detail);

        tvTitle = findViewById(R.id.tv_detail_date_title);
        tvDate = findViewById(R.id.tv_detail_date_date);
        tvType = findViewById(R.id.tv_detail_date_type);
        tvRecurring = findViewById(R.id.tv_detail_date_recurring);
        tvReminder = findViewById(R.id.tv_detail_date_reminder);
        btnEdit = findViewById(R.id.btn_edit_date);
        btnDelete = findViewById(R.id.btn_delete_date);

        dateId = getIntent().getLongExtra("date_id", -1);
        if (dateId <= 0) finish();

        load();

        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, ImportantDateEditActivity.class);
            i.putExtra("date_id", dateId);
            startActivity(i);
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete date")
                    .setMessage("Delete this important date?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        repo.deleteDate(dateId);
                        Toast.makeText(this, "Date deleted", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void load() {
        ImportantDate d = null;
        for (ImportantDate id : repo.listDates()) if (id.id == dateId) { d = id; break; }
        if (d == null) { finish(); return; }
        
        tvTitle.setText(d.title != null ? d.title : "Untitled");
        tvDate.setText(DateUtils.formatLong(d.date));
        tvType.setText(d.type != null ? d.type : "Unknown");
        tvRecurring.setText(d.recurring ? "Yes" : "No");
        tvReminder.setText(d.reminderEnabled ? "Enabled" : "Disabled");
    }

    @Override
    protected void onResume() {
        super.onResume();
        load();
    }
}
