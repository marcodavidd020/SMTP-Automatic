package test;

import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * Prueba del comando 'usuario get' para verificar que funciona
 */
public class TestComandoUsuario {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA DE COMANDO 'usuario get'");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            EmailAppIndependiente app = new EmailAppIndependiente();
            
            System.out.println("\n1. 📧 Procesando comando 'usuario get'...");
            app.processEmailCommand("test@test.com", "usuario get", "Solicito lista de usuarios");
            
            Thread.sleep(3000); // Esperar a que se procese
            
            System.out.println("\n2. 📧 Procesando comando 'help'...");
            app.processEmailCommand("test@test.com", "help", "Necesito ayuda");
            
            Thread.sleep(3000); // Esperar a que se procese
            
            System.out.println("\n3. 📦 Procesando comando 'producto get'...");
            app.processEmailCommand("test@test.com", "producto get", "Solicito lista de productos");
            
            Thread.sleep(3000); // Esperar a que se procese
            
            System.out.println("\n✅ Pruebas completadas");
            System.out.println("📧 Revisa tu email para ver las respuestas");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 