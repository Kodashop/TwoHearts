package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.Prefs;

public class PersonalizationSetupActivity extends AppCompatActivity {

    private Spinner spTheme;
    private Spinner spTextScale;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personalization_setup);

        spTheme = findViewById(R.id.sp_theme);
        spTextScale = findViewById(R.id.sp_text_scale);
        btnSave = findViewById(R.id.btn_save_personalization);

        ArrayAdapter<CharSequence> themeAdapter = ArrayAdapter.createFromResource(this,
                R.array.personalization_theme_options, android.R.layout.simple_spinner_item);
        themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(themeAdapter);

        ArrayAdapter<CharSequence> scaleAdapter = ArrayAdapter.createFromResource(this,
                R.array.personalization_text_scale_options, android.R.layout.simple_spinner_item);
        scaleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTextScale.setAdapter(scaleAdapter);

        // Restore saved values if present
        int theme = Prefs.getInt(this, Prefs.KEY_THEME, -1);
        int textScale = Prefs.getInt(this, Prefs.KEY_TEXT_SCALE, -1);
        if (theme >= 0) {
            spTheme.setSelection(theme);
        }
        if (textScale >= 0) {
            spTextScale.setSelection(textScale);
        }

        btnSave.setOnClickListener(v -> onSave());
    }

    private void onSave() {
        int theme = spTheme.getSelectedItemPosition();
        int textScale = spTextScale.getSelectedItemPosition();

        Prefs.setInt(this, Prefs.KEY_THEME, theme);
        Prefs.setInt(this, Prefs.KEY_TEXT_SCALE, textScale);

        Toast.makeText(this, "Personalization saved", Toast.LENGTH_SHORT).show();

        // Navigate to App Lock Setup
        startActivity(new Intent(this, AppLockSetupActivity.class));
        finish();
    }
}
