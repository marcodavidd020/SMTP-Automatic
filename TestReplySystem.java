/**
 * Test específico para la funcionalidad de respuestas como REPLY
 * Simula emails con Message-ID para verificar el threading
 */
public class TestReplySystem {

    public static void main(String[] args) {
        System.out.println("🧪 TEST SISTEMA DE RESPUESTAS COMO REPLY");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Cargar la clase EmailAppIndependiente
            Class<?> emailAppClass = Class.forName("com.mycompany.parcial1.tecnoweb.EmailAppIndependiente");
            Object emailApp = emailAppClass.getDeclaredConstructor().newInstance();

            System.out.println("✅ EmailAppIndependiente cargado correctamente");

            // Datos de prueba para simular emails con Message-ID
            String emailRegistrado = "JairoJairoJairo@gmail.com";
            String originalSubject = "Test de comandos en secuencia";
            String messageId = "<test-message-123@gmail.com>";

            // Obtener método sobrecargado que acepta originalSubject y messageId
            java.lang.reflect.Method processMethod = emailAppClass.getMethod("processEmailCommand",
                    String.class, String.class, String.class, String.class, String.class);

            System.out.println("\n🧵 SIMULANDO CONVERSACIÓN CON THREADING:");
            System.out.println("   📧 Email original: " + originalSubject);
            System.out.println("   🆔 Message-ID: " + messageId);

            // Test 1: Comando inicial
            System.out.println("\n1. 📧 Email inicial: 'usuario get'");
            processMethod.invoke(emailApp, emailRegistrado, "usuario get", "", originalSubject, messageId);

            Thread.sleep(2000);

            // Test 2: Respuesta 1 en la misma conversación
            System.out.println("\n2. 💬 Reply 1: 'producto get'");
            processMethod.invoke(emailApp, emailRegistrado, "producto get", "", originalSubject, messageId);

            Thread.sleep(2000);

            // Test 3: Respuesta 2 en la misma conversación
            System.out.println("\n3. 💬 Reply 2: 'categorias get'");
            processMethod.invoke(emailApp, emailRegistrado, "categorias get", "", originalSubject, messageId);

            Thread.sleep(2000);

            // Test 4: Help en la conversación
            System.out.println("\n4. 💬 Reply 3: 'help'");
            processMethod.invoke(emailApp, emailRegistrado, "help", "", originalSubject, messageId);

            System.out.println("\n✅ PRUEBAS DE THREADING COMPLETADAS");
            System.out.println("\n📧 Resultados esperados:");
            System.out.println("   • ✅ Todas las respuestas deben aparecer como REPLY");
            System.out.println("   • ✅ Todas en la MISMA conversación");
            System.out.println("   • ✅ Subject debe empezar con 'Re:'");
            System.out.println("   • ✅ Headers In-Reply-To y References presentes");

            // Test 5: Comparación con método sin reply
            System.out.println("\n🔄 COMPARACIÓN CON MÉTODO SIN REPLY:");
            java.lang.reflect.Method processMethodOld = emailAppClass.getMethod("processEmailCommand",
                    String.class, String.class, String.class);

            System.out.println("\n5. 📧 Email sin threading: 'tipo_pago get'");
            processMethodOld.invoke(emailApp, emailRegistrado, "tipo_pago get", "");

            System.out.println("\n✅ COMPARACIÓN COMPLETADA");
            System.out.println("   • ⚠️ El último email debe crear conversación NUEVA");
            System.out.println("   • ✅ Los anteriores deben estar en MISMA conversación");

        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();

            System.out.println("\n🔧 POSIBLES SOLUCIONES:");
            System.out.println("   1. Verificar que EmailAppIndependiente esté compilado");
            System.out.println("   2. Asegurar que los métodos estén disponibles");
            System.out.println("   3. Verificar conexión a base de datos");
        }
    }
}