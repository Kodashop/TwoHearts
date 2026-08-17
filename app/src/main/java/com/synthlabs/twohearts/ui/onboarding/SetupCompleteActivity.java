package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.Prefs;

public class SetupCompleteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_complete);

        Button finish = findViewById(R.id.btn_finish_setup);
        finish.setOnClickListener(v -> {
            Prefs.setBool(this, Prefs.KEY_SETUP_DONE, true);
            startActivity(new Intent(this, com.synthlabs.twohearts.ui.MainActivity.class));
            finish();
        });
    }
}
