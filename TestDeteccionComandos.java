import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * 🧪 TEST DETECCIÓN COMANDOS - Verificar que se detecten comandos del carrito
 */
public class TestDeteccionComandos {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DETECCIÓN COMANDOS DEL CARRITO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        EmailAppIndependiente app = new EmailAppIndependiente();
        String emailTest = "marcodavidtoledocanna@gmail.com";
        
        // Test comando exacto que falló
        System.out.println("\n📝 Test: carrito add 161 2");
        app.processEmailCommand(emailTest, "carrito add 161 2", "");
        
        System.out.println("\n📝 Test: carrito get");
        app.processEmailCommand(emailTest, "carrito get", "");
        
        System.out.println("\n📝 Test: checkout");
        app.processEmailCommand(emailTest, "checkout", "");
        
        System.out.println("\n✅ Si se procesan correctamente, el monitor funcionará");
    }
} 