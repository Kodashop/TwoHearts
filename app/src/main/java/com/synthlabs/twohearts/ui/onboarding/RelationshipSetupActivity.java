package com.synthlabs.twohearts.ui.onboarding;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.model.Relationship;
import com.synthlabs.twohearts.data.repo.ProfileRepository;

import java.util.Calendar;

public class RelationshipSetupActivity extends AppCompatActivity {

    private EditText etSpaceName;
    private EditText etGreeting;
    private TextView tvStartDate;
    private Spinner spStatus;
    private Button btnSave;

    private long startDateMs = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relationship_setup);

        etSpaceName = findViewById(R.id.et_space_name);
        etGreeting = findViewById(R.id.et_greeting);
        tvStartDate = findViewById(R.id.tv_start_date);
        spStatus = findViewById(R.id.sp_status);
        btnSave = findViewById(R.id.btn_save_relationship);

        tvStartDate.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSave());

        // Load existing relationship if present
        ProfileRepository repo = new ProfileRepository();
        Relationship r = repo.getRelationship();
        if (r != null) {
            if (!TextUtils.isEmpty(r.spaceName)) etSpaceName.setText(r.spaceName);
            if (!TextUtils.isEmpty(r.greeting)) etGreeting.setText(r.greeting);
            if (r.startDate > 0) {
                startDateMs = r.startDate;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(startDateMs);
                tvStartDate.setText((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
            }
            if (!TextUtils.isEmpty(r.status)) {
                // attempt to match spinner value
                String status = r.status;
                for (int i = 0; i < spStatus.getCount(); i++) {
                    if (spStatus.getItemAtPosition(i).toString().equalsIgnoreCase(status)) {
                        spStatus.setSelection(i);
                        break;
                    }
                }
            }
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, (DatePicker view, int y, int m, int d) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(y, m, d, 0, 0, 0);
            startDateMs = sel.getTimeInMillis();
            tvStartDate.setText((m + 1) + "/" + d + "/" + y);
        }, year, month, day);
        dp.show();
    }

    private void onSave() {
        String spaceName = etSpaceName.getText().toString().trim();
        String greeting = etGreeting.getText().toString().trim();
        String status = spStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(spaceName)) {
            etSpaceName.setError("Please enter a name for your space");
            etSpaceName.requestFocus();
            return;
        }
        if (startDateMs <= 0) {
            Toast.makeText(this, "Please choose a start date", Toast.LENGTH_SHORT).show();
            tvStartDate.requestFocus();
            return;
        }

        Relationship r = new Relationship();
        r.spaceName = spaceName;
        r.greeting = TextUtils.isEmpty(greeting) ? null : greeting;
        r.startDate = startDateMs;
        r.status = status;

        ProfileRepository repo = new ProfileRepository();
        repo.saveRelationship(r);

        Toast.makeText(this, "Relationship saved", Toast.LENGTH_SHORT).show();

        // Navigate to MainActivity for now
        Intent i = new Intent(RelationshipSetupActivity.this, com.synthlabs.twohearts.ui.MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        finish();
    }
}
