package com.example.chatapplication;

public class User {
    private int id;
    private String username;
    private String email;
    private String bio;
    private String profilePic;
    private boolean isOnline;

    public User(int id, String username, String email, String bio,
                String profilePic, boolean isOnline) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.profilePic = profilePic;
        this.isOnline = isOnline;
    }


    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getBio() { return bio; }
    public String getProfilePic() { return profilePic; }
    public boolean isOnline() { return isOnline; }

    public void setId(int id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setBio(String bio) { this.bio = bio; }
    public void setProfilePic(String profilePic) { this.profilePic = profilePic; }
    public void setOnline(boolean online) { isOnline = online; }
}