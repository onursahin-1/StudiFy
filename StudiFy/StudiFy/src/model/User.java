package model;

public abstract class User {
    protected String username;
    protected String email;
    protected String password;
    protected String profilePicture;
    
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    
    public abstract boolean canSkipAds();
    public abstract int getMaxPlaylistCount();
    public abstract boolean canDownloadSongs();
    public abstract String getMembershipType();
    
    public String getPassword() {
        return password;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getUsername() {
        return username;
    }
} 