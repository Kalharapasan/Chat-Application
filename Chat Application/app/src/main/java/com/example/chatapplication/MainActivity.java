package com.example.chatapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
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

        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadMessages();

        btnSend.setOnClickListener(v -> sendMessage());

        setTitle("Chat - " + username);
    }

    private void loadMessages() {
        List<Message> messages = db.getAllMessages();
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
            Toast.makeText(this, "Message sent", Toast.LENGTH_SHORT).show();
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
            // Navigate to Profile Activity
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_refresh) {
            // Refresh messages
            loadMessages();
            Toast.makeText(this, "Messages refreshed", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_logout) {
            // Update user offline status before logout
            db.updateOnlineStatus(userId, false);

            SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Update last seen when app goes to background
        db.updateOnlineStatus(userId, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Update online status when app comes to foreground
        if (userId != -1) {
            db.updateOnlineStatus(userId, true);
        }
    }
}