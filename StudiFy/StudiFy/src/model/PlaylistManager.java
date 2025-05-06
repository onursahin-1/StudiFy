package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PlaylistManager {
    private static PlaylistManager instance;
    private Map<String, List<Playlist>> userPlaylists; // email -> playlists
    private static final String PLAYLISTS_FILE = "src/resources/playlists.txt";
    
    private PlaylistManager() {
        userPlaylists = new HashMap<>();
        loadPlaylists();
    }
    
    public static PlaylistManager getInstance() {
        if (instance == null) {
            instance = new PlaylistManager();
        }
        return instance;
    }
    
    private void loadPlaylists() {
        try {
            // Dosya yoksa oluştur
            File file = new File(PLAYLISTS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return;
            }
            
            // Dosyayı oku
            List<String> lines = Files.readAllLines(Paths.get(PLAYLISTS_FILE));
            
            // Her satırı işle (format: ownerEmail,playlistName,isPublic,song1|song2|song3)
            for (String line : lines) {
                String[] parts = line.split(",", 4); // En fazla 4 parça
                if (parts.length >= 3) {
                    String ownerEmail = parts[0];
                    String playlistName = parts[1];
                    boolean isPublic = Boolean.parseBoolean(parts[2]);
                    
                    // Playlist'i oluştur
                    User owner = UserManager.getInstance().getUserByEmail(ownerEmail);
                    if (owner != null) {
                        Playlist playlist = new Playlist(playlistName, owner);
                        playlist.setPublic(isPublic);
                        
                        // Şarkıları ekle
                        if (parts.length == 4 && !parts[3].isEmpty()) {
                            String[] songs = parts[3].split("\\|");
                            for (String songInfo : songs) {
                                String[] songParts = songInfo.split(" - ");
                                if (songParts.length == 3) {
                                    String name = songParts[0];
                                    String artist = songParts[1];
                                    String album = songParts[2];
                                    Song song = new Song(name, artist, album);
                                    playlist.addMedia(song);
                                }
                            }
                        }
                        
                        // Playlist'i kullanıcıya ekle
                        userPlaylists.computeIfAbsent(ownerEmail, k -> new ArrayList<>()).add(playlist);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            userPlaylists = new HashMap<>();
        }
    }
    
    private void savePlaylists() {
        try {
            List<String> lines = new ArrayList<>();
            
            // Her playlist için bir satır oluştur
            for (Map.Entry<String, List<Playlist>> entry : userPlaylists.entrySet()) {
                String ownerEmail = entry.getKey();
                List<Playlist> playlists = entry.getValue();
                
                for (Playlist playlist : playlists) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(ownerEmail).append(",");
                    sb.append(playlist.getName()).append(",");
                    sb.append(playlist.isPublic()).append(",");
                    
                    // Şarkıları ekle
                    List<String> songInfos = new ArrayList<>();
                    for (Media media : playlist.getMediaList()) {
                        if (media instanceof Song) {
                            Song song = (Song) media;
                            songInfos.add(song.toString()); // toString() metodunu kullan
                        }
                    }
                    sb.append(String.join("|", songInfos));
                    
                    lines.add(sb.toString());
                }
            }
            
            // Dosyaya yaz
            Files.write(Paths.get(PLAYLISTS_FILE), lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public List<Playlist> getUserPlaylists(String userEmail) {
        return userPlaylists.getOrDefault(userEmail, new ArrayList<>());
    }
    
    public void addPlaylist(Playlist playlist) {
        String ownerEmail = playlist.getOwner().getEmail();
        userPlaylists.computeIfAbsent(ownerEmail, k -> new ArrayList<>()).add(playlist);
        savePlaylists();
    }
    
    public void updatePlaylist(Playlist playlist) {
        String ownerEmail = playlist.getOwner().getEmail();
        List<Playlist> playlists = userPlaylists.get(ownerEmail);
        if (playlists != null) {
            for (int i = 0; i < playlists.size(); i++) {
                if (playlists.get(i).getName().equals(playlist.getName())) {
                    playlists.set(i, playlist);
                    break;
                }
            }
            savePlaylists();
        }
    }
    
    public void removePlaylist(Playlist playlist) {
        String ownerEmail = playlist.getOwner().getEmail();
        List<Playlist> playlists = userPlaylists.get(ownerEmail);
        if (playlists != null) {
            playlists.remove(playlist);
            savePlaylists();
        }
    }
} 