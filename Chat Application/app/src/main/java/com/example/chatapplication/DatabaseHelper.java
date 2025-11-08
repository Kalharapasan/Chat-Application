package com.example.chatapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ChatApp.db";
    private static final int DATABASE_VERSION = 2;

    // Users table
    private static final String TABLE_USERS = "users";
    private static final String COL_USER_ID = "id";
    private static final String COL_USERNAME = "username";
    private static final String COL_PASSWORD = "password";
    private static final String COL_EMAIL = "email";

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COL_MSG_ID = "id";
    private static final String COL_MSG_USER_ID = "user_id";
    private static final String COL_MSG_CONTENT = "content";
    private static final String COL_MSG_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT,"
                + COL_EMAIL + " TEXT"
                + ")";

        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COL_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MSG_USER_ID + " INTEGER,"
                + COL_MSG_CONTENT + " TEXT,"
                + COL_MSG_TIMESTAMP + " TEXT,"
                + "FOREIGN KEY(" + COL_MSG_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")"
                + ")";

        db.execSQL(createUsersTable);
        db.execSQL(createMessagesTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // User operations
    public boolean registerUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);

        long result = db.insert(TABLE_USERS, null, values);
        return result != -1;
    }

    public boolean checkUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=? AND " + COL_PASSWORD + "=?",
                new String[]{username, password},
                null, null, null);

        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public int getUserId(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                new String[]{COL_USER_ID},
                COL_USERNAME + "=?",
                new String[]{username},
                null, null, null);

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(0);
            cursor.close();
            return id;
        }
        cursor.close();
        return -1;
    }

    // Message operations (CRUD)
    public boolean addMessage(int userId, String content) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MSG_USER_ID, userId);
        values.put(COL_MSG_CONTENT, content);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COL_MSG_TIMESTAMP, sdf.format(new Date()));

        long result = db.insert(TABLE_MESSAGES, null, values);
        return result != -1;
    }

    public List<Message> getAllMessages() {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m." + COL_MSG_ID + ", u." + COL_USERNAME + ", m." + COL_MSG_CONTENT + ", m." + COL_MSG_TIMESTAMP +
                " FROM " + TABLE_MESSAGES + " m INNER JOIN " + TABLE_USERS + " u ON m." + COL_MSG_USER_ID + "=u." + COL_USER_ID +
                " ORDER BY m." + COL_MSG_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3)
                );
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;
    }

    public boolean updateMessage(int messageId, String newContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MSG_CONTENT, newContent);

        int result = db.update(TABLE_MESSAGES, values, COL_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});
        return result > 0;
    }

    public boolean deleteMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_MESSAGES, COL_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});
        return result > 0;
    }
}