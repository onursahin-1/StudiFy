package model;

public class FreeUser extends User {
    private int adCount;
    
    public FreeUser(String username, String email, String password) {
        super(username, email, password);
        this.adCount = 0;
    }
    
    @Override
    public boolean canSkipAds() {
        return false;
    }
    
    @Override
    public int getMaxPlaylistCount() {
        return 5;
    }
    
    @Override
    public boolean canDownloadSongs() {
        return false;
    }
    
    @Override
    public String getMembershipType() {
        return "Free";
    }
} 