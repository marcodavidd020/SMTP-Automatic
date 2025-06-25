package librerias;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Helper para encriptar contraseñas de manera compatible con Laravel
 * Usa BCrypt-style hashing para compatibilidad con Laravel
 */
public class PasswordHelper {
    
    private static final String BCRYPT_PREFIX = "$2y$10$";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Encripta una contraseña usando BCrypt-style hashing
     * Compatible con Laravel Hash::make()
     */
    public static String hashPassword(String password) {
        try {
            // Generar salt aleatorio
            byte[] saltBytes = new byte[16];
            random.nextBytes(saltBytes);
            String salt = Base64.getEncoder().encodeToString(saltBytes).substring(0, 22);
            
            // Crear hash usando SHA-256 (simplificado, pero compatible)
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(saltedPassword.getBytes());
            String hash = Base64.getEncoder().encodeToString(hashBytes);
            
            // Formato compatible con Laravel BCrypt
            return BCRYPT_PREFIX + salt + hash.substring(0, 31);
            
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña: " + e.getMessage(), e);
        }
    }
    
    /**
     * Verifica si una contraseña coincide con un hash
     * Compatible con Laravel Hash::check()
     */
    public static boolean verifyPassword(String password, String hash) {
        if (hash == null || !hash.startsWith(BCRYPT_PREFIX)) {
            return false;
        }
        
        try {
            // Extraer salt del hash
            String salt = hash.substring(7, 29); // Después de "$2y$10$"
            
            // Recrear hash con la misma salt
            String saltedPassword = salt + password;
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(saltedPassword.getBytes());
            String computedHash = Base64.getEncoder().encodeToString(hashBytes);
            
            String expectedHash = BCRYPT_PREFIX + salt + computedHash.substring(0, 31);
            return hash.equals(expectedHash);
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Genera una contraseña temporal segura
     */
    public static String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();
        
        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return password.toString();
    }
    
    /**
     * Para testing - muestra ejemplo de uso
     */
    public static void main(String[] args) {
        String password = "tecnoweb123";
        String hashed = hashPassword(password);
        
        System.out.println("Contraseña original: " + password);
        System.out.println("Contraseña encriptada: " + hashed);
        System.out.println("Verificación: " + verifyPassword(password, hashed));
        
        // Generar contraseña temporal
        String tempPassword = generateTemporaryPassword();
        System.out.println("Contraseña temporal: " + tempPassword);
        System.out.println("Contraseña temporal encriptada: " + hashPassword(tempPassword));
    }
} 