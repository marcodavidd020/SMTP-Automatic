package test;

import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * Prueba del comando 'categoria get'
 */
public class TestCategorias {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA DE COMANDO 'categoria get'");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            EmailAppIndependiente app = new EmailAppIndependiente();
            
            System.out.println("\n1. 📂 Procesando comando 'categoria get'...");
            app.processEmailCommand("test@test.com", "categoria get", "Solicito lista de categorías");
            
            Thread.sleep(3000); // Esperar a que se procese
            
            System.out.println("\n2. 📂 Procesando comando 'categoria get 1'...");
            app.processEmailCommand("test@test.com", "categoria get 1", "Solicito categoría ID 1");
            
            Thread.sleep(3000); // Esperar a que se procese
            
            System.out.println("\n✅ Pruebas completadas");
            System.out.println("📧 Revisa tu email para ver las respuestas");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 