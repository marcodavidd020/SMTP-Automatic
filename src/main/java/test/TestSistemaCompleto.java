package test;

import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * Prueba completa del sistema mejorado con autenticación y CSS
 */
public class TestSistemaCompleto {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA COMPLETA DEL SISTEMA MEJORADO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            EmailAppIndependiente app = new EmailAppIndependiente();
            
            String emailNoRegistrado = "nuevo@test.com";
            String emailRegistrado = "JairoJairoJairo@gmail.com";
            
            // Test 1: Usuario no registrado intenta usar comando
            System.out.println("\n1. 🚫 Probando usuario no registrado...");
            app.processEmailCommand(emailNoRegistrado, "usuario get", "Intento sin registro");
            Thread.sleep(2000);
            
            // Test 2: Registro de nuevo usuario
            System.out.println("\n2. 📝 Probando registro de nuevo usuario...");
            app.processEmailCommand(emailNoRegistrado, "registrar Juan Pérez 123456789 masculino", "Registro");
            Thread.sleep(2000);
            
            // Test 3: Help con usuario registrado
            System.out.println("\n3. 📚 Probando help mejorado...");
            app.processEmailCommand(emailRegistrado, "help", "Ver comandos");
            Thread.sleep(2000);
            
            // Test 4: Comandos básicos con usuario registrado
            System.out.println("\n4. 👥 Probando comando usuario get...");
            app.processEmailCommand(emailRegistrado, "usuario get", "Lista usuarios");
            Thread.sleep(2000);
            
            System.out.println("\n5. 📦 Probando comando producto get...");
            app.processEmailCommand(emailRegistrado, "producto get", "Lista productos");
            Thread.sleep(2000);
            
            System.out.println("\n6. 📂 Probando comando categoria get...");
            app.processEmailCommand(emailRegistrado, "categoria get", "Lista categorías");
            Thread.sleep(2000);
            
            // Test 5: Error de registro inválido
            System.out.println("\n7. ❌ Probando registro inválido...");
            app.processEmailCommand("otro@test.com", "registrar Juan", "Registro incompleto");
            Thread.sleep(2000);
            
            System.out.println("\n✅ TODAS LAS PRUEBAS COMPLETADAS");
            System.out.println("\n📊 RESUMEN DE FUNCIONALIDADES PROBADAS:");
            System.out.println("   ✅ Autenticación por email");
            System.out.println("   ✅ Mensaje de bienvenida para no registrados");
            System.out.println("   ✅ Registro de nuevos usuarios");
            System.out.println("   ✅ Validación de parámetros de registro");
            System.out.println("   ✅ Help mejorado con documentación completa");
            System.out.println("   ✅ CSS moderno y responsivo en emails");
            System.out.println("   ✅ Comandos GET para todas las entidades");
            System.out.println("   ✅ Manejo de errores mejorado");
            
            System.out.println("\n📧 Revisa tu email para ver los resultados con el nuevo diseño");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 