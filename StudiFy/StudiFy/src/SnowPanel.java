import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SnowPanel extends JPanel {
    private ArrayList<Snowflake> snowflakes;
    private Timer timer;
    private Random random;
    private static final int DEFAULT_WIDTH = 360;
    
    public SnowPanel() {
        snowflakes = new ArrayList<>();
        random = new Random();
        setPreferredSize(new Dimension(DEFAULT_WIDTH, 640));
        setOpaque(true);
        
        // Başlangıçta kar tanelerini oluştur
        for (int i = 0; i < 150; i++) {
            snowflakes.add(new Snowflake());
        }
        
        // Timer'ı başlat
        timer = new Timer(30, e -> {
            moveSnow();
            repaint();
        });
        timer.start();
    }
    
    private void moveSnow() {
        if (getWidth() <= 0 || getHeight() <= 0) return;
        
        for (Snowflake snow : snowflakes) {
            snow.y += snow.speed;
            snow.x += Math.sin(snow.y / 30) * 2;
            
            // Ekranın altına ulaşan kar tanelerini yukarı taşı
            if (snow.y > getHeight()) {
                snow.y = -10;
                snow.x = random.nextInt(Math.max(1, getWidth()));
            }
            
            // Ekranın yanlarından çıkan kar tanelerini içeri al
            if (snow.x > getWidth()) {
                snow.x = 0;
            } else if (snow.x < 0) {
                snow.x = getWidth();
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Arkaplan gradyanı - Daha canlı kırmızı tonları
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(170, 10, 10),    // Daha parlak kırmızı
            0, getHeight(), new Color(100, 0, 0)  // Biraz daha açık koyu kırmızı
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Kar tanelerini çiz - Daha belirgin
        g2d.setColor(new Color(255, 255, 255, 180)); // Beyaz renk, yarı saydam
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (Snowflake snow : snowflakes) {
            float alpha = snow.opacity * 0.7f; // Opaklığı artırdım ama hala kontrollü
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            
            // Kar tanesi parlaklık efekti
            int glowSize = snow.size + 2;
            g2d.setColor(new Color(255, 255, 255, 40)); // Dış parlaklık
            g2d.fillOval((int)snow.x - 1, (int)snow.y - 1, glowSize, glowSize);
            
            g2d.setColor(new Color(255, 255, 255, 220)); // Ana kar tanesi
            g2d.fillOval((int)snow.x, (int)snow.y, snow.size, snow.size);
        }
        
        // Diğer bileşenlerin düzgün çizilmesi için
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
    
    private class Snowflake {
        double x, y;
        int size;
        float opacity;
        double speed;
        
        Snowflake() {
            reset();
            y = random.nextInt(Math.max(1, getHeight()));
        }
        
        void reset() {
            x = random.nextInt(Math.max(1, getWidth()));
            y = -10;
            size = random.nextInt(4) + 2;  // Boyutu biraz artırdım
            opacity = random.nextFloat() * 0.6f + 0.3f;  // Minimum opaklığı artırdım
            speed = random.nextDouble() * 2.5 + 1.5;  // Hızı biraz artırdım
        }
    }
} 