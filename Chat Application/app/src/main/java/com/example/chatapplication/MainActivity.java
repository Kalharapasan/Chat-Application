package com.example.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    private TextView tvEmptyState;
    private MessageAdapter adapter;
    private DatabaseHelper db;
    private int userId;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
        username = prefs.getString("username", "");
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Update user online status
        db.updateOnlineStatus(userId, true);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        // Setup RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // Load messages
        loadMessages();

        // Send button click
        btnSend.setOnClickListener(v -> sendMessage());

        // Set title
        setTitle("Chat - " + username);
    }

    private void loadMessages() {
        List<Message> messages = db.getAllMessages();

        if (messages.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        adapter = new MessageAdapter(this, messages, userId);
        recyclerView.setAdapter(adapter);
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.addMessage(userId, username, content)) {
            etMessage.setText("");
            loadMessages();
            recyclerView.scrollToPosition(0);
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            loadMessages();
            Toast.makeText(this, "Messages refreshed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        // Update user offline status
        db.updateOnlineStatus(userId, false);

        // Clear preferences
        SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();

        // Navigate to login
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        db.updateOnlineStatus(userId, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId != -1) {
            db.updateOnlineStatus(userId, true);
            loadMessages();
        }
    }
}