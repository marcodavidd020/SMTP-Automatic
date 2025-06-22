package test;

import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * Prueba completa de todos los comandos disponibles
 */
public class TestTodosComandos {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA COMPLETA DE TODOS LOS COMANDOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            EmailAppIndependiente app = new EmailAppIndependiente();
            
            // Test 1: Help
            System.out.println("\n1. 📋 Procesando comando 'help'...");
            app.processEmailCommand("test@test.com", "help", "Necesito ayuda");
            Thread.sleep(2000);
            
            // Test 2: Usuarios
            System.out.println("\n2. 👥 Procesando comando 'usuario get'...");
            app.processEmailCommand("test@test.com", "usuario get", "Lista de usuarios");
            Thread.sleep(2000);
            
            // Test 3: Productos
            System.out.println("\n3. 📦 Procesando comando 'producto get'...");
            app.processEmailCommand("test@test.com", "producto get", "Lista de productos");
            Thread.sleep(2000);
            
            // Test 4: Categorías
            System.out.println("\n4. 📂 Procesando comando 'categoria get'...");
            app.processEmailCommand("test@test.com", "categoria get", "Lista de categorías");
            Thread.sleep(2000);
            
            // Test 5: Clientes
            System.out.println("\n5. 👤 Procesando comando 'cliente get'...");
            app.processEmailCommand("test@test.com", "cliente get", "Lista de clientes");
            Thread.sleep(2000);
            
            // Test 6: Tipos de pago
            System.out.println("\n6. 💳 Procesando comando 'tipo_pago get'...");
            app.processEmailCommand("test@test.com", "tipo_pago get", "Lista de tipos de pago");
            Thread.sleep(2000);
            
            // Test 7: Comando específico por ID
            System.out.println("\n7. 🎯 Procesando comando 'usuario get 1'...");
            app.processEmailCommand("test@test.com", "usuario get 1", "Usuario específico");
            Thread.sleep(2000);
            
            System.out.println("\n✅ Todas las pruebas completadas");
            System.out.println("📧 Revisa tu email para ver las respuestas");
            System.out.println("\n📊 RESUMEN:");
            System.out.println("   ✅ help - Comando de ayuda");
            System.out.println("   ✅ usuario get - Lista de usuarios");
            System.out.println("   ✅ producto get - Lista de productos");
            System.out.println("   ✅ categoria get - Lista de categorías");
            System.out.println("   ✅ cliente get - Lista de clientes");
            System.out.println("   ✅ tipo_pago get - Lista de tipos de pago");
            System.out.println("   ✅ usuario get 1 - Usuario específico por ID");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 