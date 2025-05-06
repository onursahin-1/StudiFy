package model;

public class Song extends Media {
    private String artist;
    private String album;
    private String duration;
    private String genre;
    
    public Song(String name, String artist, String album) {
        super(name);
        this.artist = artist;
        this.album = album;
        this.duration = "";
        this.genre = "";
    }
    
    public String getArtist() {
        return artist;
    }
    
    public void setArtist(String artist) {
        this.artist = artist;
    }
    
    public String getAlbum() {
        return album;
    }
    
    public void setAlbum(String album) {
        this.album = album;
    }
    
    public String getDuration() {
        return duration;
    }
    
    public void setDuration(String duration) {
        this.duration = duration;
    }
    
    public String getGenre() {
        return genre;
    }
    
    public void setGenre(String genre) {
        this.genre = genre;
    }
    
    @Override
    public String getDisplayInfo() {
        return getName() + " - " + artist;
    }
    
    @Override
    public String toString() {
        return getName() + " - " + artist + " - " + album;
    }
} 