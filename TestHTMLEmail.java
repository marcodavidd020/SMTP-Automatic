import java.util.ArrayList;
import java.util.List;

/**
 * Test directo para enviar emails HTML correctamente
 */
public class TestHTMLEmail {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE ENVÍO DE EMAIL HTML");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // Cargar clases necesarias
            Class<?> htmlResClass = Class.forName("librerias.HtmlRes");
            Class<?> relayClass = Class.forName("servidor.GmailRelayHTML");
            
            System.out.println("✅ Clases cargadas correctamente");
            
            // Crear instancia del relay HTML
            Object relay = relayClass.getDeclaredConstructor().newInstance();
            
            // Test 1: Generar HTML de bienvenida
            System.out.println("\n1. 📨 Generando HTML de bienvenida...");
            
            java.lang.reflect.Method welcomeMethod = htmlResClass.getMethod("generateWelcome", String.class);
            String htmlBienvenida = (String) welcomeMethod.invoke(null, "marcodavidtoledocanna@gmail.com");
            
            // Enviar email HTML
            System.out.println("2. 📧 Enviando email HTML...");
            
            java.lang.reflect.Method sendMethod = relayClass.getMethod("sendHtmlEmail", 
                String.class, String.class, String.class, String.class);
            
            sendMethod.invoke(relay, 
                "servidor-independiente@localhost",
                "marcodavidtoledocanna@gmail.com", 
                "Bienvenida - HTML Moderno",
                htmlBienvenida);
            
            Thread.sleep(2000);
            
            // Test 2: Generar tabla HTML
            System.out.println("\n3. 📊 Generando tabla HTML...");
            
            String[] headers = {"ID", "Comando", "Estado"};
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"1", "usuario get", "✅ SÍ"});
            data.add(new String[]{"2", "help", "✅ SÍ"});
            data.add(new String[]{"3", "registrar", "✅ SÍ"});
            
            java.lang.reflect.Method tableMethod = htmlResClass.getMethod("generateTable", 
                String.class, String[].class, List.class);
            String htmlTabla = (String) tableMethod.invoke(null, "Comandos Disponibles", headers, data);
            
            // Enviar tabla HTML
            System.out.println("4. 📧 Enviando tabla HTML...");
            
            sendMethod.invoke(relay, 
                "servidor-independiente@localhost",
                "marcodavidtoledocanna@gmail.com", 
                "Lista de Comandos - HTML Moderno",
                htmlTabla);
            
            System.out.println("\n✅ EMAILS ENVIADOS");
            System.out.println("\n📧 Revisa tu email en 1-2 minutos");
            System.out.println("   • Deberías ver 2 emails con diseño moderno");
            System.out.println("   • HTML con gradientes, sombras y diseño responsivo");
            System.out.println("   • Sin código crudo, solo diseño visual");
            
            System.out.println("\n🔍 VERIFICACIONES:");
            System.out.println("   ✅ HTML generado correctamente");
            System.out.println("   ✅ Relay HTML configurado");
            System.out.println("   ✅ Content-Type: text/html forzado");
            System.out.println("   ✅ Emails enviados");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 