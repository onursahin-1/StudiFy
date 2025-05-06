/**
 * Çalma Listeleri Ekranı
 * Kullanıcının çalma listelerini görüntülemesini ve yönetmesini sağlar
 * Yeni çalma listesi oluşturma ve mevcut listeleri düzenleme özelliklerini içerir
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import model.*;
import java.util.List;
import java.util.ArrayList;

public class PlaylistScreen extends JFrame {
    // Sınıf değişkenleri
    private User currentUser;          // Mevcut kullanıcı
    private JPanel playlistPanel;      // Çalma listelerinin görüntülendiği panel
    private List<Playlist> playlists;  // Kullanıcının çalma listeleri
    
    private JPanel sidebarPanel;       // Sol menü paneli
    private JPanel contentPanel;       // Ana içerik paneli
    
    /**
     * Çalma Listeleri Ekranı yapıcı metodu
     * @param user Mevcut kullanıcı
     * Pencere boyutları, başlık ve temel bileşenleri ayarlar
     */
    public PlaylistScreen(User user) {
        this.currentUser = user;
        this.playlists = new ArrayList<>(); // Gerçek uygulamada veritabanından çekilecek
        
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - Çalma Listeleri");
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
        
        // Örnek çalma listeleri ekle
        addSamplePlaylists();
        updatePlaylistPanel();
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
     * Çalma listeleri başlığı ve yeni liste oluşturma butonu içerir
     * Mevcut çalma listelerini görüntüler
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
        
        JLabel titleLabel = new JLabel("Çalma Listeleri");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        // Yeni çalma listesi oluştur butonu
        JButton createButton = new JButton("+ Yeni Çalma Listesi");
        styleCreateButton(createButton);
        createButton.addActionListener(e -> showCreatePlaylistDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(createButton, BorderLayout.EAST);
        
        // Çalma listeleri paneli
        playlistPanel = new JPanel();
        playlistPanel.setLayout(new BoxLayout(playlistPanel, BoxLayout.Y_AXIS));
        playlistPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(playlistPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
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
        
        JLabel songLabel = new JLabel("🎵 Şarkı Adı");
        songLabel.setForeground(Color.WHITE);
        songLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        songInfoPanel.add(songLabel);
        
        // Kontroller
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlsPanel.setOpaque(false);
        
        String[] controls = {"⏮", "⏯", "⏭"};
        for (String control : controls) {
            JButton button = new JButton(control);
            button.setFont(new Font("Arial", Font.PLAIN, 24));
            button.setForeground(Color.WHITE);
            button.setBackground(null);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            controlsPanel.add(button);
        }
        
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
     * Geri dönüş butonunu stillendirir
     * @param button Stillendirilecek buton
     * Butonun görünümünü ve hover efektini düzenler
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
     * Yeni çalma listesi oluşturma butonunu stillendirir
     * @param button Stillendirilecek buton
     * Butonun görünümünü ve hover efektini düzenler
     */
    private void styleCreateButton(JButton button) {
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
    
    private void showCreatePlaylistDialog() {
        String playlistName = JOptionPane.showInputDialog(this,
            "Çalma listesi adını girin:",
            "Yeni Çalma Listesi",
            JOptionPane.PLAIN_MESSAGE);
        
        if (playlistName != null && !playlistName.trim().isEmpty()) {
            // Kullanıcının playlist limitini kontrol et
            List<Playlist> userPlaylists = PlaylistManager.getInstance().getUserPlaylists(currentUser.getEmail());
            if (userPlaylists.size() >= currentUser.getMaxPlaylistCount()) {
                JOptionPane.showMessageDialog(this,
                    "Maksimum çalma listesi sayısına ulaştınız!",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Yeni playlist oluştur
            Playlist newPlaylist = new Playlist(playlistName, currentUser);
            PlaylistManager.getInstance().addPlaylist(newPlaylist);
            
            // Arayüzü güncelle
            updatePlaylistPanel();
        }
    }
    
    private void addSamplePlaylists() {
        // Örnek çalma listeleri
        playlists.add(new Playlist("Favoriler", currentUser));
        playlists.add(new Playlist("Yaz Şarkıları", currentUser));
        if (currentUser instanceof PremiumUser) {
            playlists.add(new Playlist("Rock", currentUser));
            playlists.add(new Playlist("Pop", currentUser));
            playlists.add(new Playlist("Klasik", currentUser));
        }
    }
    
    private void updatePlaylistPanel() {
        playlistPanel.removeAll();
        
        // Kullanıcının playlistlerini getir
        List<Playlist> playlists = PlaylistManager.getInstance().getUserPlaylists(currentUser.getEmail());
        
        // Her playlist için panel oluştur
        for (Playlist playlist : playlists) {
            JPanel playlistItem = createPlaylistItem(playlist);
            playlistPanel.add(playlistItem);
            playlistPanel.add(Box.createVerticalStrut(10));
        }
        
        // Paneli yenile
        playlistPanel.revalidate();
        playlistPanel.repaint();
    }
    
    private JPanel createPlaylistItem(Playlist playlist) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout(10, 0));
        item.setBackground(new Color(40, 40, 40, 150));
        item.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        item.setMaximumSize(new Dimension(300, 70));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR)); // Tıklanabilir görünüm
        
        // Çalma listesi ikonu
        JLabel iconLabel = new JLabel("🎵");
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        
        // Çalma listesi bilgileri
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(playlist.getName());
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel countLabel = new JLabel(playlist.getMediaCount() + " şarkı");
        countLabel.setForeground(Color.GRAY);
        countLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(countLabel);
        
        // Silme butonu
        JButton deleteButton = new JButton("×");
        deleteButton.setFont(new Font("Arial", Font.BOLD, 18));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setBackground(null);
        deleteButton.setBorder(null);
        deleteButton.setFocusPainted(false);
        deleteButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        deleteButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                "Bu çalma listesini silmek istediğinize emin misiniz?",
                "Çalma Listesini Sil",
                JOptionPane.YES_NO_OPTION);
                
            if (result == JOptionPane.YES_OPTION) {
                PlaylistManager.getInstance().removePlaylist(playlist);
                updatePlaylistPanel();
            }
        });
        
        // Ana panel için tıklama olayı
        MouseAdapter clickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Eğer tıklanan bileşen silme butonu değilse detay ekranına git
                Component source = (Component) e.getSource();
                if (!source.equals(deleteButton)) {
                    PlaylistDetailScreen detailScreen = new PlaylistDetailScreen(currentUser, playlist);
                    detailScreen.setVisible(true);
                    PlaylistScreen.this.dispose();
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                // Eğer hover olan bileşen silme butonu değilse rengi değiştir
                Component source = (Component) e.getSource();
                if (!source.equals(deleteButton)) {
                    item.setBackground(new Color(178, 34, 34, 150));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Eğer hover biten bileşen silme butonu değilse rengi değiştir
                Component source = (Component) e.getSource();
                if (!source.equals(deleteButton)) {
                    item.setBackground(new Color(40, 40, 40, 150));
                }
            }
        };
        
        // Tüm bileşenlere tıklama olayı ekle
        item.addMouseListener(clickAdapter);
        iconLabel.addMouseListener(clickAdapter);
        infoPanel.addMouseListener(clickAdapter);
        nameLabel.addMouseListener(clickAdapter);
        countLabel.addMouseListener(clickAdapter);
        
        item.add(iconLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(deleteButton, BorderLayout.EAST);
        
        return item;
    }
} 