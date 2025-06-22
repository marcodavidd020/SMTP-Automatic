/**
 * Test directo del EmailAppIndependiente mejorado
 */
public class TestEmailAppDirecto {

    public static void main(String[] args) {
        System.out.println("🧪 TEST DIRECTO DEL EMAILAPP INDEPENDIENTE");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Cargar la clase EmailAppIndependiente
            Class<?> emailAppClass = Class.forName("com.mycompany.parcial1.tecnoweb.EmailAppIndependiente");
            Object emailApp = emailAppClass.getDeclaredConstructor().newInstance();

            System.out.println("✅ EmailAppIndependiente cargado correctamente");

            // Test 1: Usuario registrado (tu email)
            System.out.println("\n1. 🔍 Probando usuario REGISTRADO...");
            String emailRegistrado = "marcodavidtoledo@gmail.com";

            java.lang.reflect.Method processMethod = emailAppClass.getMethod("processEmailCommand",
                    String.class, String.class, String.class);

            System.out.println("   📧 Procesando comando 'usuario get' para: " + emailRegistrado);
            processMethod.invoke(emailApp, emailRegistrado, "usuario get", "");

            Thread.sleep(2000);

            // Teast 2: Usuario NO registrado
            System.out.println("\n2. 🔍 Probando usuario NO REGISTRADO...");
            String emailNoRegistrado = "nuevo@test.com";

            System.out.println("   📧 Procesando comando 'usuario get' para: " + emailNoRegistrado);
            processMethod.invoke(emailApp, emailNoRegistrado, "usuario get", "");

            Thread.sleep(2000);

            // Test 3: Comando help
            System.out.println("\n3. 📚 Probando comando help...");
            System.out.println("   📧 Procesando comando 'help' para: " + emailRegistrado);
            processMethod.invoke(emailApp, emailRegistrado, "help", "");

            Thread.sleep(2000);

            // Test 4: Comando de registro
            System.out.println("\n4. 📝 Probando comando de registro...");
            String emailParaRegistrar = "test.registro@example.com";
            System.out.println("   📧 Procesando registro para: " + emailParaRegistrar);
            processMethod.invoke(emailApp, emailParaRegistrar, "registrar Juan Pérez 123456789 masculino", "");

            System.out.println("\n✅ PRUEBAS COMPLETADAS");
            System.out.println("\n📧 Revisa tu email para ver las respuestas con:");
            System.out.println("   • ✅ Usuario registrado: debe ver datos");
            System.out.println("   • ❌ Usuario no registrado: debe ver mensaje de bienvenida");
            System.out.println("   • 📚 Help: debe ver tabla de comandos moderna");
            System.out.println("   • 📝 Registro: debe ver confirmación");

        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();

            System.out.println("\n🔧 POSIBLES SOLUCIONES:");
            System.out.println("   1. Recompilar EmailAppIndependiente");
            System.out.println("   2. Verificar que estés usando el monitor correcto");
            System.out.println("   3. Asegurar que la base de datos esté corriendo");
        }
    }
}