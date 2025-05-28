import java.util.ArrayList;
import java.util.List;

/**
 * Test directo para verificar que se use el HTML moderno
 */
public class TestHTMLDirecto {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DIRECTO DE HTML MODERNO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // Importar la clase HtmlRes compilada
            Class<?> htmlResClass = Class.forName("librerias.HtmlRes");
            
            System.out.println("✅ Clase HtmlRes cargada correctamente");
            
            // Test 1: Generar mensaje de bienvenida
            System.out.println("\n1. 📨 Generando mensaje de bienvenida...");
            
            // Usar reflexión para llamar al método
            java.lang.reflect.Method welcomeMethod = htmlResClass.getMethod("generateWelcome", String.class);
            String htmlBienvenida = (String) welcomeMethod.invoke(null, "marcodavidtoledo@gmail.com");
            
            // Guardar HTML
            java.nio.file.Files.write(java.nio.file.Paths.get("test_bienvenida_directo.html"), 
                htmlBienvenida.getBytes());
            
            System.out.println("   ✅ HTML de bienvenida generado");
            System.out.println("   📁 Guardado como: test_bienvenida_directo.html");
            
            // Mostrar extracto
            System.out.println("\n2. 📝 Extracto del HTML:");
            String extracto = htmlBienvenida.substring(0, Math.min(300, htmlBienvenida.length()));
            System.out.println("   " + extracto + "...");
            
            // Verificar si es el HTML moderno
            boolean esModerno = htmlBienvenida.contains("linear-gradient") && 
                              htmlBienvenida.contains("border-radius") && 
                              htmlBienvenida.contains("box-shadow");
            
            System.out.println("\n3. 🔍 Verificación de diseño moderno:");
            System.out.println("   • Contiene gradientes: " + (htmlBienvenida.contains("linear-gradient") ? "✅ SÍ" : "❌ NO"));
            System.out.println("   • Contiene border-radius: " + (htmlBienvenida.contains("border-radius") ? "✅ SÍ" : "❌ NO"));
            System.out.println("   • Contiene box-shadow: " + (htmlBienvenida.contains("box-shadow") ? "✅ SÍ" : "❌ NO"));
            System.out.println("   • Diseño moderno: " + (esModerno ? "✅ SÍ" : "❌ NO"));
            
            // Test 2: Generar tabla moderna
            System.out.println("\n4. 📊 Generando tabla moderna...");
            
            String[] headers = {"ID", "Comando", "Estado"};
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"1", "usuario get", "✅ SÍ"});
            data.add(new String[]{"2", "producto get", "✅ SÍ"});
            data.add(new String[]{"3", "categoria add", "⏳ DESARROLLO"});
            data.add(new String[]{"4", "evento get", "❌ NO"});
            
            java.lang.reflect.Method tableMethod = htmlResClass.getMethod("generateTable", String.class, String[].class, List.class);
            String htmlTabla = (String) tableMethod.invoke(null, "Lista de Comandos", headers, data);
            
            // Guardar tabla
            java.nio.file.Files.write(java.nio.file.Paths.get("test_tabla_directa.html"), 
                htmlTabla.getBytes());
            
            System.out.println("   ✅ Tabla HTML generada");
            System.out.println("   📁 Guardado como: test_tabla_directa.html");
            
            // Verificar tabla moderna
            boolean tablaModerna = htmlTabla.contains("badge") && 
                                 htmlTabla.contains("container") && 
                                 htmlTabla.contains("header");
            
            System.out.println("   • Tabla moderna: " + (tablaModerna ? "✅ SÍ" : "❌ NO"));
            
            System.out.println("\n✅ RESULTADO FINAL:");
            if (esModerno && tablaModerna) {
                System.out.println("   🎉 ¡HTML MODERNO FUNCIONANDO CORRECTAMENTE!");
                System.out.println("   📧 Los emails deberían verse con el diseño nuevo");
            } else {
                System.out.println("   ❌ HTML ANTIGUO DETECTADO");
                System.out.println("   🔧 Necesita recompilación o hay conflicto de versiones");
            }
            
            System.out.println("\n🌐 Abre los archivos HTML para ver el resultado visual");
            
        } catch (Exception e) {
            System.err.println("❌ Error en el test: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 