import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * 🛒 TEST DEBUG CARRITO - Para diagnosticar el problema de persistencia
 */
public class TestCarritoDebug {
    
    public static void main(String[] args) {
        System.out.println("🛒 TEST DEBUG CARRITO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        EmailAppIndependiente app = new EmailAppIndependiente();
        String emailTest = "marcodavidtoledocanna@gmail.com";  // Cliente ID 31
        
        try {
            // Test 1: Agregar producto
            System.out.println("\n🔸 PASO 1: Agregar producto 161 cantidad 2");
            app.processEmailCommand(emailTest, "carrito add 161 2", "Test agregar");
            
            Thread.sleep(1000); // Pausa pequeña
            
            // Test 2: Ver carrito inmediatamente
            System.out.println("\n🔸 PASO 2: Ver carrito inmediatamente después");
            app.processEmailCommand(emailTest, "carrito get", "Test ver carrito");
            
        } catch (Exception e) {
            System.err.println("Error en test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n✅ Test de debug completado");
    }
} 