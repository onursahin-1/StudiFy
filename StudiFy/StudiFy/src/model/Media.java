package model;

public abstract class Media {
    private String name;
    
    public Media(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public abstract String getDisplayInfo();
} 