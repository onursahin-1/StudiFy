package model;

public class PremiumUser extends User {
    private String subscriptionType; // "monthly", "yearly"
    private boolean familyPlan;
    
    public PremiumUser(String username, String email, String password, String subscriptionType) {
        super(username, email, password);
        this.subscriptionType = subscriptionType;
    }
    
    @Override
    public boolean canSkipAds() {
        return true;
    }
    
    @Override
    public int getMaxPlaylistCount() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public boolean canDownloadSongs() {
        return true;
    }
    
    @Override
    public String getMembershipType() {
        return "Premium";
    }
} 