package test;

import librerias.analex.Analex;
import librerias.analex.Token;

/**
 * Prueba del analizador léxico
 */
public class TestAnalex {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA DEL ANALIZADOR LÉXICO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String[] comandos = {
            "usuario get",
            "help",
            "producto get",
            "categoria get"
        };
        
        for (String comando : comandos) {
            System.out.println("\n🔍 Analizando: '" + comando + "'");
            
            try {
                Analex analex = new Analex(comando);
                Token token;
                
                while ((token = analex.Preanalisis()).getName() != Token.END && token.getName() != Token.ERROR) {
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
                e.printStackTrace();
            }
        }
        
        System.out.println("\n🎉 Prueba completada");
    }
} 