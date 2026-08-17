package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.core.Prefs;
import com.synthlabs.twohearts.data.repo.ProfileRepository;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MS = 700;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.synthlabs.twohearts.R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(this::route, SPLASH_MS);
    }

    private void route() {
        // If setup is complete, go to main
        if (Prefs.getBool(this, Prefs.KEY_SETUP_DONE, false)) {
            startActivity(new Intent(this, com.synthlabs.twohearts.ui.MainActivity.class));
            finish();
            return;
        }

        ProfileRepository repo = new ProfileRepository();
        boolean hasProfile = repo.getProfile(false) != null;
        if (!hasProfile) {
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        // Profile exists; check relationship
        if (repo.getRelationship() == null || repo.getRelationship().spaceName == null || repo.getRelationship().startDate == 0) {
            startActivity(new Intent(this, RelationshipSetupActivity.class));
            finish();
            return;
        }

        // Profile + Relationship present; check personalization
        int theme = Prefs.getInt(this, Prefs.KEY_THEME, -1);
        int textScale = Prefs.getInt(this, Prefs.KEY_TEXT_SCALE, -1);
        if (theme == -1 || textScale == -1) {
            startActivity(new Intent(this, PersonalizationSetupActivity.class));
            finish();
            return;
        }

        // Personalization present; check app lock
        boolean lockEnabled = Prefs.getBool(this, Prefs.KEY_LOCK_ENABLED, false);
        if (!lockEnabled) {
            startActivity(new Intent(this, AppLockSetupActivity.class));
            finish();
            return;
        }

        // Finally, go to setup complete if not yet marked
        startActivity(new Intent(this, SetupCompleteActivity.class));
        finish();
    }
}
