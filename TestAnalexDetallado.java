import librerias.analex.Analex;
import librerias.analex.Token;

/**
 * 🧪 TEST DETALLADO DEL ANALEX
 * 
 * Identifica específicamente dónde está el problema con el análisis léxico
 * 
 * @author Jairo
 */
public class TestAnalexDetallado {

    public static void main(String[] args) {
        System.out.println("🧪 TEST DETALLADO DEL ANALEX");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Test de tokens individuales
        testTokensIndividuales();
        
        // Test de comandos completos
        testComandosCompletos();
        
        // Test de construcción de tokens
        testConstruccionTokens();
        
        System.out.println("\n🎉 TEST DETALLADO COMPLETADO");
    }

    /**
     * Test de tokens individuales
     */
    private static void testTokensIndividuales() {
        System.out.println("\n🔍 TEST 1: TOKENS INDIVIDUALES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] tokens = {
            "usuario",
            "producto", 
            "categoria",
            "cliente",
            "tipo_pago",
            "help",
            "get",
            "add",
            "delete"
        };
        
        for (String tokenStr : tokens) {
            System.out.println("\n🔍 Analizando token: '" + tokenStr + "'");
            
            try {
                Analex analex = new Analex(tokenStr);
                Token token = analex.Preanalisis();
                
                System.out.println("   Token Name: " + token.getName() + " (" + token.getStringToken(token.getName()) + ")");
                System.out.println("   Token Attribute: " + token.getAttribute() + " (" + token.getStringToken(token.getAttribute()) + ")");
                System.out.println("   Lexema: '" + analex.lexeme() + "'");
                
                if (token.getName() == Token.ERROR) {
                    System.out.println("   ❌ ERROR DETECTADO");
                } else {
                    System.out.println("   ✅ Token analizado correctamente");
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ Excepción: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Test de comandos completos
     */
    private static void testComandosCompletos() {
        System.out.println("\n🔍 TEST 2: COMANDOS COMPLETOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] comandos = {
            "usuario get",
            "producto get",
            "help"
        };
        
        for (String comando : comandos) {
            System.out.println("\n🔍 Analizando comando: '" + comando + "'");
            
            try {
                Analex analex = new Analex(comando);
                Token token;
                int count = 0;
                
                while ((token = analex.Preanalisis()).getName() != Token.END && 
                       token.getName() != Token.ERROR && count < 10) {
                    
                    System.out.println("   Token " + (count + 1) + ":");
                    System.out.println("     Name: " + token.getName() + " (" + token.getStringToken(token.getName()) + ")");
                    System.out.println("     Attribute: " + token.getAttribute() + " (" + token.getStringToken(token.getAttribute()) + ")");
                    System.out.println("     Lexema: '" + analex.lexeme() + "'");
                    
                    analex.next();
                    count++;
                }
                
                if (token.getName() == Token.ERROR) {
                    System.out.println("   ❌ ERROR: " + token.getStringToken(token.getAttribute()) + 
                                     " | Lexema: '" + analex.lexeme() + "'");
                } else if (token.getName() == Token.END) {
                    System.out.println("   ✅ Comando analizado completamente");
                } else {
                    System.out.println("   ⚠️ Análisis detenido prematuramente");
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ Excepción: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Test de construcción de tokens
     */
    private static void testConstruccionTokens() {
        System.out.println("\n🔍 TEST 3: CONSTRUCCIÓN DE TOKENS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] tokens = {
            "usuario",
            "producto",
            "get",
            "help"
        };
        
        for (String tokenStr : tokens) {
            System.out.println("\n🔍 Construyendo token: '" + tokenStr + "'");
            
            try {
                Token token = new Token(tokenStr);
                
                System.out.println("   Token Name: " + token.getName() + " (" + token.getStringToken(token.getName()) + ")");
                System.out.println("   Token Attribute: " + token.getAttribute() + " (" + token.getStringToken(token.getAttribute()) + ")");
                
                if (token.getName() == Token.ERROR) {
                    System.out.println("   ❌ Token no reconocido");
                } else {
                    System.out.println("   ✅ Token construido correctamente");
                }
                
            } catch (Exception e) {
                System.out.println("   ❌ Excepción: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 