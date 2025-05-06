import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import model.*;
import java.util.List;
import java.util.ArrayList;

public class LikedSongsScreen extends JFrame {
    private User currentUser;
    private List<Song> likedSongs;
    private JPanel songsPanel;
    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel nowPlayingLabel;
    private JButton prevButton;
    private JButton playPauseButton;
    private JButton nextButton;
    
    public LikedSongsScreen(User user) {
        this.currentUser = user;
        this.likedSongs = new ArrayList<>();
        
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - Beğendiğim Şarkılar");
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
        
        // Örnek şarkıları ekle
        addSampleSongs();
        updateSongsList();
    }
    
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
            } else if (item.equals("Çalma Listeleri")) {
                menuButton.addActionListener(e -> {
                    PlaylistScreen playlistScreen = new PlaylistScreen(currentUser);
                    playlistScreen.setVisible(true);
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
    
    private void createContentPanel() {
        contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Üst başlık alanı
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Beğendiğim Şarkılar");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel countLabel = new JLabel(likedSongs.size() + " şarkı");
        countLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        countLabel.setForeground(Color.GRAY);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.add(titleLabel);
        titlePanel.add(countLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Şarkılar paneli
        songsPanel = new JPanel();
        songsPanel.setLayout(new BoxLayout(songsPanel, BoxLayout.Y_AXIS));
        songsPanel.setOpaque(false);
        
        JScrollPane scrollPane = new JScrollPane(songsPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
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
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(210, 40));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(text.equals("Beğendiklerim") ? 
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
                button.setBackground(text.equals("Beğendiklerim") ? 
                    new Color(178, 34, 34, 200) : new Color(40, 40, 40, 200));
            }
        });
        
        return button;
    }
    
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
    
    private void addSampleSongs() {
        // Örnek beğenilen şarkılar
        Song[] sampleSongs = {
            new Song("Seni Dert Etmeler", "Madrigal", "Neogazino"),
            new Song("Rüyalarda Buruşmuşum", "Adamlar", "Rüyalarda Buruşmuşum"),
            new Song("Sarılırım Birine", "Model", "Diğer Masallar"),
            new Song("Gel", "Model", "Diğer Masallar"),
            new Song("Değmesin Ellerimiz", "Sezen Aksu", "Gülümse"),
            new Song("Firuze", "Sezen Aksu", "Gülümse"),
            new Song("Yalnızlık Senfonisi", "Manga", "Şehr-i Hüzün"),
            new Song("Dursun Zaman", "Manga", "Şehr-i Hüzün")
        };
        
        // Şarkıları listeye ekle
        for (Song song : sampleSongs) {
            likedSongs.add(song);
        }
    }
    
    private void updateSongsList() {
        songsPanel.removeAll();
        
        for (int i = 0; i < likedSongs.size(); i++) {
            if (likedSongs.get(i) instanceof Song) {
                Song song = (Song) likedSongs.get(i);
                JPanel item = createSongItem(song, i + 1);
                songsPanel.add(item);
                songsPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        songsPanel.revalidate();
        songsPanel.repaint();
    }
    
    private JPanel createSongItem(Song song, int index) {
        JPanel item = new JPanel();
        item.setLayout(new BorderLayout(10, 0));
        item.setBackground(new Color(40, 40, 40, 150));
        item.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        item.setMaximumSize(new Dimension(300, 60));
        
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
            likedSongs.remove(song);
            updateSongsList();
        });
        
        item.add(numberLabel, BorderLayout.WEST);
        item.add(infoPanel, BorderLayout.CENTER);
        item.add(removeButton, BorderLayout.EAST);
        
        // Hover efekti
        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(178, 34, 34, 150));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(new Color(40, 40, 40, 150));
            }
        });
        
        return item;
    }
    
    private void removeSongFromLiked(Song song) {
        likedSongs.remove(song);
        updateSongsList();
    }
    
    
} 