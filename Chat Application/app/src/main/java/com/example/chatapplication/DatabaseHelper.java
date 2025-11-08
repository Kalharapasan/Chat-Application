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
    private static final String COL_PROFILE_PIC = "profile_pic";
    private static final String COL_BIO = "bio";
    private static final String COL_ONLINE_STATUS = "online_status";
    private static final String COL_LAST_SEEN = "last_seen";

    // Messages table
    private static final String TABLE_MESSAGES = "messages";
    private static final String COL_MSG_ID = "id";
    private static final String COL_MSG_USER_ID = "user_id";
    private static final String COL_MSG_USERNAME = "username";
    private static final String COL_MSG_CONTENT = "content";
    private static final String COL_MSG_TIMESTAMP = "timestamp";
    private static final String COL_MSG_TYPE = "message_type";
    private static final String COL_MSG_IMAGE_PATH = "image_path";
    private static final String COL_MSG_REPLY_TO = "reply_to_id";
    private static final String COL_MSG_EDITED = "is_edited";
    private static final String COL_MSG_READ = "is_read";

    // Reactions table
    private static final String TABLE_REACTIONS = "reactions";
    private static final String COL_REACTION_ID = "id";
    private static final String COL_REACTION_MSG_ID = "message_id";
    private static final String COL_REACTION_USER_ID = "user_id";
    private static final String COL_REACTION_TYPE = "reaction_type";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + "("
                + COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_USERNAME + " TEXT UNIQUE,"
                + COL_PASSWORD + " TEXT,"
                + COL_EMAIL + " TEXT,"
                + COL_PROFILE_PIC + " TEXT,"
                + COL_BIO + " TEXT,"
                + COL_ONLINE_STATUS + " INTEGER DEFAULT 0,"
                + COL_LAST_SEEN + " TEXT"
                + ")";

        String createMessagesTable = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COL_MSG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_MSG_USER_ID + " INTEGER,"
                + COL_MSG_USERNAME + " TEXT,"
                + COL_MSG_CONTENT + " TEXT,"
                + COL_MSG_TIMESTAMP + " TEXT,"
                + COL_MSG_TYPE + " TEXT DEFAULT 'text',"
                + COL_MSG_IMAGE_PATH + " TEXT,"
                + COL_MSG_REPLY_TO + " INTEGER DEFAULT 0,"
                + COL_MSG_EDITED + " INTEGER DEFAULT 0,"
                + COL_MSG_READ + " INTEGER DEFAULT 0,"
                + "FOREIGN KEY(" + COL_MSG_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COL_USER_ID + ")"
                + ")";

        String createReactionsTable = "CREATE TABLE " + TABLE_REACTIONS + "("
                + COL_REACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_REACTION_MSG_ID + " INTEGER,"
                + COL_REACTION_USER_ID + " INTEGER,"
                + COL_REACTION_TYPE + " TEXT,"
                + "FOREIGN KEY(" + COL_REACTION_MSG_ID + ") REFERENCES " + TABLE_MESSAGES + "(" + COL_MSG_ID + "),"
                + "UNIQUE(" + COL_REACTION_MSG_ID + ", " + COL_REACTION_USER_ID + ")"
                + ")";

        db.execSQL(createUsersTable);
        db.execSQL(createMessagesTable);
        db.execSQL(createReactionsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_PROFILE_PIC + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_BIO + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_ONLINE_STATUS + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_USERS + " ADD COLUMN " + COL_LAST_SEEN + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_TYPE + " TEXT DEFAULT 'text'");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_IMAGE_PATH + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_REPLY_TO + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_EDITED + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_READ + " INTEGER DEFAULT 0");
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                db.execSQL("ALTER TABLE " + TABLE_MESSAGES + " ADD COLUMN " + COL_MSG_USERNAME + " TEXT");
            } catch (Exception e) {
                e.printStackTrace();
            }

            String createReactionsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_REACTIONS + "("
                    + COL_REACTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COL_REACTION_MSG_ID + " INTEGER,"
                    + COL_REACTION_USER_ID + " INTEGER,"
                    + COL_REACTION_TYPE + " TEXT,"
                    + "UNIQUE(" + COL_REACTION_MSG_ID + ", " + COL_REACTION_USER_ID + ")"
                    + ")";
            db.execSQL(createReactionsTable);
        }
    }

    public boolean registerUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_USERNAME, username);
        values.put(COL_PASSWORD, password);
        values.put(COL_EMAIL, email);
        values.put(COL_BIO, "Hey there! I'm using Chat App");
        values.put(COL_ONLINE_STATUS, 0);

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

    public void updateOnlineStatus(int userId, boolean isOnline) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_ONLINE_STATUS, isOnline ? 1 : 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COL_LAST_SEEN, sdf.format(new Date()));

        db.update(TABLE_USERS, values, COL_USER_ID + "=?", new String[]{String.valueOf(userId)});
    }

    public boolean updateUserProfile(int userId, String bio, String profilePic) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (bio != null) values.put(COL_BIO, bio);
        if (profilePic != null) values.put(COL_PROFILE_PIC, profilePic);

        int result = db.update(TABLE_USERS, values, COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)});
        return result > 0;
    }

    public User getUserProfile(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS,
                null,
                COL_USER_ID + "=?",
                new String[]{String.valueOf(userId)},
                null, null, null);

        if (cursor.moveToFirst()) {
            User user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_BIO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COL_PROFILE_PIC)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(COL_ONLINE_STATUS)) == 1
            );
            cursor.close();
            return user;
        }
        cursor.close();
        return null;
    }

    // Fixed: Added username parameter to method signature
    public boolean addMessage(int userId, String username, String content) {
        return addMessage(userId, username, content, "text", null);
    }

    public boolean addMessage(int userId, String username, String content, String messageType, String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_MSG_USER_ID, userId);
        values.put(COL_MSG_USERNAME, username);
        values.put(COL_MSG_CONTENT, content);
        values.put(COL_MSG_TYPE, messageType);
        values.put(COL_MSG_IMAGE_PATH, imagePath);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        values.put(COL_MSG_TIMESTAMP, sdf.format(new Date()));

        long result = db.insert(TABLE_MESSAGES, null, values);
        return result != -1;
    }

    public List<Message> getAllMessages() {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT m.*, "
                + "(SELECT COUNT(*) FROM " + TABLE_REACTIONS + " WHERE " + COL_REACTION_MSG_ID + "=m." + COL_MSG_ID + ") as reaction_count "
                + "FROM " + TABLE_MESSAGES + " m "
                + "ORDER BY m." + COL_MSG_ID + " DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_IMAGE_PATH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_EDITED)) == 1,
                        cursor.getInt(cursor.getColumnIndexOrThrow("reaction_count"))
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
        values.put(COL_MSG_EDITED, 1);

        int result = db.update(TABLE_MESSAGES, values, COL_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});
        return result > 0;
    }

    public boolean deleteMessage(int messageId) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_REACTIONS, COL_REACTION_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});

        int result = db.delete(TABLE_MESSAGES, COL_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});
        return result > 0;
    }

    public List<Message> searchMessages(String searchQuery) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_MESSAGES
                + " WHERE " + COL_MSG_CONTENT + " LIKE ? "
                + "ORDER BY " + COL_MSG_ID + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + searchQuery + "%"});

        if (cursor.moveToFirst()) {
            do {
                Message message = new Message(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_CONTENT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_TIMESTAMP)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_USER_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COL_MSG_IMAGE_PATH)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COL_MSG_EDITED)) == 1,
                        0
                );
                messageList.add(message);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return messageList;
    }

    public boolean addReaction(int messageId, int userId, String reactionType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_REACTION_MSG_ID, messageId);
        values.put(COL_REACTION_USER_ID, userId);
        values.put(COL_REACTION_TYPE, reactionType);

        long result = db.insertWithOnConflict(TABLE_REACTIONS, null, values,
                SQLiteDatabase.CONFLICT_REPLACE);
        return result != -1;
    }

    public boolean removeReaction(int messageId, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_REACTIONS,
                COL_REACTION_MSG_ID + "=? AND " + COL_REACTION_USER_ID + "=?",
                new String[]{String.valueOf(messageId), String.valueOf(userId)});
        return result > 0;
    }

    public int getReactionCount(int messageId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_REACTIONS + " WHERE " + COL_REACTION_MSG_ID + "=?",
                new String[]{String.valueOf(messageId)});

        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    public String getUserReaction(int messageId, int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_REACTIONS,
                new String[]{COL_REACTION_TYPE},
                COL_REACTION_MSG_ID + "=? AND " + COL_REACTION_USER_ID + "=?",
                new String[]{String.valueOf(messageId), String.valueOf(userId)},
                null, null, null);

        String reaction = null;
        if (cursor.moveToFirst()) {
            reaction = cursor.getString(0);
        }
        cursor.close();
        return reaction;
    }
}