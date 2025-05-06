/**
 * Kar Yağışı Efektli Panel
 * Arka planda animasyonlu kar taneleri efekti oluşturur
 * Uygulama ekranlarına dinamik ve estetik bir görünüm sağlar
 */

package view;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class SnowPanel extends JPanel {
    // Kar efekti bileşenleri
    private ArrayList<Snow> snowList = new ArrayList<>();  // Kar tanelerinin listesi
    private Random random = new Random();                  // Rastgele değerler için
    private Timer timer;                                   // Animasyon zamanlayıcısı
    
    /**
     * Kar Yağışı Paneli yapıcı metodu
     * Arka plan rengini ayarlar ve kar tanelerini oluşturur
     * Animasyon timer'ını başlatır
     */
    public SnowPanel() {
        setBackground(new Color(18, 18, 18)); // Koyu arka plan
        
        // Kar tanelerini oluştur
        for (int i = 0; i < 100; i++) {
            snowList.add(new Snow(
                random.nextInt(1200),
                random.nextInt(800),
                random.nextInt(3) + 1
            ));
        }
        
        // Animasyon timer'ı
        timer = new Timer(50, e -> {
            moveSnow();
            repaint();
        });
        timer.start();
    }
    
    /**
     * Kar tanelerini hareket ettirir
     * Her kar tanesinin y koordinatını günceller
     * Ekranın altına ulaşan taneleri üste taşır
     */
    private void moveSnow() {
        for (Snow snow : snowList) {
            snow.y += snow.speed;
            if (snow.y > getHeight()) {
                snow.y = -5;
                snow.x = random.nextInt(getWidth());
            }
        }
    }
    
    /**
     * Panel içeriğini çizer
     * @param g Grafik nesnesi
     * Kar tanelerini yarı saydam beyaz renkte çizer
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Kar tanelerini çiz
        g2d.setColor(new Color(255, 255, 255, 100));
        for (Snow snow : snowList) {
            g2d.fillOval(snow.x, snow.y, 4, 4);
        }
    }
    
    /**
     * Kar tanesi sınıfı
     * Kar tanesinin konumunu ve hızını tutar
     */
    private class Snow {
        int x, y, speed;  // x,y koordinatları ve düşme hızı
        
        /**
         * Kar tanesi yapıcı metodu
         * @param x Yatay konum
         * @param y Dikey konum
         * @param speed Düşme hızı
         */
        Snow(int x, int y, int speed) {
            this.x = x;
            this.y = y;
            this.speed = speed;
        }
    }
} 