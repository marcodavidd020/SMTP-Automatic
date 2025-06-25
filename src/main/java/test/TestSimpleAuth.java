package test;

import data.DUsuario;

/**
 * Test simple de autenticación
 */
public class TestSimpleAuth {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST SIMPLE DE AUTENTICACIÓN");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            String emailTest = "marcodavidtoledo@gmail.com";
            
            // Usar constructor explícito con tecnoweb
            DUsuario dUsuario = new DUsuario(
                "db_grupo21sc",
                "mail.tecnoweb.org.bo", 
                "5432",
                "grupo21sc",
                "grup021grup021*"
            );
            
            System.out.println("🔍 Verificando email: " + emailTest);
            
            // Test 1: Verificar si existe
            boolean existe = dUsuario.existsByEmail(emailTest);
            System.out.println("📧 Email existe: " + existe);
            
            if (!existe) {
                System.out.println("\n📝 Registrando usuario...");
                try {
                    var resultado = dUsuario.register("Marco David", "Toledo", "12345678", "masculino", emailTest);
                    System.out.println("✅ Usuario registrado: " + String.join(", ", resultado.get(0)));
                } catch (Exception e) {
                    System.out.println("❌ Error registrando: " + e.getMessage());
                }
            } else {
                System.out.println("\n📋 Obteniendo datos del usuario...");
                try {
                    var userData = dUsuario.getByEmail(emailTest);
                    System.out.println("✅ Usuario encontrado: " + String.join(", ", userData.get(0)));
                } catch (Exception e) {
                    System.out.println("❌ Error obteniendo datos: " + e.getMessage());
                }
            }
            
            System.out.println("\n✅ TEST COMPLETADO");
            
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 