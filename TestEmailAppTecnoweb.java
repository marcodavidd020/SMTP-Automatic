import com.mycompany.parcial1.tecnoweb.EmailAppTecnoweb;

/**
 * Test específico para EmailApp Tecnoweb
 * Demuestra el envío via SMTP tecnoweb
 */
public class TestEmailAppTecnoweb {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST ESPECÍFICO: EmailApp Tecnoweb");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Crear instancia
        EmailAppTecnoweb app = new EmailAppTecnoweb();
        
        System.out.println("\n🔬 Ejecutando tests de comandos...");
        
        // Test 1: Comando help
        System.out.println("\n1️⃣ Test comando 'help':");
        app.processEmailCommand("marcodavidtoledo20@gmail.com", "help", "Solicito ayuda");
        
        try {
            Thread.sleep(3000); // Esperar envío
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Test 2: Comando usuario get
        System.out.println("\n2️⃣ Test comando 'usuario get':");
        app.processEmailCommand("marcodavidtoledo20@gmail.com", "usuario get", "Lista usuarios");
        
        try {
            Thread.sleep(3000); // Esperar envío
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Test 3: Usuario no registrado
        System.out.println("\n3️⃣ Test usuario no registrado:");
        app.processEmailCommand("nuevo@test.com", "help", "Soy nuevo");
        
        try {
            Thread.sleep(3000); // Esperar envío
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n✅ TESTS COMPLETADOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎯 RESULTADOS ESPERADOS:");
        System.out.println("   📧 Emails enviados via SMTP tecnoweb (mail.tecnoweb.org.bo:25)");
        System.out.println("   📬 Remitente: grupo21sc@tecnoweb.org.bo");
        System.out.println("   🗄️ Base de datos: db_grupo21sc en mail.tecnoweb.org.bo");
        System.out.println("   ✅ Diferentes respuestas según el comando y usuario");
    }
} 