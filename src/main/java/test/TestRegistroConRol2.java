package test;

import data.DUsuarioOptimizado;

/**
 * Test específico para registro con rol_id = 2 (cliente)
 */
public class TestRegistroConRol2 {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE REGISTRO CON ROL_ID = 2 (CLIENTE)");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            String emailTest = "marcodavidtoledo20@gmail.com";
            
            System.out.println("🔍 Verificando email: " + emailTest);
            
            // Test 1: Verificar si existe
            boolean existe = DUsuarioOptimizado.existsByEmail(emailTest);
            System.out.println("📧 Email existe: " + existe);
            
            if (!existe) {
                System.out.println("\n📝 Registrando usuario con ROL_ID = 2...");
                try {
                    var resultado = DUsuarioOptimizado.registerWithRoleId2(
                        "Marco", "Toledo", "67733399", "masculino", emailTest
                    );
                    System.out.println("✅ Usuario registrado: " + String.join(", ", resultado.get(0)));
                    
                    // Verificar que se registró
                    boolean ahoraExiste = DUsuarioOptimizado.existsByEmail(emailTest);
                    System.out.println("🔄 Verificación post-registro: " + (ahoraExiste ? "REGISTRADO" : "ERROR"));
                    
                } catch (Exception e) {
                    System.out.println("❌ Error registrando: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("\n📋 Obteniendo datos del usuario existente...");
                try {
                    var userData = DUsuarioOptimizado.getByEmail(emailTest);
                    System.out.println("✅ Usuario encontrado: " + String.join(", ", userData.get(0)));
                } catch (Exception e) {
                    System.out.println("❌ Error obteniendo datos: " + e.getMessage());
                }
            }
            
            System.out.println("\n✅ TEST COMPLETADO");
            System.out.println("\n📋 RESUMEN:");
            System.out.println("   🎯 El usuario debería estar registrado como CLIENTE (rol_id = 2)");
            System.out.println("   📧 Email: " + emailTest);
            System.out.println("   🗃️ Base de datos: LOCAL (EcommerceTool)");
            
            // Cleanup
            DUsuarioOptimizado.cleanup();
            
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 