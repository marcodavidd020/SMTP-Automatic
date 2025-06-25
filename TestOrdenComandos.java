/**
 * Test para verificar el orden correcto de comandos en el analex
 */
public class TestOrdenComandos {

    public static void main(String[] args) {
        System.out.println("🧪 TEST: Orden de Comandos en Analex");
        System.out.println("═══════════════════════════════════════");

        try {
            // Test 1: ACCIÓN + CASO_DE_USO (tu sugerencia)
            System.out.println("\n1. 🔍 Probando: ACCIÓN + CASO_DE_USO");
            testCommand("get usuario");
            testCommand("get producto"); 
            testCommand("get categoria");
            testCommand("get cliente");
            
            // Test 2: CASO_DE_USO + ACCIÓN (implementación actual)
            System.out.println("\n2. 🔍 Probando: CASO_DE_USO + ACCIÓN");
            testCommand("usuario get");
            testCommand("producto get");
            testCommand("categoria get");
            testCommand("cliente get");
            
            // Test 3: Solo CASO_DE_USO
            System.out.println("\n3. 🔍 Probando: Solo CASO_DE_USO");
            testCommand("help");
            testCommand("usuario");
            testCommand("producto");
            
            // Test 4: Usar Interpreter directamente con diferentes órdenes
            System.out.println("\n4. 🔍 Test con Interpreter:");
            testWithInterpreter("get usuario");
            testWithInterpreter("usuario get");
            testWithInterpreter("help");
            
        } catch (Exception e) {
            System.err.println("\n❌ ERROR EN TEST: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCommand(String command) {
        try {
            System.out.println("\n🔍 Comando: '" + command + "'");
            
            // Usar Analex para analizar el comando
            librerias.analex.Analex analex = new librerias.analex.Analex(command);
            librerias.analex.Token token = analex.Preanalisis();
            
            if (token != null) {
                System.out.println("   ✅ Primer token reconocido:");
                System.out.println("      Name: " + token.getName() + " (" + token.getStringToken(token.getName()) + ")");
                System.out.println("      Attribute: " + token.getAttribute() + " (" + token.getStringToken(token.getAttribute()) + ")");
                System.out.println("      Lexema: '" + analex.lexeme() + "'");
                
                // Intentar obtener siguiente token
                analex.next();
                librerias.analex.Token nextToken = analex.Preanalisis();
                if (nextToken != null && nextToken.getName() != librerias.analex.Token.END) {
                    System.out.println("   ✅ Segundo token:");
                    System.out.println("      Name: " + nextToken.getName() + " (" + nextToken.getStringToken(nextToken.getName()) + ")");
                    System.out.println("      Attribute: " + nextToken.getAttribute() + " (" + nextToken.getStringToken(nextToken.getAttribute()) + ")");
                    System.out.println("      Lexema: '" + analex.lexeme() + "'");
                }
            } else {
                System.out.println("   ❌ No se pudo reconocer el comando");
            }
            
        } catch (Exception e) {
            System.out.println("   ❌ Error: " + e.getMessage());
        }
    }
    
    private static void testWithInterpreter(String command) {
        try {
            System.out.println("\n🔧 Interpreter test: '" + command + "'");
            
            // Crear un listener mock para capturar las llamadas
            interfaces.ICasoUsoListener mockListener = new interfaces.ICasoUsoListener() {
                @Override
                public void usuario(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método usuario() llamado exitosamente");
                }
                @Override
                public void producto(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método producto() llamado exitosamente");
                }
                @Override
                public void categoria(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método categoria() llamado exitosamente");
                }
                @Override
                public void cliente(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método cliente() llamado exitosamente");
                }
                @Override
                public void help(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método help() llamado exitosamente");
                }
                @Override
                public void tipo_pago(librerias.ParamsAction event) {
                    System.out.println("   ✅ Método tipo_pago() llamado exitosamente");
                }
                
                // Métodos requeridos (implementación vacía)
                @Override public void pago(librerias.ParamsAction event) {}
                @Override public void proveedor(librerias.ParamsAction event) {}
                @Override public void patrocinador(librerias.ParamsAction event) {}
                @Override public void patrocinio(librerias.ParamsAction event) {}
                @Override public void rol(librerias.ParamsAction event) {}
                @Override public void servicio(librerias.ParamsAction event) {}
                @Override public void error(librerias.ParamsAction event) {}
            };
            
            // Crear Interpreter y ejecutar
            librerias.Interpreter interpreter = new librerias.Interpreter(command, "test@email.com");
            interpreter.setCasoUsoListener(mockListener);
            interpreter.run();
            
        } catch (Exception e) {
            System.out.println("   ❌ Error en Interpreter: " + e.getMessage());
        }
    }
} 