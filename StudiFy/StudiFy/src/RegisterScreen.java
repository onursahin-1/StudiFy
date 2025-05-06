/**
 * Kayıt Ekranı
 * Yeni kullanıcıların sisteme kaydolmasını sağlar
 * Kullanıcı bilgileri ve üyelik tipi seçimi sunar
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import model.*;

public class RegisterScreen extends JFrame {
    // Form alanları
    private JTextField usernameField;           // Kullanıcı adı giriş alanı
    private JTextField emailField;              // E-posta giriş alanı
    private JPasswordField passwordField;       // Şifre giriş alanı
    private JPasswordField confirmPasswordField; // Şifre tekrar giriş alanı
    private ButtonGroup membershipGroup;        // Üyelik tipi seçim grubu
    
    /**
     * Kayıt Ekranı yapıcı metodu
     * Pencere boyutları, başlık ve form bileşenlerini ayarlar
     * Kar efektli arka plan ve kullanıcı arayüzü elemanlarını oluşturur
     */
    public RegisterScreen() {
        // Temel pencere ayarları
        setTitle("Müzik Uygulaması - Kayıt Ol");
        setSize(360, 640);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Ana pencereyi kapatmadan bu pencereyi kapatır
        setLocationRelativeTo(null);
        
        // Kar yağışlı arka plan paneli
        SnowPanel snowPanel = new SnowPanel();
        setContentPane(snowPanel);
        snowPanel.setLayout(new BoxLayout(snowPanel, BoxLayout.Y_AXIS));
        snowPanel.setBorder(new EmptyBorder(40, 30, 40, 30));
        
        // Logo alanı
        JLabel logoLabel = new JLabel("🎄");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 72));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Başlık
        JLabel titleLabel = new JLabel("Yeni Hesap Oluştur");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Form alanları
        usernameField = new JTextField(20);
        emailField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        
        styleTextField(usernameField, "Kullanıcı Adı");
        styleTextField(emailField, "E-posta");
        styleTextField(passwordField, "Şifre");
        styleTextField(confirmPasswordField, "Şifre Tekrar");
        
        // Üyelik tipi seçimi
        JPanel membershipPanel = new JPanel();
        membershipPanel.setOpaque(false);
        membershipPanel.setMaximumSize(new Dimension(300, 60));
        membershipPanel.setLayout(new BoxLayout(membershipPanel, BoxLayout.Y_AXIS));
        
        JLabel membershipLabel = new JLabel("Üyelik Tipi");
        membershipLabel.setForeground(Color.WHITE);
        membershipLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JPanel radioPanel = new JPanel();
        radioPanel.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        JRadioButton freeButton = new JRadioButton("Ücretsiz");
        JRadioButton premiumButton = new JRadioButton("Premium");
        
        freeButton.setActionCommand("free");
        premiumButton.setActionCommand("premium");
        
        styleMembershipButton(freeButton);
        styleMembershipButton(premiumButton);
        
        group.add(freeButton);
        group.add(premiumButton);
        radioPanel.add(freeButton);
        radioPanel.add(premiumButton);
        
        membershipGroup = group;
        
        membershipPanel.add(membershipLabel);
        membershipPanel.add(Box.createVerticalStrut(5));
        membershipPanel.add(radioPanel);
        
        // Kayıt ol butonu
        JButton registerButton = new JButton("Kayıt Ol");
        styleButton(registerButton);
        
        // Giriş yap linki
        JLabel loginLink = new JLabel("Zaten hesabın var mı? Giriş yap");
        loginLink.setForeground(Color.WHITE);
        loginLink.setFont(new Font("Arial", Font.BOLD, 14));
        loginLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Bileşenleri panele ekleme
        snowPanel.add(Box.createVerticalStrut(30));
        snowPanel.add(logoLabel);
        snowPanel.add(Box.createVerticalStrut(20));
        snowPanel.add(titleLabel);
        snowPanel.add(Box.createVerticalStrut(30));
        snowPanel.add(usernameField);
        snowPanel.add(Box.createVerticalStrut(15));
        snowPanel.add(emailField);
        snowPanel.add(Box.createVerticalStrut(15));
        snowPanel.add(passwordField);
        snowPanel.add(Box.createVerticalStrut(15));
        snowPanel.add(confirmPasswordField);
        snowPanel.add(Box.createVerticalStrut(20));
        snowPanel.add(membershipPanel);
        snowPanel.add(Box.createVerticalStrut(25));
        snowPanel.add(registerButton);
        snowPanel.add(Box.createVerticalStrut(15));
        snowPanel.add(loginLink);
        
        // Buton işlevleri
        registerButton.addActionListener(e -> handleRegistration());
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose(); // Bu pencereyi kapat
            }
        });
    }
    
    /**
     * Üyelik tipi seçim butonlarını stillendirir
     * @param button Stillendirilecek buton
     * Butonun görünümünü ve renklerini düzenler
     */
    private void styleMembershipButton(JRadioButton button) {
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setOpaque(false);
        button.setFocusPainted(false);
    }
    
    /**
     * Kayıt işlemini gerçekleştirir
     * Form alanlarının doğruluğunu kontrol eder
     * Kullanıcıyı sisteme kaydeder ve sonucu bildirir
     */
    private void handleRegistration() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Boş alan kontrolü
        if (username.equals("Kullanıcı Adı") || email.equals("E-posta") || 
            password.equals("Şifre") || confirmPassword.equals("Şifre Tekrar") ||
            username.trim().isEmpty() || email.trim().isEmpty() || 
            password.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(this,
                "Lütfen tüm alanları doldurun.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // E-posta formatı kontrolü
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                "Lütfen geçerli bir e-posta adresi girin.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Şifre uzunluğu kontrolü
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this,
                "Şifre en az 6 karakter olmalıdır.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Şifre eşleşme kontrolü
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                "Şifreler eşleşmiyor!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Üyelik tipi kontrolü
        ButtonModel selectedButton = membershipGroup.getSelection();
        if (selectedButton == null) {
            JOptionPane.showMessageDialog(this,
                "Lütfen üyelik tipini seçin.",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean isPremium = selectedButton.getActionCommand().equals("premium");
        
        // Kullanıcıyı kaydet
        if (UserManager.getInstance().register(username, email, password, isPremium)) {
            JOptionPane.showMessageDialog(this,
                "Kayıt başarılı! Giriş yapabilirsiniz.",
                "Başarılı",
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                "Bu e-posta adresi zaten kayıtlı!",
                "Hata",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Metin giriş alanlarını stillendirir
     * @param field Stillendirilecek alan
     * @param placeholder Varsayılan metin
     * Alanın görünümünü ve odaklanma davranışını düzenler
     */
    private void styleTextField(JTextField field, String placeholder) {
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
        
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.WHITE);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    /**
     * Kayıt butonunu stillendirir
     * @param button Stillendirilecek buton
     * Butonun görünümünü ve hover efektini düzenler
     */
    private void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(300, 40));
        
        Color startColor = new Color(178, 34, 34);
        Color endColor = new Color(220, 20, 60);
        
        button.setBackground(startColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(endColor);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(startColor);
            }
        });
        
        button.setUI(new BasicButtonUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, startColor,
                    0, button.getHeight(), endColor
                );
                
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, button.getWidth(), button.getHeight(), 20, 20);
                
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
} 