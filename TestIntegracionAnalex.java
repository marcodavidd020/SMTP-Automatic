import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;
import librerias.Interpreter;
import librerias.analex.Analex;
import librerias.analex.Token;

/**
 * 🧪 TEST DE INTEGRACIÓN ANALEX
 * 
 * Prueba la integración completa del analizador léxico (analex) 
 * con las aplicaciones de email
 * 
 * @author MARCO
 */
public class TestIntegracionAnalex {

    public static void main(String[] args) {
        System.out.println("🧪 TEST DE INTEGRACIÓN ANALEX CON EMAIL APPS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Test 1: Probar el analizador léxico directamente
        testAnalexDirecto();
        
        // Test 2: Probar el interpreter con comandos
        testInterpreterConComandos();
        
        // Test 3: Probar integración completa con EmailApp
        testIntegracionCompleta();
        
        System.out.println("\n🎉 TODOS LOS TESTS COMPLETADOS");
    }

    /**
     * Test 1: Verificar que el analex funciona correctamente
     */
    private static void testAnalexDirecto() {
        System.out.println("\n🔍 TEST 1: ANALEX DIRECTO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] comandos = {
            "usuario get",
            "producto get",
            "categoria get",
            "cliente get",
            "tipo_pago get",
            "help"
        };
        
        for (String comando : comandos) {
            System.out.println("\n🔍 Analizando: '" + comando + "'");
            
            try {
                Analex analex = new Analex(comando);
                Token token;
                
                while ((token = analex.Preanalisis()).getName() != Token.END && 
                       token.getName() != Token.ERROR) {
                    
                    System.out.println("   Token: " + token.getStringToken(token.getName()) + 
                                     " | Atributo: " + token.getStringToken(token.getAttribute()));
                    analex.next();
                }
                
                if (token.getName() == Token.ERROR) {
                    System.out.println("   ❌ ERROR: " + token.getStringToken(token.getAttribute()) + 
                                     " | Lexema: " + analex.lexeme());
                } else {
                    System.out.println("   ✅ Análisis exitoso");
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ Excepción: " + e.getMessage());
            }
        }
    }

    /**
     * Test 2: Verificar que el Interpreter funciona
     */
    private static void testInterpreterConComandos() {
        System.out.println("\n🤖 TEST 2: INTERPRETER CON COMANDOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] comandos = {
            "usuario get",
            "producto get",
            "categoria get",
            "help"
        };
        
        // Crear EmailApp para usar como listener
        EmailAppIndependiente emailApp = new EmailAppIndependiente(false);
        
        for (String comando : comandos) {
            System.out.println("\n🔄 Procesando: '" + comando + "'");
            
            try {
                Interpreter interpreter = new Interpreter(comando, "test@ejemplo.com");
                interpreter.setCasoUsoListener(emailApp);
                
                System.out.println("   🚀 Ejecutando interpreter...");
                interpreter.run();
                System.out.println("   ✅ Comando procesado exitosamente");
                
            } catch (Exception e) {
                System.out.println("   ❌ Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Test 3: Verificar integración completa con EmailApp
     */
    private static void testIntegracionCompleta() {
        System.out.println("\n📧 TEST 3: INTEGRACIÓN COMPLETA CON EMAIL APP");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        EmailAppIndependiente emailApp = new EmailAppIndependiente(false);
        
        String[] comandos = {
            "usuario get",
            "producto get", 
            "categoria get",
            "cliente get",
            "tipo_pago get",
            "help"
        };
        
        for (String comando : comandos) {
            System.out.println("\n📩 Simulando email con comando: '" + comando + "'");
            
            try {
                // Simular procesamiento de email con comando
                emailApp.processEmailCommand("test@ejemplo.com", comando, "Solicitud de información");
                System.out.println("   ✅ Email procesado exitosamente");
                
            } catch (Exception e) {
                System.out.println("   ❌ Error procesando email: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 