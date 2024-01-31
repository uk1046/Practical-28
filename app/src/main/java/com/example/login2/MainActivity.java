package com.example.login2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText editTextNewUsername, editTextNewPassword;
    private Button buttonSignup;

    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        editTextNewUsername = findViewById(R.id.editTextNewUsername);
        editTextNewPassword = findViewById(R.id.editTextNewPassword);
        buttonSignup = findViewById(R.id.buttonSignup);

        // Initialize DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Set click listener for Signup button
        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for valid input and perform signup
                String newUsername = editTextNewUsername.getText().toString().trim();
                String newPassword = editTextNewPassword.getText().toString().trim();

                if (isValidInput(newUsername, newPassword)) {
                    // Check if the username already exists
                    if (!dbHelper.isUsernameExists(newUsername)) {
                        // Insert the new user into the database
                        dbHelper.insertUser(newUsername, newPassword);
                        showToast("Signup Successful");
                        // Redirect to the login activity
                        goToLogin();
                    } else {
                        showToast("Username already exists. Please choose another one.");
                    }
                }
            }
        });
    }

    // Check for valid input (non-empty and within desired length)
    private boolean isValidInput(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            showToast("Please fill in all fields.");
            return false;
        }

        if (username.length() < 4 || password.length() < 4) {
            showToast("Username and password must be at least 4 characters long.");
            return false;
        }

        return true;
    }

    // Show a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Redirect to Login activity
    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
