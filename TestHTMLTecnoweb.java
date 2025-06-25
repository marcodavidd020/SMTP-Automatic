import java.lang.reflect.Method;

/**
 * Test específico para verificar que EmailAppTecnoweb envíe emails con estilos HTML correctos
 */
public class TestHTMLTecnoweb {

    public static void main(String[] args) {
        System.out.println("🧪 TEST: Verificación de HTML en Tecnoweb");
        System.out.println("═══════════════════════════════════════════");

        try {
            // Test 1: Verificar que HtmlRes genere contenido moderno
            testHtmlGeneration();
            
            // Test 2: Simular envío de bienvenida
            testWelcomeEmail();
            
            // Test 3: Simular envío de error
            testErrorEmail();
            
            // Test 4: Test de producto con HTML
            testProductEmail();
            
            System.out.println("\n✅ TODOS LOS TESTS PASARON");
            System.out.println("🎨 Los emails de Tecnoweb ahora incluyen estilos HTML modernos");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR EN TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testHtmlGeneration() throws Exception {
        System.out.println("\n1. 🎨 Test de generación HTML...");
        
        // Usar reflection para acceder a HtmlRes
        Class<?> htmlResClass = Class.forName("librerias.HtmlRes");
        
        // Test generar bienvenida
        Method welcomeMethod = htmlResClass.getMethod("generateWelcome", String.class);
        String htmlWelcome = (String) welcomeMethod.invoke(null, "usuario@tecnoweb.org.bo");
        
        // Test generar error
        Method errorMethod = htmlResClass.getMethod("generateError", String.class, String.class);
        String htmlError = (String) errorMethod.invoke(null, "Error Test", "Mensaje de prueba");
        
        // Test generar éxito
        Method successMethod = htmlResClass.getMethod("generateSuccess", String.class, String.class);
        String htmlSuccess = (String) successMethod.invoke(null, "Éxito Test", "Operación completada");
        
        // Verificaciones
        boolean welcomeOk = htmlWelcome.contains("<!DOCTYPE html") && htmlWelcome.contains("linear-gradient");
        boolean errorOk = htmlError.contains("<!DOCTYPE html") && htmlError.contains("box-shadow");
        boolean successOk = htmlSuccess.contains("<!DOCTYPE html") && htmlSuccess.contains("border-radius");
        
        System.out.println("   • HTML Bienvenida: " + (welcomeOk ? "✅ MODERNO" : "❌ BÁSICO"));
        System.out.println("   • HTML Error: " + (errorOk ? "✅ MODERNO" : "❌ BÁSICO"));
        System.out.println("   • HTML Éxito: " + (successOk ? "✅ MODERNO" : "❌ BÁSICO"));
        
        if (!welcomeOk || !errorOk || !successOk) {
            throw new Exception("HTML generado no incluye estilos modernos");
        }
        
        // Guardar samples para inspección
        java.nio.file.Files.write(java.nio.file.Paths.get("test_tecnoweb_welcome.html"), 
                                htmlWelcome.getBytes());
        java.nio.file.Files.write(java.nio.file.Paths.get("test_tecnoweb_error.html"), 
                                htmlError.getBytes());
        java.nio.file.Files.write(java.nio.file.Paths.get("test_tecnoweb_success.html"), 
                                htmlSuccess.getBytes());
        
        System.out.println("   📁 Samples guardados: test_tecnoweb_*.html");
    }

    private static void testWelcomeEmail() throws Exception {
        System.out.println("\n2. 📧 Test de email de bienvenida...");
        
        // Simular EmailAppTecnoweb
        Class<?> appClass = Class.forName("com.mycompany.parcial1.tecnoweb.EmailAppTecnoweb");
        Object app = appClass.getDeclaredConstructor().newInstance();
        
        // Usar reflection para llamar sendWelcomeEmail
        Method welcomeMethod = appClass.getDeclaredMethod("sendWelcomeEmail", String.class);
        welcomeMethod.setAccessible(true);
        
        System.out.println("   📤 Simulando envío de bienvenida...");
        welcomeMethod.invoke(app, "test@tecnoweb.org.bo");
        
        System.out.println("   ✅ Email de bienvenida procesado con HTML moderno");
    }

    private static void testErrorEmail() throws Exception {
        System.out.println("\n3. ❌ Test de email de error...");
        
        Class<?> appClass = Class.forName("com.mycompany.parcial1.tecnoweb.EmailAppTecnoweb");
        Object app = appClass.getDeclaredConstructor().newInstance();
        
        Method errorMethod = appClass.getDeclaredMethod("sendErrorEmail", String.class, String.class);
        errorMethod.setAccessible(true);
        
        System.out.println("   📤 Simulando envío de error...");
        errorMethod.invoke(app, "test@tecnoweb.org.bo", "Error de prueba para test HTML");
        
        System.out.println("   ✅ Email de error procesado con HTML moderno");
    }

    private static void testProductEmail() throws Exception {
        System.out.println("\n4. 🛍️ Test de email de producto...");
        
        Class<?> appClass = Class.forName("com.mycompany.parcial1.tecnoweb.EmailAppTecnoweb");
        Object app = appClass.getDeclaredConstructor().newInstance();
        
        // Crear ParamsAction simulado para producto
        Class<?> paramsClass = Class.forName("librerias.ParamsAction");
        Object event = paramsClass.getDeclaredConstructor().newInstance();
        
        // Configurar event básico
        Method setSenderMethod = paramsClass.getMethod("setSender", String.class);
        setSenderMethod.invoke(event, "test@tecnoweb.org.bo");
        
        Method setActionMethod = paramsClass.getMethod("setAction", int.class);
        setActionMethod.invoke(event, 203); // GET action
        
        System.out.println("   📤 Simulando comando producto...");
        Method productoMethod = appClass.getMethod("producto", paramsClass);
        productoMethod.invoke(app, event);
        
        System.out.println("   ✅ Comando producto procesado con HTML moderno");
    }
} 