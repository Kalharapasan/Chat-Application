package com.example.chatapplication;

public class Message {
    private int id;
    private String username;
    private String content;
    private String timestamp;
    private int userId;
    private String messageType;
    private String imagePath;
    private boolean isEdited;
    private int reactionCount;


    public Message(int id, String username, String content, String timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = "text";
        this.isEdited = false;
        this.reactionCount = 0;
    }


    public Message(int id, String username, String content, String timestamp,
                   int userId, String messageType, String imagePath,
                   boolean isEdited, int reactionCount) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
        this.userId = userId;
        this.messageType = messageType;
        this.imagePath = imagePath;
        this.isEdited = isEdited;
        this.reactionCount = reactionCount;
    }


    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
    public int getUserId() { return userId; }
    public String getMessageType() { return messageType; }
    public String getImagePath() { return imagePath; }
    public boolean isEdited() { return isEdited; }
    public int getReactionCount() { return reactionCount; }


    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setContent(String content) { this.content = content; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    public void setEdited(boolean edited) { isEdited = edited; }
    public void setReactionCount(int reactionCount) { this.reactionCount = reactionCount; }
}