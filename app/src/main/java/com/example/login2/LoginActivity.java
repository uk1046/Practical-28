package com.example.login2;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/** @noinspection ALL*/
public class LoginActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextPassword;
    private Button buttonLogin;
    private TextView textViewResult, textViewAttempts;

    private SQLiteDatabase database;

    private int loginAttempts = 0;
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewResult = findViewById(R.id.textViewResult);
        textViewAttempts = findViewById(R.id.textViewAttempts);

        // Initialize SQLite database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Set click listener for Login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Execute AsyncTask to check login credentials
                new CheckLoginTask().execute(
                        editTextUsername.getText().toString(),
                        editTextPassword.getText().toString()
                );
            }
        });
    }

    // AsyncTask to check login credentials
    private class CheckLoginTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            // Check if the provided username and password match a record in the database
            String username = params[0];
            String password = params[1];

            String[] projection = {DatabaseHelper.COLUMN_USERNAME, DatabaseHelper.COLUMN_PASSWORD};
            String selection = DatabaseHelper.COLUMN_USERNAME + " = ? AND " +
                    DatabaseHelper.COLUMN_PASSWORD + " = ?";
            String[] selectionArgs = {username, password};

            Cursor cursor = database.query(
                    DatabaseHelper.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
            );

            boolean loginSuccessful = cursor != null && cursor.getCount() > 0;

            if (cursor != null) {
                cursor.close();
            }

            return loginSuccessful;
        }

        @Override
        protected void onPostExecute(Boolean loginSuccessful) {
            // Update UI based on login result
            if (loginSuccessful) {
                // Reset login attempts on successful login
                loginAttempts = 0;
                textViewAttempts.setText("");
                textViewResult.setText("Login Successful");
                showToast("Welcome!");
                // Add further actions for successful login, e.g., navigating to another activity
            } else {
                // Increment login attempts on unsuccessful login
                loginAttempts++;
                textViewAttempts.setText("Login Attempts: " + loginAttempts + "/" + MAX_LOGIN_ATTEMPTS);
                textViewResult.setText("Login Unsuccessful");

                if (loginAttempts >= MAX_LOGIN_ATTEMPTS) {
                    showToast("Too many unsuccessful attempts. Try again later.");
                    // Disable login button or take appropriate action
                } else {
                    showToast("Invalid username or password.");
                }
            }
        }
    }

    // Show a Toast message
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // Redirect to Signup activity
    public void goToSignup(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
