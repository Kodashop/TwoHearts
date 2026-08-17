package com.synthlabs.twohearts.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.ui.MainActivity;

public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_MS = 700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // For now, go straight to MainActivity. Onboarding flow can be added later.
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }, SPLASH_MS);
    }
}
