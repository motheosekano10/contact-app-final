package com.example.contactapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private TextInputLayout tilName, tilPhone, tilEmail;
    private ToggleButton tbMode;
    private Button btnSave;

    // Validation patterns
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{7,15}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Link UI elements
        tilName = findViewById(R.id.tilName);
        tilPhone = findViewById(R.id.tilPhone);
        tilEmail = findViewById(R.id.tilEmail);
        tbMode = findViewById(R.id.tbMode);
        btnSave = findViewById(R.id.btnSave);

        // 2. Setup listeners
        setupListeners();

        // 3. Start in View Mode (fields disabled)
        setEditMode(false);

        // 4. Load saved data if available
        loadContactData();
    }

    private void setupListeners() {
        // Toggle switch logic
        tbMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setEditMode(isChecked);
            clearAllErrors();
        });

        // Save button logic
        btnSave.setOnClickListener(v -> saveContact());

        // Auto-clear errors when user types
        addTextWatcher(tilName);
        addTextWatcher(tilPhone);
        addTextWatcher(tilEmail);
    }

    private void setEditMode(boolean enabled) {
        tilName.setEnabled(enabled);
        tilPhone.setEnabled(enabled);
        tilEmail.setEnabled(enabled);
        tbMode.setChecked(enabled);
    }

    private void addTextWatcher(TextInputLayout layout) {
        layout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                layout.setError(null);
                layout.setErrorEnabled(false);
            }
        });
    }

    private void clearAllErrors() {
        tilName.setError(null); tilName.setErrorEnabled(false);
        tilPhone.setError(null); tilPhone.setErrorEnabled(false);
        tilEmail.setError(null); tilEmail.setErrorEnabled(false);
    }

    private void saveContact() {
        String name = tilName.getEditText().getText().toString().trim();
        String phone = tilPhone.getEditText().getText().toString().trim();
        String email = tilEmail.getEditText().getText().toString().trim();
        boolean isValid = true;

        // Name validation
        if (name.isEmpty()) {
            tilName.setError("Name cannot be empty");
            isValid = false;
        } else if (!NAME_PATTERN.matcher(name).matches()) {
            tilName.setError("Only letters and spaces allowed");
            isValid = false;
        }

        // Phone validation
        if (phone.isEmpty()) {
            tilPhone.setError("Phone number cannot be empty");
            isValid = false;
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            tilPhone.setError("Enter 7 to 15 digits only");
            isValid = false;
        }

        // Email validation
        if (email.isEmpty()) {
            tilEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            isValid = false;
        }

        if (isValid) {
            SharedPreferences prefs = getSharedPreferences("ContactData", MODE_PRIVATE);
            prefs.edit()
                .putString("name", name)
                .putString("phone", phone)
                .putString("email", email)
                .apply();

            Toast.makeText(this, "Contact saved successfully!", Toast.LENGTH_SHORT).show();
            setEditMode(false);
        } else {
            Toast.makeText(this, "Please fix the errors above", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadContactData() {
        SharedPreferences prefs = getSharedPreferences("ContactData", MODE_PRIVATE);
        String name = prefs.getString("name", "");
        String phone = prefs.getString("phone", "");
        String email = prefs.getString("email", "");

        if (!name.isEmpty()) {
            tilName.getEditText().setText(name);
            tilPhone.getEditText().setText(phone);
            tilEmail.getEditText().setText(email);
        }
    }
}
