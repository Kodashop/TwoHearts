package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.Prefs;
import com.synthlabs.twohearts.data.model.Profile;
import com.synthlabs.twohearts.data.repo.ProfileRepository;

import java.util.Calendar;

public class ProfileSetupActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etNickname;
    private EditText etBirthday;
    private ImageView ivPhoto;
    private Button btnSave;

    private long birthdayMs = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        etName = findViewById(R.id.et_name);
        etNickname = findViewById(R.id.et_nickname);
        etBirthday = findViewById(R.id.et_birthday);
        ivPhoto = findViewById(R.id.iv_photo);
        btnSave = findViewById(R.id.btn_save_profile);

        etBirthday.setOnClickListener(v -> showDatePicker());

        btnSave.setOnClickListener(v -> onSave());

        // If an existing profile is present, prefill
        ProfileRepository repo = new ProfileRepository();
        Profile p = repo.getProfile(false);
        if (p != null) {
            etName.setText(p.name);
            etNickname.setText(p.nickname);
            if (p.birthday > 0) {
                birthdayMs = p.birthday;
                Calendar c = Calendar.getInstance();
                c.setTimeInMillis(birthdayMs);
                etBirthday.setText((c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.DAY_OF_MONTH) + "/" + c.get(Calendar.YEAR));
            }
            // photoUri handling omitted for now; use default launcher icon
        }
    }

    private void showDatePicker() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dp = new DatePickerDialog(this, (view, y, m, d) -> {
            Calendar sel = Calendar.getInstance();
            sel.set(y, m, d, 0, 0, 0);
            birthdayMs = sel.getTimeInMillis();
            etBirthday.setText((m + 1) + "/" + d + "/" + y);
        }, year, month, day);
        dp.show();
    }

    private void onSave() {
        String name = etName.getText().toString().trim();
        String nickname = etNickname.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etName.setError("Please enter your name");
            etName.requestFocus();
            return;
        }

        Profile p = new Profile();
        p.isPartner = false;
        p.name = name;
        p.nickname = TextUtils.isEmpty(nickname) ? null : nickname;
        p.birthday = birthdayMs;

        ProfileRepository repo = new ProfileRepository();
        repo.saveProfile(p);

        // Mark that onboarding has at least progressed (not full setup)
        Prefs.setBool(this, Prefs.KEY_PERMISSIONS_SHOWN, true); // reuse an existing key as an indicator

        Toast.makeText(this, "Profile saved", Toast.LENGTH_SHORT).show();

        // Navigate to Relationship Setup
        Intent i = new Intent(ProfileSetupActivity.this, RelationshipSetupActivity.class);
        startActivity(i);
        finish();
    }
}
