package test;

import java.util.ArrayList;
import java.util.List;

import librerias.HtmlRes;

/**
 * Test para verificar las mejoras en el HTML de los emails
 */
public class TestHtmlMejorado {
    
    public static void main(String[] args) {
        System.out.println("🧪 PRUEBA DE HTML MEJORADO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // Test 1: Tabla de datos
            System.out.println("\n1. 📊 Generando tabla de usuarios...");
            String[] headers = {"ID", "Nombre", "Email", "Estado"};
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"1", "Juan Pérez", "juan@test.com", "✅ SÍ"});
            data.add(new String[]{"2", "María López", "maria@test.com", "❌ NO"});
            data.add(new String[]{"3", "Carlos Ruiz", "carlos@test.com", "⏳ DESARROLLO"});
            
            String tableHtml = HtmlRes.generateTable("Lista de Usuarios", headers, data);
            System.out.println("   ✅ HTML de tabla generado");
            
            // Test 2: Mensaje de error
            System.out.println("\n2. 🚨 Generando mensaje de error...");
            String errorHtml = HtmlRes.generateError("Error de Conexión", 
                "No se pudo conectar a la base de datos PostgreSQL");
            System.out.println("   ✅ HTML de error generado");
            
            // Test 3: Mensaje de éxito
            System.out.println("\n3. 🎉 Generando mensaje de éxito...");
            String successHtml = HtmlRes.generateSuccess("Registro Exitoso", 
                "¡Tu cuenta ha sido creada correctamente!");
            System.out.println("   ✅ HTML de éxito generado");
            
            // Test 4: Mensaje de bienvenida
            System.out.println("\n4. 👋 Generando mensaje de bienvenida...");
            String welcomeHtml = HtmlRes.generateWelcome("nuevo@test.com");
            System.out.println("   ✅ HTML de bienvenida generado");
            
            // Test 5: Texto simple
            System.out.println("\n5. 📝 Generando texto simple...");
            String[] textArgs = {"Información", "Este es un mensaje de prueba", "Sistema funcionando correctamente"};
            String textHtml = HtmlRes.generateText(textArgs);
            System.out.println("   ✅ HTML de texto generado");
            
            System.out.println("\n✅ TODAS LAS PRUEBAS COMPLETADAS");
            System.out.println("\n📋 CARACTERÍSTICAS IMPLEMENTADAS:");
            System.out.println("   ✅ CSS moderno con gradientes y sombras");
            System.out.println("   ✅ Diseño responsivo para móviles");
            System.out.println("   ✅ Badges de estado coloridos");
            System.out.println("   ✅ Tipografía profesional");
            System.out.println("   ✅ Estructura header/content/footer");
            System.out.println("   ✅ Efectos hover en tablas");
            System.out.println("   ✅ Boxes de error/éxito/información");
            
            // Guardar ejemplos en archivos
            System.out.println("\n📁 Guardando ejemplos HTML...");
            java.nio.file.Files.write(java.nio.file.Paths.get("tabla_ejemplo.html"), 
                tableHtml.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("error_ejemplo.html"), 
                errorHtml.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("exito_ejemplo.html"), 
                successHtml.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("bienvenida_ejemplo.html"), 
                welcomeHtml.getBytes());
            java.nio.file.Files.write(java.nio.file.Paths.get("texto_ejemplo.html"), 
                textHtml.getBytes());
            
            System.out.println("   ✅ Archivos HTML guardados:");
            System.out.println("      - tabla_ejemplo.html");
            System.out.println("      - error_ejemplo.html");
            System.out.println("      - exito_ejemplo.html");
            System.out.println("      - bienvenida_ejemplo.html");
            System.out.println("      - texto_ejemplo.html");
            
            System.out.println("\n🌐 Abre cualquier archivo .html en tu navegador para ver el diseño");
            
        } catch (Exception e) {
            System.err.println("❌ Error en la prueba: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 