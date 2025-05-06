import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import model.*;  // Model paketindeki tüm sınıfları import et

// Giriş ekranı sınıfı - Kullanıcı girişi ve kayıt işlemlerini yönetir
public class LoginScreen extends JFrame {
    // Giriş formu için gerekli bileşenler
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    
    // Giriş ekranının yapıcı metodu - Pencere ayarları ve bileşenleri başlatır
    public LoginScreen() {
        // Temel pencere ayarları
        setTitle("StudiFy");
        setSize(360, 640); // Mobil uygulama boyutu
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Kar yağışlı arka plan paneli oluştur ve ayarla
        SnowPanel snowPanel = new SnowPanel();
        setContentPane(snowPanel); // Ana içerik paneli olarak ayarla
        snowPanel.setLayout(new BoxLayout(snowPanel, BoxLayout.Y_AXIS));
        snowPanel.setBorder(new EmptyBorder(40, 30, 40, 30));
        
        // Logo alanını oluştur ve ayarla
        JLabel logoLabel = new JLabel("🎄");  // Noel ağacı emojisi
        logoLabel.setFont(new Font("Arial", Font.BOLD, 72));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Başlık alanını oluştur ve ayarla
        JLabel titleLabel = new JLabel("StudiFy");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Giriş form alanlarını oluştur ve ayarla
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        styleTextField(usernameField, "E-posta");
        styleTextField(passwordField, "Şifre");
        
        // Giriş butonunu oluştur ve ayarla
        JButton loginButton = new JButton("Giriş Yap");
        styleButton(loginButton);
        loginButton.addActionListener(e -> handleLogin());
        
        // Kayıt ol linkini oluştur ve ayarla
        JLabel registerLink = new JLabel("Hesabın yok mu? Kayıt ol");
        registerLink.setForeground(new Color(255, 255, 255));
        registerLink.setFont(new Font("Arial", Font.BOLD, 14));
        registerLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Kayıt linkine tıklama olayını ekle
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Kayıt ekranını aç
                new RegisterScreen().setVisible(true);
            }
        });
        
        // Tüm bileşenleri kar paneline ekle ve aralarına boşluk bırak
        snowPanel.add(Box.createVerticalStrut(50));
        snowPanel.add(logoLabel);
        snowPanel.add(Box.createVerticalStrut(20));
        snowPanel.add(titleLabel);
        snowPanel.add(Box.createVerticalStrut(40));
        snowPanel.add(usernameField);
        snowPanel.add(Box.createVerticalStrut(15));
        snowPanel.add(passwordField);
        snowPanel.add(Box.createVerticalStrut(25));
        snowPanel.add(loginButton);
        snowPanel.add(Box.createVerticalStrut(20));
        snowPanel.add(registerLink);
    }
    
    // Giriş işlemini gerçekleştirir - Kullanıcı bilgilerini kontrol eder ve ana ekrana yönlendirir
    private void handleLogin() {
        // Form alanlarından değerleri al
        String email = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Boş alan kontrolü yap
        if (email.equals("E-posta") || password.equals("Şifre") || 
            email.trim().isEmpty() || password.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Lütfen e-posta ve şifre alanlarını doldurun.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // E-posta formatını kontrol et
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Lütfen geçerli bir e-posta adresi girin.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Şifre uzunluğunu kontrol et
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Şifre en az 6 karakter olmalıdır.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kullanıcı bilgilerini doğrula
        User user = UserManager.getInstance().login(email, password);
        
        // Giriş başarılıysa ana ekrana yönlendir, değilse hata mesajı göster
        if (user != null) {
            MainScreen mainScreen = new MainScreen(user);
            mainScreen.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "E-posta veya şifre hatalı.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Metin alanlarını stillendirir - Tutarlı görünüm için ortak stil uygular
    private void styleTextField(JTextField field, String placeholder) {
        // Metin alanının boyut ve görünüm ayarları
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
        
        // Placeholder metin ayarları
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        // Placeholder davranışı için focus dinleyicileri
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Alan seçildiğinde placeholder'ı temizle
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                // Alan boşsa placeholder'ı geri getir
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    // Butonları stillendirir - Tutarlı görünüm için ortak stil uygular
    private void styleButton(JButton button) {
        // Butonun boyut ayarları
        button.setMaximumSize(new Dimension(300, 40));
        
        // Yılbaşı teması için kırmızı tonları tanımla
        Color startColor = new Color(178, 34, 34);  // Koyu kırmızı
        Color endColor = new Color(220, 20, 60);    // Parlak kırmızı
        
        // Butonun temel görünüm ayarları
        button.setBackground(startColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Hover efekti için mouse dinleyicisi
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Mouse üzerine geldiğinde rengi değiştir
                button.setBackground(endColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                // Mouse ayrıldığında orijinal renge dön
                button.setBackground(startColor);
            }
        });
        
        // Özel buton görünümü için UI'ı özelleştir
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                // Grafik ayarları
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient arka plan oluştur
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, button.getHeight(), endColor
                );
                
                // Yuvarlak köşeli gradient arka planı çiz
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);
                
                // Buton metnini ortala ve çiz
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle textRect = new Rectangle(0, 0, button.getWidth(), button.getHeight());
                String text = button.getText();
                
                g2d.setColor(Color.WHITE);
                g2d.setFont(button.getFont());
                int x = (button.getWidth() - fm.stringWidth(text)) / 2;
                int y = (button.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2d.drawString(text, x, y);
            }
        });
    }
    
    // Uygulamayı başlat
    public static void main(String[] args) {
        // Swing bileşenlerini EDT (Event Dispatch Thread) üzerinde çalıştır
        SwingUtilities.invokeLater(() -> {
            try {
                // Sistem görünümünü kullan
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Giriş ekranını oluştur ve göster
            new LoginScreen().setVisible(true);
        });
    }
} 