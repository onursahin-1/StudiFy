package model;

import javax.sound.sampled.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class MusicPlayer {
    private static MusicPlayer instance;
    private Clip currentClip;
    private String currentSongPath;
    private static final String MUSIC_DIRECTORY = "src/resources/music/";
    private boolean isPlaying = false;
    
    // Playlist kontrolü için yeni değişkenler
    private List<Song> currentPlaylist;
    private int currentSongIndex = -1;
    private boolean isPlayingFromPlaylist = false;
    
    private MusicPlayer() {
        File directory = new File(MUSIC_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }
    
    public void playPlaylist(List<Song> playlist, int startIndex) throws Exception {
        if (playlist == null || playlist.isEmpty()) {
            throw new Exception("Çalma listesi boş");
        }

        this.currentPlaylist = new ArrayList<>(playlist);
        this.currentSongIndex = startIndex;
        this.isPlayingFromPlaylist = true;

        playSong(playlist.get(startIndex));
    }
    
    public void playNextSong() throws Exception {
        if (!isPlayingFromPlaylist || currentPlaylist == null || currentPlaylist.isEmpty()) {
            throw new Exception("Aktif bir çalma listesi yok");
        }

        currentSongIndex = (currentSongIndex + 1) % currentPlaylist.size();
        Song nextSong = currentPlaylist.get(currentSongIndex);
        playSong(nextSong);
    }
    
    public void playPreviousSong() throws Exception {
        if (!isPlayingFromPlaylist || currentPlaylist == null || currentPlaylist.isEmpty()) {
            throw new Exception("Aktif bir çalma listesi yok");
        }

        currentSongIndex = (currentSongIndex - 1 + currentPlaylist.size()) % currentPlaylist.size();
        Song previousSong = currentPlaylist.get(currentSongIndex);
        playSong(previousSong);
    }
    
    public boolean canPlayNext() {
        return isPlayingFromPlaylist && currentPlaylist != null && 
               !currentPlaylist.isEmpty();
    }
    
    public boolean canPlayPrevious() {
        return isPlayingFromPlaylist && currentPlaylist != null && 
               !currentPlaylist.isEmpty();
    }
    
    public void playSong(Song song) throws Exception {
        if (song == null) {
            throw new Exception("Geçersiz şarkı");
        }

        String fileName = formatFileName(song);
        String filePath = findSongFile(fileName);
        
        if (filePath == null) {
            throw new Exception("Müzik dosyası bulunamadı");
        }

        try {
            if (currentClip != null && currentClip.isRunning()) {
                currentClip.stop();
                currentClip.close();
            }
            
            playWAV(filePath);
            currentSongPath = filePath;
            isPlaying = true;
        } catch (Exception e) {
            throw new Exception("Müzik çalınırken bir hata oluştu: " + e.getMessage());
        }
    }
    
    private void playWAV(String filePath) throws Exception {
        File musicFile = new File(filePath);
        AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
        currentClip = AudioSystem.getClip();
        currentClip.open(audioStream);
        currentClip.start();
    }
    
    public void pauseCurrentSong() {
        if (currentClip != null && currentClip.isRunning()) {
            currentClip.stop();
            isPlaying = false;
        }
    }
    
    public void resumeCurrentSong() {
        if (currentClip != null && !currentClip.isRunning()) {
            currentClip.start();
            isPlaying = true;
        }
    }
    
    public void stopCurrentSong() {
        if (currentClip != null) {
            currentClip.stop();
            currentClip.close();
            currentClip = null;
            isPlaying = false;
            currentSongPath = null;
        }
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean isPlayingFromPlaylist() {
        return isPlayingFromPlaylist;
    }
    
    private String formatFileName(Song song) {
        String baseName = song.getArtist().replaceAll("\\s+", "") + "_" + 
                         song.getName().replaceAll("\\s+", "");
        return baseName;
    }
    
    private String findSongFile(String fileName) {
        try {
            String[] extensions = {".wav", ".aiff"};
            
            for (String ext : extensions) {
                String fullPath = MUSIC_DIRECTORY + fileName + ext;
                if (Files.exists(Paths.get(fullPath))) {
                    return fullPath;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Song> getCurrentPlaylist() {
        return currentPlaylist;
    }
    
    public int getCurrentSongIndex() {
        return currentSongIndex;
    }
    
    public void setCurrentPlaylist(List<Song> playlist) {
        this.currentPlaylist = playlist;
        this.currentSongIndex = -1;
    }
} 