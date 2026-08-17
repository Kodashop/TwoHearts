package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.data.repo.ProfileRepository;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button start = findViewById(R.id.btn_get_started);
        start.setOnClickListener(v -> {
            // Move to profile setup
            Intent i = new Intent(WelcomeActivity.this, ProfileSetupActivity.class);
            startActivity(i);
        });

        // If profile already exists, skip welcome
        ProfileRepository repo = new ProfileRepository();
        if (repo.getProfile(false) != null) {
            // already have a profile, skip welcome
            startActivity(new Intent(this, com.synthlabs.twohearts.ui.MainActivity.class));
            finish();
        }
    }
}
