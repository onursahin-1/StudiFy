package model;

import java.util.List;
import java.util.ArrayList;

public class Playlist {
    private String name;
    private User owner;
    private List<Media> mediaList;
    private boolean isPublic;
    
    public Playlist(String name, User owner) {
        this.name = name;
        this.owner = owner;
        this.mediaList = new ArrayList<>();
        this.isPublic = false;
    }
    
    public void addMedia(Media media) {
        if (owner instanceof FreeUser && mediaList.size() >= 5) {
            throw new IllegalStateException("Ücretsiz kullanıcılar en fazla 5 şarkı ekleyebilir.");
        }
        
        if (media instanceof Song) {
            Song newSong = (Song) media;
            for (Media existingMedia : mediaList) {
                if (existingMedia instanceof Song) {
                    Song existingSong = (Song) existingMedia;
                    if (existingSong.getName().equals(newSong.getName()) && 
                        existingSong.getArtist().equals(newSong.getArtist()) &&
                        existingSong.getAlbum().equals(newSong.getAlbum())) {
                        throw new IllegalStateException("Bu şarkı zaten çalma listenizde bulunuyor.");
                    }
                }
            }
        }
        
        mediaList.add(media);
    }
    
    public void removeMedia(Media media) {
        mediaList.remove(media);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public List<Media> getMediaList() {
        return mediaList;
    }
    
    public int getMediaCount() {
        return mediaList.size();
    }
    
    public boolean isPublic() {
        return isPublic;
    }
    
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }
} 