/**
 * Ana ekran sınıfı - Uygulamanın ana arayüzünü oluşturur
 * Kullanıcıya önerilen şarkıları gösterir ve temel navigasyon işlevlerini sağlar
 * Ayrıca müzik çalma kontrollerini içerir
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import model.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

// Ana ekran sınıfı - Uygulamanın giriş noktası ve ana arayüzü
// Kullanıcıya önerilen şarkıları gösterir ve temel navigasyon işlevlerini sağlar
public class MainScreen extends JFrame {
    // Sınıf değişkenleri
    private JPanel sidebarPanel;          // Sol menü paneli - Logo, navigasyon butonları ve kullanıcı profili
    private JPanel contentPanel;          // Ana içerik paneli - Önerilen şarkılar ve diğer içerikler
    private User currentUser;             // Mevcut kullanıcı - Oturum açmış kullanıcının bilgileri
    private JLabel nowPlayingLabel;       // Çalan şarkı bilgisi etiketi - Alt çalma çubuğunda gösterilir
    
    /**
     * Ana ekranın yapıcı metodu
     * @param user Oturum açmış kullanıcı bilgisi
     * Pencere boyutları, başlık ve temel bileşenleri ayarlar
     */
    public MainScreen(User user) {
        this.currentUser = user;
        
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - Ana Menü");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Ana panel - Kar efektli arka plan
        SnowPanel mainPanel = new SnowPanel();
        setContentPane(mainPanel);
        mainPanel.setLayout(new BorderLayout(0, 0));
        
        // Sidebar oluşturma - Sol menü paneli
        createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Ana içerik alanı - Önerilen şarkılar
        createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Alt çalma çubuğu - Müzik kontrolleri
        createPlayerPanel();
    }
    
    /**
     * Sol menü panelini oluşturur
     * Logo, kullanıcı profili ve navigasyon butonlarını içerir
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
            
            // Arama butonu için özel işlevsellik ekle
            if (item.equals("Arama")) {
                menuButton.addActionListener(e -> {
                    SearchScreen searchScreen = new SearchScreen(currentUser);
                    searchScreen.setVisible(true);
                    this.dispose();
                });
            }
            // Çalma Listeleri butonu için özel işlevsellik ekle
            else if (item.equals("Çalma Listeleri")) {
                menuButton.addActionListener(e -> {
                    PlaylistScreen playlistScreen = new PlaylistScreen(currentUser);
                    playlistScreen.setVisible(true);
                    this.dispose();
                });
            }
            // Beğendiklerim butonu için özel işlevsellik ekle
            else if (item.equals("Beğendiklerim")) {
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
     * Hoş geldin mesajı ve önerilen şarkıları gösterir
     * Şarkılar kartlar halinde görüntülenir
     */
    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Hoş geldin mesajı
        JLabel welcomeLabel = new JLabel("Hoş Geldin!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 32));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Önerilen şarkılar
        JLabel suggestionsLabel = new JLabel("Önerilen Şarkılar");
        suggestionsLabel.setFont(new Font("Arial", Font.BOLD, 24));
        suggestionsLabel.setForeground(Color.WHITE);
        suggestionsLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Önerilen şarkılar paneli
        JPanel suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        suggestionsPanel.setOpaque(false);

        // Önerilen şarkıları ekle
        Song[] suggestedSongs = {
            new Song("İki Gözüm", "Sezen Aksu", "Single"),
            new Song("Kaçak", "Sezen Aksu", "Single"),
            new Song("Gülümse", "Sezen Aksu", "Single")
        };

        for (Song song : suggestedSongs) {
            JPanel songCard = createSongCard(song);
            suggestionsPanel.add(songCard);
        }

        // Panelleri yerleştir
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(welcomeLabel);
        topPanel.add(suggestionsLabel);

        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(suggestionsPanel, BorderLayout.CENTER);
    }

    /**
     * Şarkı kartı oluşturur
     * @param song Kart olarak gösterilecek şarkı bilgisi
     * @return Oluşturulan şarkı kartı paneli
     * Kapak resmi, şarkı adı ve sanatçı bilgisini içeren kart oluşturur
     * Hover efekti ve çift tıklama ile şarkı çalma özelliği ekler
     */
    private JPanel createSongCard(Song song) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(new Color(40, 40, 40, 150));
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        card.setPreferredSize(new Dimension(200, 250));

        // Kapak fotoğrafı
        String coverPath = "src/resources/covers/";
        if (song.getAlbum().equals("Single")) {
            coverPath += song.getName().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        } else {
            coverPath += song.getAlbum().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        }

        JLabel coverLabel = new JLabel();
        coverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        try {
            if (Files.exists(Paths.get(coverPath))) {
                ImageIcon originalIcon = new ImageIcon(coverPath);
                Image scaledImage = originalIcon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                coverLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.out.println("Kapak resmi bulunamadı: " + coverPath);
                coverLabel.setText("🎵");
                coverLabel.setFont(new Font("Arial", Font.PLAIN, 48));
            }
        } catch (Exception e) {
            System.out.println("Kapak resmi yüklenirken hata: " + e.getMessage());
            coverLabel.setText("🎵");
            coverLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        }

        // Şarkı bilgileri
        JLabel titleLabel = new JLabel(song.getName());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel artistLabel = new JLabel(song.getArtist());
        artistLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        artistLabel.setForeground(Color.GRAY);
        artistLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(coverLabel);
        card.add(Box.createVerticalStrut(10));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(artistLabel);

        // Hover efekti ve çift tıklama
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(178, 34, 34, 150));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(new Color(40, 40, 40, 150));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    try {
                        MusicPlayer.getInstance().playSong(song);
                        updateNowPlayingLabel();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(MainScreen.this,
                            "Müzik çalınırken bir hata oluştu: " + ex.getMessage(),
                            "Hata",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        return card;
    }
    
    /**
     * Şarkı için kapak resmini yükler
     * @param song Kapak resmi yüklenecek şarkı
     * @return Yüklenen kapak resmi ikonu
     * Önce albüm adıyla, bulunamazsa şarkı adıyla arar
     * Bulunamazsa varsayılan müzik ikonu gösterir
     */
    private ImageIcon loadCoverImage(Song song) {
        String coverPath = "src/resources/covers/";
        if (song.getAlbum().equals("Single")) {
            coverPath += song.getName().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        } else {
            coverPath += song.getAlbum().replaceAll("[^a-zA-Z0-9]", "") + ".jpg";
        }

        try {
            if (Files.exists(Paths.get(coverPath))) {
                return new ImageIcon(coverPath);
            } else {
                System.out.println("Kapak resmi bulunamadı: " + coverPath);
                return new ImageIcon("src/resources/covers/default.jpg");
            }
        } catch (Exception e) {
            System.out.println("Kapak resmi yüklenirken hata: " + e.getMessage());
            return new ImageIcon("src/resources/covers/default.jpg");
        }
    }
    
    /**
     * Çalma kontrollerini içeren alt panel
     * Şarkı bilgisi ve kontrol butonlarını (önceki, oynat/duraklat, sonraki) içerir
     * Her buton için gerekli olay dinleyicilerini ekler
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
            stylePlayerButton(button);
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
     * Şu an çalan şarkı bilgisini günceller
     * MusicPlayer'dan güncel şarkı bilgisini alır ve etiketi günceller
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
                nowPlayingLabel.setText("🎵 " + songInfo);
                return;
            }
        }
        nowPlayingLabel.setText("🎵 Şarkı çalmıyor");
    }
    
    /**
     * Menü butonlarını oluşturur ve stillendirir
     * @param text Buton üzerinde gösterilecek metin
     * @return Oluşturulan menü butonu
     * Her buton için tutarlı görünüm sağlar
     * Hover efekti ve tıklama olayları ekler
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(210, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 40, 40, 200));
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
                button.setBackground(new Color(40, 40, 40, 200));
            }
        });
        
        return button;
    }
    
    /**
     * Çalma kontrol butonlarını stillendirir
     * @param button Stillendirilecek buton
     * Oynatma kontrollerinin görünümünü düzenler
     * Tutarlı font ve renk şeması uygular
     */
    private void stylePlayerButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(null);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    /**
     * Arama butonunu stillendirir
     * @param button Stillendirilecek buton
     * Arama butonunun görünümünü düzenler
     * Hover efekti ve özel renk şeması uygular
     */
    private void styleSearchButton(JButton button) {
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(18, 18, 18));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover efekti
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(40, 40, 40));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(18, 18, 18));
            }
        });
    }
} 