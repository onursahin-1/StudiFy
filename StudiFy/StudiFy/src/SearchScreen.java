/**
 * Arama Ekranı
 * Kullanıcının şarkı aramasını ve sonuçları görüntülemesini sağlar
 * Arama sonuçlarından şarkı çalma ve yönetme özelliklerini içerir
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

import model.MusicPlayer;
import model.Song;
import model.User;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.ArrayList;

public class SearchScreen extends JFrame {
    // Arama ve sonuç bileşenleri
    private JTextField searchField;        // Arama metin kutusu
    private JPanel resultPanel;           // Arama sonuçlarının gösterildiği panel
    private User currentUser;             // Mevcut kullanıcı
    
    // Dosya yolları
    private static final String SONGS_FILE = "src/resources/songs/songs.txt";    // Şarkı listesi dosyası
    private static final String COVERS_PATH = "src/resources/covers/";           // Albüm kapakları dizini
    
    // Arayüz bileşenleri
    private JPanel sidebarPanel;          // Sol menü paneli
    private JPanel contentPanel;          // Ana içerik paneli
    private JLabel nowPlayingLabel;       // Çalan şarkı bilgisi etiketi

    /**
     * Arama Ekranı yapıcı metodu
     * @param user Mevcut kullanıcı
     * Pencere boyutları, başlık ve temel bileşenleri ayarlar
     */
    public SearchScreen(User user) {
        this.currentUser = user;
        
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - Arama");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel
        SnowPanel mainPanel = new SnowPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));
        
        // Sol menü
        createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Ana içerik
        createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Alt çalma çubuğu
        createPlayerPanel();
    }
    
    /**
     * Sol menü panelini oluşturur
     * Logo, navigasyon butonları ve kullanıcı profilini içerir
     * Her buton ilgili ekrana yönlendirme yapar
     */
    private void createSidebar() {
        sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, getHeight()));
        sidebarPanel.setBackground(new Color(20, 20, 20, 200));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Logo
        JLabel logoLabel = new JLabel("🎄");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 32));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebarPanel.add(logoLabel);
        
        // Menü başlıkları
        String[] menuItems = {"Ana Sayfa", "Arama", "Çalma Listeleri", "Beğendiklerim"};
        for (String item : menuItems) {
            JButton menuButton = createMenuButton(item);
            
            if (item.equals("Ana Sayfa")) {
                menuButton.addActionListener(e -> {
                    MainScreen mainScreen = new MainScreen(currentUser);
                    mainScreen.setVisible(true);
                    this.dispose();
                });
            } else if (item.equals("Çalma Listeleri")) {
                menuButton.addActionListener(e -> {
                    PlaylistScreen playlistScreen = new PlaylistScreen(currentUser);
                    playlistScreen.setVisible(true);
                    this.dispose();
                });
            } else if (item.equals("Beğendiklerim")) {
                menuButton.addActionListener(e -> {
                    LikedSongsScreen likedSongsScreen = new LikedSongsScreen(currentUser);
                    likedSongsScreen.setVisible(true);
                    this.dispose();
                });
            }
            
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(menuButton);
        }
        
        // Kullanıcı profili
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        profilePanel.setBackground(new Color(30, 30, 30, 200));
        profilePanel.setMaximumSize(new Dimension(210, 60));
        
        JLabel avatarLabel = new JLabel("👤");
        avatarLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        JLabel usernameLabel = new JLabel(currentUser.getUsername());
        usernameLabel.setForeground(Color.WHITE);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        profilePanel.add(avatarLabel);
        profilePanel.add(usernameLabel);
        
        sidebarPanel.add(Box.createVerticalGlue());
        sidebarPanel.add(profilePanel);
    }
    
    /**
     * Ana içerik panelini oluşturur
     * Arama alanı ve sonuçların görüntülendiği paneli içerir
     * Arama işlevselliğini ve sonuçların dinamik güncellenmesini sağlar
     */
    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Üst başlık alanı
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        // Arama alanı
        searchField = new JTextField(20);
        styleSearchField(searchField);
        searchField.setMaximumSize(new Dimension(600, 40));
        headerPanel.add(searchField, BorderLayout.NORTH);
        
        // Arama sonuçları paneli
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BoxLayout(resultsPanel, BoxLayout.Y_AXIS));
        resultsPanel.setOpaque(false);
        resultsPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Arama sonuçlarını göster
        JScrollPane scrollPane = new JScrollPane(resultsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Arama işlevi
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            
            private void search() {
                String searchTerm = searchField.getText().toLowerCase();
                resultsPanel.removeAll();
                
                // Şarkıları ara
                try {
                    List<String> lines = Files.readAllLines(Paths.get(SONGS_FILE));
                    for (String line : lines) {
                        if (line.toLowerCase().contains(searchTerm)) {
                            JPanel songItem = createSongResultItem(line);
                            resultsPanel.add(songItem);
                            resultsPanel.add(Box.createVerticalStrut(10));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
                if (resultsPanel.getComponentCount() == 0) {
                    JLabel noResultsLabel = new JLabel("Sonuç bulunamadı");
                    noResultsLabel.setForeground(Color.WHITE);
                    noResultsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    resultsPanel.add(noResultsLabel);
                }
                
                resultsPanel.revalidate();
                resultsPanel.repaint();
            }
        });
    }
    
    /**
     * Alt çalma çubuğunu oluşturur
     * Şarkı bilgisi ve kontrol butonlarını içerir
     * Önceki, oynat/duraklat ve sonraki şarkı kontrollerini sağlar
     */
    private void createPlayerPanel() {
        JPanel playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        playerPanel.setBackground(new Color(30, 30, 30, 230));
        playerPanel.setLayout(new BorderLayout());
        playerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Şarkı bilgisi
        JPanel songInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        songInfoPanel.setOpaque(false);
        
        nowPlayingLabel = new JLabel("🎵 Şarkı çalmıyor");
        nowPlayingLabel.setForeground(Color.WHITE);
        nowPlayingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        songInfoPanel.add(nowPlayingLabel);
        
        // Kontroller
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.setOpaque(false);
        
        // Kontrol butonları
        JButton prevButton = new JButton("⏮");
        JButton playPauseButton = new JButton("⏯");
        JButton nextButton = new JButton("⏭");
        
        // Butonları stille
        for (JButton button : new JButton[]{prevButton, playPauseButton, nextButton}) {
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.setForeground(Color.WHITE);
            button.setBackground(null);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
        // Önceki şarkı butonu için dinleyici
        prevButton.addActionListener(e -> {
            try {
                MusicPlayer.getInstance().playPreviousSong();
                updateNowPlayingLabel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Önceki şarkıya geçilirken bir hata oluştu: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Oynat/Duraklat tuşu için dinleyici
        playPauseButton.addActionListener(e -> {
            MusicPlayer player = MusicPlayer.getInstance();
            if (player.isPlaying()) {
                player.pauseCurrentSong();
                playPauseButton.setText("⏵");
            } else {
                player.resumeCurrentSong();
                playPauseButton.setText("⏸");
            }
        });
        
        // Sonraki şarkı butonu için dinleyici
        nextButton.addActionListener(e -> {
            try {
                MusicPlayer.getInstance().playNextSong();
                updateNowPlayingLabel();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Sonraki şarkıya geçilirken bir hata oluştu: " + ex.getMessage(),
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        controlsPanel.add(prevButton);
        controlsPanel.add(playPauseButton);
        controlsPanel.add(nextButton);
        
        playerPanel.add(songInfoPanel, BorderLayout.WEST);
        playerPanel.add(controlsPanel, BorderLayout.CENTER);
        
        add(playerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Menü butonlarını oluşturur ve stillendirir
     * @param text Buton üzerinde gösterilecek metin
     * @return Oluşturulan menü butonu
     * Her buton için tutarlı görünüm ve hover efekti sağlar
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(210, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(text.equals("Arama") ? 
            new Color(178, 34, 34, 200) : new Color(40, 40, 40, 200));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60, 200));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(text.equals("Arama") ? 
                    new Color(178, 34, 34, 200) : new Color(40, 40, 40, 200));
            }
        });
        
        return button;
    }
    
    /**
     * Arama alanını stillendirir
     * @param field Stillendirilecek alan
     * Alanın görünümünü ve odaklanma davranışını düzenler
     */
    private void styleBackButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 40, 40, 150));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(50, 40));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(60, 60, 60, 150));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(40, 40, 40, 150));
            }
        });
    }
    
    /**
     * Arama alanını stillendirir
     * @param field Stillendirilecek alan
     * Alanın görünümünü ve odaklanma davranışını düzenler
     */
    private void styleSearchField(JTextField field) {
        field.setMaximumSize(new Dimension(300, 40));
        field.setBackground(new Color(40, 40, 40, 230));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setOpaque(true);
        
        // Placeholder
        field.setText("Şarkı, sanatçı veya albüm ara...");
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals("Şarkı, sanatçı veya albüm ara...")) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("Şarkı, sanatçı veya albüm ara...");
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    /**
     * Arama sonucu öğesi oluşturur
     * @param songInfo Şarkı bilgisi metni
     * @return Oluşturulan sonuç paneli
     * Şarkı bilgilerini ve kontrolleri içeren panel oluşturur
     */
    private JPanel createSongResultItem(String songInfo) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout(10, 0));
        item.setBackground(new Color(40, 40, 40, 150));
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        item.setMaximumSize(new Dimension(300, 60));

        // Şarkı bilgilerini ayır
        String[] parts = songInfo.split(" - ");
        final String songName;
        final String artistName;
        final String albumName;

        // Parçaları güvenli bir şekilde ayır
        try {
            songName = parts.length >= 1 ? parts[0].trim() : "";
            artistName = parts.length >= 2 ? parts[1].trim() : "";
            albumName = parts.length >= 3 ? parts[2].trim() : "";
        } catch (Exception e) {
            System.err.println("Hatalı format: " + songInfo);
            return item;
        }

        // Kapak resmi yolu belirleme
        String coverPath = COVERS_PATH;
        if (albumName.equals("Single")) {
            coverPath += songName.replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        } else if (!albumName.isEmpty()) {
            coverPath += albumName.replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        } else {
            coverPath += "default.jpg";
        }

        JLabel coverLabel = new JLabel();
        
        // Kapak resmini yükle
        try {
            if (Files.exists(Paths.get(coverPath))) {
                ImageIcon originalIcon = new ImageIcon(coverPath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                coverLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.out.println("Kapak resmi bulunamadı: " + coverPath);
                coverLabel.setText("🎵");
                coverLabel.setFont(new Font("Arial", Font.PLAIN, 24));
            }
        } catch (Exception e) {
            System.out.println("Kapak resmi yüklenirken hata: " + e.getMessage());
            coverLabel.setText("🎵");
            coverLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        }

        // Şarkı bilgileri paneli
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        // Başlık etiketi
        JLabel titleLabel = new JLabel(songName);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Alt bilgi etiketi
        String subInfo = artistName;
        if (!albumName.isEmpty()) {
            subInfo += " - " + albumName;
        }
        JLabel artistLabel = new JLabel(subInfo);
        artistLabel.setForeground(Color.GRAY);
        artistLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        infoPanel.add(titleLabel);
        infoPanel.add(artistLabel);

        item.add(coverLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);

        // Hover efekti ve çift tıklama
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(178, 34, 34, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(40, 40, 40, 150));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        // Şarkı nesnesini oluştur ve çal
                        Song song = new Song(songName, artistName, albumName);
                        MusicPlayer.getInstance().playSong(song);
                        updateNowPlayingLabel();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(SearchScreen.this,
                            "Müzik çalınırken bir hata oluştu: " + ex.getMessage(),
                            "Hata",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        };

        // Tüm bileşenlere mouse adapter'ı ekle
        item.addMouseListener(mouseAdapter);
        infoPanel.addMouseListener(mouseAdapter);
        titleLabel.addMouseListener(mouseAdapter);
        artistLabel.addMouseListener(mouseAdapter);

        return item;
    }

    /**
     * Çalan şarkı bilgisini günceller
     * MusicPlayer'dan güncel şarkı bilgisini alır
     * Şarkı çalmıyorsa varsayılan mesajı gösterir
     */
    private void updateNowPlayingLabel() {
        MusicPlayer player = MusicPlayer.getInstance();
        List<Song> playlist = player.getCurrentPlaylist();
        if (playlist != null && !playlist.isEmpty()) {
            int currentIndex = player.getCurrentSongIndex();
            if (currentIndex >= 0 && currentIndex < playlist.size()) {
                Song currentSong = playlist.get(currentIndex);
                String songInfo = currentSong.getName() + " - " + currentSong.getArtist();
                nowPlayingLabel.setText("Çalıyor: " + songInfo);
                return;
            }
        }
        nowPlayingLabel.setText("Şarkı çalmıyor");
    }
}