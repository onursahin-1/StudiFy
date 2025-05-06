/**
 * Çalma Listesi Detay Ekranı
 * Seçilen çalma listesinin detaylarını gösterir ve yönetimini sağlar
 * Şarkı ekleme, silme ve çalma işlevlerini içerir
 */
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.plaf.basic.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.List;
import model.*;

public class PlaylistDetailScreen extends JFrame {
    // Sınıf değişkenleri
    private User currentUser;             // Mevcut kullanıcı
    private Playlist currentPlaylist;     // Görüntülenen çalma listesi
    private JPanel songsPanel;           // Şarkıların listelendiği panel
    private JPanel sidebarPanel;         // Sol menü paneli
    private JPanel contentPanel;         // Ana içerik paneli
    private JLabel nowPlayingLabel;      // Çalan şarkı bilgisi etiketi
    private JButton prevButton;          // Önceki şarkı butonu
    private JButton playPauseButton;     // Oynat/Duraklat butonu
    private JButton nextButton;          // Sonraki şarkı butonu
    
    /**
     * Çalma Listesi Detay Ekranı yapıcı metodu
     * @param user Mevcut kullanıcı
     * @param playlist Görüntülenecek çalma listesi
     * Pencere boyutları, başlık ve temel bileşenleri ayarlar
     */
    public PlaylistDetailScreen(User user, Playlist playlist) {
        this.currentUser = user;
        this.currentPlaylist = playlist;
        
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - " + playlist.getName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel - Kar efektli arka plan
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
        
        // Şarkıları listele
        updateSongsList();
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
            } else if (item.equals("Arama")) {
                menuButton.addActionListener(e -> {
                    SearchScreen searchScreen = new SearchScreen(currentUser);
                    searchScreen.setVisible(true);
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
     * Çalma listesi başlığı, şarkı sayısı ve gizlilik ayarlarını içerir
     * Şarkı ekleme butonu ve şarkı listesini gösterir
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
        
        // Çalma listesi başlığı ve bilgileri
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(currentPlaylist.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 32));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel countLabel = new JLabel(currentPlaylist.getMediaCount() + " şarkı");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        countLabel.setForeground(Color.GRAY);
        
        // Gizlilik durumu
        JCheckBox privacyCheckbox = new JCheckBox("Herkese Açık");
        privacyCheckbox.setSelected(currentPlaylist.isPublic());
        privacyCheckbox.setOpaque(false);
        privacyCheckbox.setForeground(Color.WHITE);
        privacyCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        privacyCheckbox.addActionListener(e -> 
            currentPlaylist.setPublic(privacyCheckbox.isSelected()));
        
        titlePanel.add(nameLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(countLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(privacyCheckbox);
        
        // Şarkı ekle butonu
        JButton addButton = new JButton("+ Şarkı Ekle");
        styleAddButton(addButton);
        addButton.addActionListener(e -> showAddSongDialog());
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(addButton, BorderLayout.EAST);
        
        // Şarkılar paneli
        songsPanel = new JPanel();
        songsPanel.setLayout(new BoxLayout(songsPanel, BoxLayout.Y_AXIS));
        songsPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(songsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(30, 30, 30);
            }
        });
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
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
        prevButton = new JButton("⏮");
        playPauseButton = new JButton("⏯");
        nextButton = new JButton("⏭");
        
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
                MusicPlayer player = MusicPlayer.getInstance();
                if (player.canPlayPrevious()) {
                    player.playPreviousSong();
                    updateNowPlayingLabel();
                }
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
                MusicPlayer player = MusicPlayer.getInstance();
                if (player.canPlayNext()) {
                    player.playNextSong();
                    updateNowPlayingLabel();
                }
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
        button.setBackground(text.equals("Çalma Listeleri") ? 
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
                button.setBackground(text.equals("Çalma Listeleri") ? 
                    new Color(178, 34, 34, 200) : new Color(40, 40, 40, 200));
            }
        });
        
        return button;
    }
    
    /**
     * Şarkı ekleme butonunu stillendirir
     * @param button Stillendirilecek buton
     * Butonun görünümünü ve hover efektini düzenler
     */
    private void styleAddButton(JButton button) {
        button.setMaximumSize(new Dimension(300, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(178, 34, 34));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(220, 20, 60));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(178, 34, 34));
            }
        });
    }
    
    /**
     * Şarkı ekleme dialogunu gösterir
     * songs.txt dosyasından şarkıları okur ve listeler
     * Arama ve filtreleme özelliği sunar
     */
    private void showAddSongDialog() {
        JDialog dialog = new JDialog(this, "Şarkı Ekle", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(new Color(30, 30, 30));
        
        // Arama alanı
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setBackground(new Color(40, 40, 40));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Placeholder text
        searchField.setText("Şarkı ara...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Şarkı ara...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Şarkı ara...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        // Sonuçlar listesi
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> resultList = new JList<>(listModel);
        resultList.setBackground(new Color(40, 40, 40));
        resultList.setForeground(Color.WHITE);
        resultList.setSelectionBackground(new Color(178, 34, 34));
        resultList.setSelectionForeground(Color.WHITE);
        resultList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // songs.txt'den şarkıları oku
        try {
            List<String> lines = Files.readAllLines(Paths.get("src/resources/songs/songs.txt"));
            for (String line : lines) {
                listModel.addElement(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // Arama işlevi
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            
            private void search() {
                String searchTerm = searchField.getText().toLowerCase();
                if (searchTerm.equals("şarkı ara...")) return;
                
                listModel.clear();
                try {
                    List<String> lines = Files.readAllLines(Paths.get("src/resources/songs/songs.txt"));
                    for (String line : lines) {
                        if (line.toLowerCase().contains(searchTerm)) {
                            listModel.addElement(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        
        // Şarkı seçme ve ekleme
        resultList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedSong = resultList.getSelectedValue();
                    if (selectedSong != null) {
                        // Şarkıyı çalma listesine ekle
                        String[] parts = selectedSong.split(" - ");
                        if (parts.length == 3) {
                            String name = parts[0];
                            String artist = parts[1];
                            String album = parts[2];
                            Song song = new Song(name, artist, album);
                            addSongToPlaylist(song);
                            dialog.dispose();
                        }
                    }
                }
            }
        });
        
        // Panel düzeni
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(new Color(30, 30, 30));
        searchPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        searchPanel.add(searchField);
        
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(60, 60, 60);
                this.trackColor = new Color(30, 30, 30);
            }
        });
        
        dialog.add(searchPanel, BorderLayout.NORTH);
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Şarkı listesini günceller
     * Çalma listesindeki tüm şarkıları görüntüler
     * Her şarkı için sıra numarası ve kaldırma butonu ekler
     */
    private void updateSongsList() {
        songsPanel.removeAll();
        
        // Şarkıları listele
        List<Media> mediaList = currentPlaylist.getMediaList();
        for (int i = 0; i < mediaList.size(); i++) {
            Media media = mediaList.get(i);
            if (media instanceof Song) {
                JPanel songItem = createSongItem((Song) media, i + 1);
                songsPanel.add(songItem);
                songsPanel.add(Box.createVerticalStrut(10));
            }
        }
        
        // Paneli yenile
        songsPanel.revalidate();
        songsPanel.repaint();
    }
    
    /**
     * Şarkı öğesi paneli oluşturur
     * @param song Görüntülenecek şarkı
     * @param index Şarkının sıra numarası
     * @return Oluşturulan şarkı paneli
     * Şarkı bilgileri ve kontrolleri içeren panel oluşturur
     */
    private JPanel createSongItem(Song song, int index) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout(10, 0));
        item.setBackground(new Color(40, 40, 40, 150));
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        item.setMaximumSize(new Dimension(1000, 60));
        
        // Sıra numarası
        JLabel numberLabel = new JLabel(String.valueOf(index));
        numberLabel.setForeground(Color.GRAY);
        numberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        numberLabel.setPreferredSize(new Dimension(30, 20));
        
        // Şarkı bilgileri
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(song.getDisplayInfo());
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel albumLabel = new JLabel(song.getAlbum());
        albumLabel.setForeground(Color.GRAY);
        albumLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(albumLabel);
        
        // Kaldır butonu
        JButton removeButton = new JButton("×");
        removeButton.setFont(new Font("Arial", Font.BOLD, 18));
        removeButton.setForeground(Color.WHITE);
        removeButton.setBackground(null);
        removeButton.setBorder(null);
        removeButton.setFocusPainted(false);
        removeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        removeButton.addActionListener(e -> {
            removeSongFromPlaylist(song);
        });
        
        item.add(numberLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(removeButton, BorderLayout.EAST);
        
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
                        List<Song> songs = currentPlaylist.getMediaList().stream()
                            .filter(media -> media instanceof Song)
                            .map(media -> (Song) media)
                            .toList();
                        MusicPlayer.getInstance().playPlaylist(songs, index - 1);
                        updateNowPlayingLabel();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(PlaylistDetailScreen.this,
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
        albumLabel.addMouseListener(mouseAdapter);
        numberLabel.addMouseListener(mouseAdapter);
        
        return item;
    }
    
    /**
     * Şarkıyı çalma listesine ekler
     * @param song Eklenecek şarkı
     * Ücretsiz kullanıcılar için şarkı limiti kontrolü yapar
     */
    private void addSongToPlaylist(Song song) {
        try {
            currentPlaylist.addMedia(song);
            PlaylistManager.getInstance().updatePlaylist(currentPlaylist);
            updateSongsList();
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage() + "\nPremium üyeliğe geçerek sınırsız şarkı ekleyebilirsiniz.",
                "Şarkı Ekleme Limiti",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Şarkıyı çalma listesinden kaldırır
     * @param song Kaldırılacak şarkı
     * Çalma listesini günceller ve değişiklikleri kaydeder
     */
    private void removeSongFromPlaylist(Song song) {
        currentPlaylist.removeMedia(song);
        PlaylistManager.getInstance().updatePlaylist(currentPlaylist);
        updateSongsList();
    }
    
    /**
     * Çalma listesinin gizlilik durumunu günceller
     * @param isPublic Yeni gizlilik durumu
     * Değişiklikleri kaydeder ve çalma listesini günceller
     */
    private void updatePlaylistVisibility(boolean isPublic) {
        currentPlaylist.setPublic(isPublic);
        PlaylistManager.getInstance().updatePlaylist(currentPlaylist);
    }
    
    /**
     * Çalan şarkı bilgisini günceller
     * MusicPlayer'dan güncel şarkı bilgisini alır
     * Şarkı çalmıyorsa varsayılan mesajı gösterir
     */
    private void updateNowPlayingLabel() {
        MusicPlayer player = MusicPlayer.getInstance();
        List<Song> playlist = player.getCurrentPlaylist();
        if (playlist != null && !playlist.isEmpty() && player.isPlayingFromPlaylist()) {
            int currentIndex = player.getCurrentSongIndex();
            if (currentIndex >= 0 && currentIndex < playlist.size()) {
                Song currentSong = playlist.get(currentIndex);
                nowPlayingLabel.setText("🎵 " + currentSong.getName() + " - " + currentSong.getArtist());
                return;
            }
        }
        nowPlayingLabel.setText("🎵 Şarkı çalmıyor");
    }
} 