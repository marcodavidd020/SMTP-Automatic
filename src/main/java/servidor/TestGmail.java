package servidor;

/**
 * Test simple para verificar que Gmail funciona
 */
public class TestGmail {
    
    public static void main(String[] args) {
        System.out.println("🧪 Probando envío de email via Gmail...");
        
        try {
            GmailRelay relay = new GmailRelay();
            
            String from = "JairoJairoJairo@gmail.com";
            String to = "JairoJairoJairoJairo@gmail.com";
            String subject = "Prueba directa de Gmail";
            String message = "Este es un mensaje de prueba enviado directamente desde el GmailRelay para verificar que las credenciales funcionan correctamente.";
            
            System.out.println("📤 Enviando email:");
            System.out.println("   From: " + from);
            System.out.println("   To: " + to);
            System.out.println("   Subject: " + subject);
            
            relay.sendEmail(from, to, subject, message);
            
            System.out.println("✅ Prueba completada! Revisa tu bandeja de entrada.");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 