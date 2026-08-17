package com.synthlabs.twohearts.ui.dates;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.ImportantDate;
import com.synthlabs.twohearts.data.repo.ProfileRepository;
import com.synthlabs.twohearts.core.DateUtils;

import java.util.Calendar;

public class ImportantDateEditActivity extends AppCompatActivity {

    private EditText etTitle, etDate;
    private Spinner spType;
    private CheckBox cbRecurring, cbReminder;
    private Button btnSave, btnCancel;
    private ProfileRepository repo = new ProfileRepository();
    private long dateId = -1;
    private ImportantDate date;
    private long selectedDate = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_date_edit);

        etTitle = findViewById(R.id.et_date_title);
        etDate = findViewById(R.id.et_date_date);
        spType = findViewById(R.id.sp_date_type);
        cbRecurring = findViewById(R.id.cb_date_recurring);
        cbReminder = findViewById(R.id.cb_date_reminder);
        btnSave = findViewById(R.id.btn_save_date);
        btnCancel = findViewById(R.id.btn_cancel_date);

        dateId = getIntent().getLongExtra("date_id", -1);
        if (dateId > 0) loadDate();

        etDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void loadDate() {
        date = null;
        for (ImportantDate d : repo.listDates()) {
            if (d.id == dateId) { date = d; break; }
        }
        if (date == null) return;
        etTitle.setText(date.title);
        selectedDate = date.date;
        etDate.setText(DateUtils.formatMedium(selectedDate));
        // Set spinner to type if it matches
        if (date.type != null) {
            int pos = 0;
            for (int i = 0; i < spType.getCount(); i++) {
                if (spType.getItemAtPosition(i).toString().equalsIgnoreCase(date.type)) {
                    pos = i;
                    break;
                }
            }
            spType.setSelection(pos);
        }
        cbRecurring.setChecked(date.recurring);
        cbReminder.setChecked(date.reminderEnabled);
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
        String type = spType.getSelectedItem().toString();
        boolean recurring = cbRecurring.isChecked();
        boolean reminderEnabled = cbReminder.isChecked();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Please enter a title");
            etTitle.requestFocus();
            return;
        }
        if (selectedDate <= 0) {
            Toast.makeText(this, "Please choose a date", Toast.LENGTH_SHORT).show();
            return;
        }

        ImportantDate d = date != null ? date : new ImportantDate();
        d.title = title;
        d.type = type;
        d.date = selectedDate;
        d.recurring = recurring;
        d.reminderEnabled = reminderEnabled;

        repo.saveDate(d);
        setResult(Activity.RESULT_OK);
        finish();
    }
}
