package com.synthlabs.twohearts.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.synthlabs.twohearts.R;
import com.synthlabs.twohearts.core.Prefs;
import com.synthlabs.twohearts.security.PinManager;

public class AppLockSetupActivity extends AppCompatActivity {

    private EditText etPin;
    private EditText etConfirmPin;
    private Switch swBiometric;
    private Button btnSave;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_lock_setup);

        etPin = findViewById(R.id.et_pin);
        etConfirmPin = findViewById(R.id.et_confirm_pin);
        swBiometric = findViewById(R.id.switch_biometric);
        btnSave = findViewById(R.id.btn_save_lock);

        btnSave.setOnClickListener(v -> onSave());
    }

    private void onSave() {
        String pin = etPin.getText().toString().trim();
        String confirm = etConfirmPin.getText().toString().trim();

        if (TextUtils.isEmpty(pin) || pin.length() < 4) {
            etPin.setError("Enter a PIN of at least 4 digits");
            etPin.requestFocus();
            return;
        }
        if (!pin.equals(confirm)) {
            etConfirmPin.setError("PINs do not match");
            etConfirmPin.requestFocus();
            return;
        }

        // Save PIN via PinManager
        PinManager.setPin(this, pin);
        Prefs.setBool(this, Prefs.KEY_LOCK_ENABLED, true);
        Prefs.setBool(this, Prefs.KEY_LOCK_BIOMETRIC, swBiometric.isChecked());

        Toast.makeText(this, "App lock enabled", Toast.LENGTH_SHORT).show();

        // Navigate to Setup Complete
        startActivity(new Intent(this, SetupCompleteActivity.class));
        finish();
    }
}
