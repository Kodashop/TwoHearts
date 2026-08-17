package com.synthlabs.twohearts.ui.timeline;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.TimelineEvent;
import com.synthlabs.twohearts.data.repo.TimelineRepository;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.Calendar;

public class TimelineEditActivity extends AppCompatActivity {

    private EditText etTitle, etDate, etNote;
    private Button btnSave, btnDelete;
    private TimelineRepository repo = new TimelineRepository();
    private long eventId = -1;
    private TimelineEvent event;
    private long selectedDate = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline_edit);

        etTitle = findViewById(R.id.et_event_title);
        etDate = findViewById(R.id.et_event_date);
        etNote = findViewById(R.id.et_event_note);
        btnSave = findViewById(R.id.btn_save_event);
        btnDelete = findViewById(R.id.btn_delete_event);

        eventId = getIntent().getLongExtra("event_id", -1);
        if (eventId > 0) loadEvent();

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSave());
        btnDelete.setOnClickListener(v -> onDelete());
    }

    private void loadEvent() {
        event = null;
        for (TimelineEvent e : repo.list()) {
            if (e.id == eventId) { event = e; break; }
        }
        if (event == null) return;
        etTitle.setText(event.title);
        etNote.setText(event.note);
        selectedDate = event.date;
        etDate.setText(DateUtils.formatMedium(selectedDate));
    }

    private void showDatePicker() {
        Calendar c = Calendar.getInstance();
        if (selectedDate > 0) c.setTimeInMillis(selectedDate);
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar chosen = Calendar.getInstance();
            chosen.set(Calendar.YEAR, year);
            chosen.set(Calendar.MONTH, month);
            chosen.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            chosen.set(Calendar.HOUR_OF_DAY, 12);
            chosen.set(Calendar.MINUTE, 0);
            chosen.set(Calendar.SECOND, 0);
            chosen.set(Calendar.MILLISECOND, 0);
            selectedDate = chosen.getTimeInMillis();
            etDate.setText(DateUtils.formatMedium(selectedDate));
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void onSave() {
        String title = etTitle.getText().toString().trim();
        String note = etNote.getText().toString().trim();
        if (TextUtils.isEmpty(title)) { etTitle.setError("Please enter a title"); etTitle.requestFocus(); return; }
        if (selectedDate <= 0) { Toast.makeText(this, "Please choose a date", Toast.LENGTH_SHORT).show(); return; }
        TimelineEvent e = event != null ? event : new TimelineEvent();
        e.title = title;
        e.note = TextUtils.isEmpty(note) ? null : note;
        e.date = selectedDate;
        repo.save(e);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void onDelete() {
        if (event == null) return;
        repo.delete(event.id);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
