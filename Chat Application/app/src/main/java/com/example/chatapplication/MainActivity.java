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
        adapter = new MessageAdapter(this, messages);
        recyclerView.setAdapter(adapter);
    }

    private void sendMessage() {
        String content = etMessage.getText().toString().trim();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please enter a message", Toast.LENGTH_SHORT).show();
            return;
        }

        if (db.addMessage(userId, content)) {
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
        if (item.getItemId() == R.id.action_logout) {
            SharedPreferences prefs = getSharedPreferences("ChatAppPrefs", MODE_PRIVATE);
            prefs.edit().clear().apply();

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}