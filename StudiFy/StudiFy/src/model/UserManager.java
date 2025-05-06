package model;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class UserManager {
    private static UserManager instance;
    private Map<String, User> users;
    private static final String USERS_FILE = "src/resources/users.txt";
    
    private UserManager() {
        users = new HashMap<>();
        loadUsers();
    }
    
    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }
    
    private void loadUsers() {
        try {
            // Dosya yoksa oluştur
            File file = new File(USERS_FILE);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                return;
            }
            
            // Dosyayı oku
            List<String> lines = Files.readAllLines(Paths.get(USERS_FILE));
            
            // Her satırı işle (format: username,email,password,isPremium)
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String username = parts[0];
                    String email = parts[1];
                    String password = parts[2];
                    boolean isPremium = Boolean.parseBoolean(parts[3]);
                    
                    User user;
                    if (isPremium) {
                        user = new PremiumUser(username, email, password, "monthly");
                    } else {
                        user = new FreeUser(username, email, password);
                    }
                    users.put(email, user);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Hata durumunda boş HashMap ile devam et
            users = new HashMap<>();
        }
    }
    
    private void saveUsers() {
        try {
            List<String> lines = new ArrayList<>();
            
            // Her kullanıcı için bir satır oluştur
            for (User user : users.values()) {
                String line = String.format("%s,%s,%s,%b",
                    user.getUsername(),
                    user.getEmail(),
                    user.getPassword(),
                    user instanceof PremiumUser);
                lines.add(line);
            }
            
            // Dosyaya yaz
            Files.write(Paths.get(USERS_FILE), lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public boolean register(String username, String email, String password, boolean isPremium) {
        // E-posta zaten kayıtlı mı kontrol et
        if (users.containsKey(email)) {
            return false;
        }
        
        // Yeni kullanıcı oluştur
        User newUser;
        if (isPremium) {
            newUser = new PremiumUser(username, email, password, "monthly");
        } else {
            newUser = new FreeUser(username, email, password);
        }
        
        // Kullanıcıyı kaydet
        users.put(email, newUser);
        saveUsers(); // Değişiklikleri dosyaya kaydet
        return true;
    }
    
    public User login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
    
    public void logout(User user) {
        // Çıkış işlemleri (gerekirse)
        saveUsers(); // Değişiklikleri kaydet
    }
    
    public boolean isEmailRegistered(String email) {
        return users.containsKey(email);
    }
    
    public int getUserCount() {
        return users.size();
    }
    
    public User getUserByEmail(String email) {
        return users.get(email);
    }
} 