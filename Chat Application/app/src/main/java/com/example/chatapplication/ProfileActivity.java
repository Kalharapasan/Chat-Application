package com.example.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvEmail;
    private EditText etBio;
    private Button btnUpdateProfile, btnLogout;
    private DatabaseHelper db;
    private int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Enable back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Profile");
        }

        db = new DatabaseHelper(this);

        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);
        username = prefs.getString("username", "");

        // Initialize views
        tvUsername = findViewById(R.id.tvProfileUsername);
        tvEmail = findViewById(R.id.tvProfileEmail);
        etBio = findViewById(R.id.etProfileBio);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        btnLogout = findViewById(R.id.btnLogout);

        // Load user profile
        loadUserProfile();

        // Update profile button
        btnUpdateProfile.setOnClickListener(v -> updateProfile());

        // Logout button
        btnLogout.setOnClickListener(v -> logout());
    }

    private void loadUserProfile() {
        User user = db.getUserProfile(userId);
        if (user != null) {
            tvUsername.setText(user.getUsername());
            tvEmail.setText(user.getEmail());

            String bio = user.getBio();
            if (bio != null && !bio.isEmpty()) {
                etBio.setText(bio);
            } else {
                etBio.setText("Hey there! I'm using Chat App");
            }
        }
    }

    private void updateProfile() {
        String newBio = etBio.getText().toString().trim();

        if (newBio.isEmpty()) {
            Toast.makeText(this, "Bio cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.updateUserProfile(userId, newBio, null)) {
            Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // Update user offline status
        db.updateOnlineStatus(userId, false);

        // Clear shared preferences
        SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Navigate to login
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}