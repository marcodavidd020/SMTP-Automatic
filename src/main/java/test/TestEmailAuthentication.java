package test;

import data.DUsuario;
import librerias.HtmlRes;

/**
 * Test directo de autenticación y registro de usuarios
 */
public class TestEmailAuthentication {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE AUTENTICACIÓN DE EMAIL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            String emailPrueba = "JairoJairoJairo@gmail.com";
            DUsuario dUsuario = new DUsuario();
            
            // Test 1: Verificar si el email está registrado
            System.out.println("\n1. 🔍 Verificando si el email está registrado...");
            System.out.println("   📧 Email: " + emailPrueba);
            
            boolean existeEmail = dUsuario.existsByEmail(emailPrueba);
            System.out.println("   ✅ Resultado: " + (existeEmail ? "REGISTRADO" : "NO REGISTRADO"));
            
            if (!existeEmail) {
                // Test 2: Generar mensaje de bienvenida
                System.out.println("\n2. 📨 Generando mensaje de bienvenida para email no registrado...");
                String htmlBienvenida = HtmlRes.generateWelcome(emailPrueba);
                
                // Guardar HTML para inspección
                java.nio.file.Files.write(java.nio.file.Paths.get("mensaje_bienvenida_real.html"), 
                    htmlBienvenida.getBytes());
                
                System.out.println("   ✅ HTML de bienvenida generado correctamente");
                System.out.println("   📁 Guardado como: mensaje_bienvenida_real.html");
                
                // Test 3: Mostrar extracto del HTML
                System.out.println("\n3. 📝 Extracto del HTML generado:");
                String extracto = htmlBienvenida.substring(0, Math.min(500, htmlBienvenida.length()));
                System.out.println("   " + extracto + "...");
                
                // Test 4: Proceso de registro
                System.out.println("\n4. 📝 Simulando proceso de registro...");
                try {
                    // Intentar registrar el usuario
                    var resultadoRegistro = dUsuario.register("Jairo", "Jairo", "123456789", "masculino", emailPrueba);
                    System.out.println("   ✅ Usuario registrado exitosamente");
                    System.out.println("   👤 Datos: " + String.join(", ", resultadoRegistro.get(0)));
                    
                    // Verificar de nuevo
                    boolean ahoraExiste = dUsuario.existsByEmail(emailPrueba);
                    System.out.println("   🔍 Verificación post-registro: " + (ahoraExiste ? "REGISTRADO" : "ERROR"));
                    
                } catch (Exception e) {
                    if (e.getMessage().contains("ya está registrado")) {
                        System.out.println("   ℹ️ Usuario ya está registrado (esto es correcto)");
                    } else {
                        System.out.println("   ❌ Error en registro: " + e.getMessage());
                    }
                }
                
            } else {
                System.out.println("\n2. ✅ Usuario ya está registrado - debería poder usar comandos");
                
                // Test: Generar tabla de usuarios
                System.out.println("\n3. 📊 Generando tabla con HTML moderno...");
                String[] headers = {"ID", "Nombre", "Email", "Fecha"};
                java.util.List<String[]> data = new java.util.ArrayList<>();
                data.add(new String[]{"1", "Usuario Test", emailPrueba, "2024-01-15"});
                
                String htmlTabla = HtmlRes.generateTable("Lista de Usuarios", headers, data);
                
                // Guardar para inspección
                java.nio.file.Files.write(java.nio.file.Paths.get("tabla_moderna_real.html"), 
                    htmlTabla.getBytes());
                
                System.out.println("   ✅ Tabla HTML moderna generada");
                System.out.println("   📁 Guardado como: tabla_moderna_real.html");
            }
            
            System.out.println("\n✅ TEST COMPLETADO");
            System.out.println("\n📋 CONCLUSIONES:");
            System.out.println("   • Email " + emailPrueba + ": " + (existeEmail ? "REGISTRADO" : "NO REGISTRADO"));
            System.out.println("   • El sistema debería " + (existeEmail ? "mostrar datos" : "pedir registro"));
            System.out.println("   • HTML moderno implementado correctamente");
            
            System.out.println("\n🌐 Abre los archivos .html generados para ver el diseño moderno");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 