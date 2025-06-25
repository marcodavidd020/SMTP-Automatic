package test;

import data.DUsuario;
import postgresConecction.DBConnection;

/**
 * Test específico para autenticación en tecnoweb
 */
public class TestTecnowebAuth {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE AUTENTICACIÓN EN TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Verificar configuración
        System.out.println("🔧 CONFIGURACIÓN ACTUAL:");
        System.out.println("   🌐 Servidor: " + DBConnection.server);
        System.out.println("   🗄️ Base: " + DBConnection.database);
        System.out.println("   👤 Usuario: " + DBConnection.user);
        
        try {
            String emailPrueba = "marcodavidtoledo@gmail.com";
            
            // Usar configuración explícita de tecnoweb
            DUsuario dUsuario = new DUsuario(
                DBConnection.database,
                DBConnection.server, 
                DBConnection.port,
                DBConnection.user,
                DBConnection.password
            );
            
            // Test 1: Verificar conexión y estructura
            System.out.println("\n1. 🔍 Verificando conexión a tecnoweb...");
            
            try {
                java.util.List<String[]> usuarios = dUsuario.list();
                System.out.println("   ✅ Conexión exitosa - " + usuarios.size() + " usuarios encontrados");
                
                // Mostrar algunos usuarios para verificar
                System.out.println("\n📋 USUARIOS EN TECNOWEB:");
                for (int i = 0; i < Math.min(3, usuarios.size()); i++) {
                    String[] usuario = usuarios.get(i);
                    System.out.println("   👤 " + usuario[0] + ": " + usuario[1] + " (" + usuario[2] + ")");
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ Error listando usuarios: " + e.getMessage());
                throw e;
            }
            
            // Test 2: Verificar email específico
            System.out.println("\n2. 🔍 Verificando email: " + emailPrueba);
            
            try {
                boolean existeEmail = dUsuario.existsByEmail(emailPrueba);
                System.out.println("   📧 Email " + emailPrueba + ": " + (existeEmail ? "REGISTRADO" : "NO REGISTRADO"));
                
                if (existeEmail) {
                    // Obtener datos del usuario
                    java.util.List<String[]> userData = dUsuario.getByEmail(emailPrueba);
                    if (!userData.isEmpty()) {
                        String[] user = userData.get(0);
                        System.out.println("   👤 Datos: ID=" + user[0] + ", Nombre=" + user[1] + ", Email=" + user[2]);
                    }
                } else {
                    System.out.println("   ℹ️ El email no está registrado en tecnoweb");
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ Error verificando email: " + e.getMessage());
                throw e;
            }
            
            // Test 3: Intentar registrar si no existe
            if (!dUsuario.existsByEmail(emailPrueba)) {
                System.out.println("\n3. 📝 Intentando registrar usuario...");
                
                try {
                    java.util.List<String[]> resultado = dUsuario.register(
                        "Marco David", "Toledo", "12345678", "masculino", emailPrueba
                    );
                    
                    System.out.println("   ✅ Usuario registrado exitosamente");
                    String[] nuevoUsuario = resultado.get(0);
                    System.out.println("   👤 ID: " + nuevoUsuario[0] + ", Nombre: " + nuevoUsuario[1]);
                    
                } catch (Exception e) {
                    System.err.println("   ❌ Error registrando: " + e.getMessage());
                }
            }
            
            // Test 4: Verificar estructura de cliente
            System.out.println("\n4. 🔍 Verificando estructura de clientes...");
            
            try {
                data.DCliente dCliente = new data.DCliente();
                
                java.util.List<String[]> clientes = dCliente.list();
                System.out.println("   ✅ " + clientes.size() + " clientes en tecnoweb");
                
                // Mostrar algunos clientes
                for (int i = 0; i < Math.min(3, clientes.size()); i++) {
                    String[] cliente = clientes.get(i);
                    System.out.println("   🛍️ Cliente ID " + cliente[0] + " - NIT: " + cliente[2]);
                }
                
            } catch (Exception e) {
                System.err.println("   ❌ Error con clientes: " + e.getMessage());
            }
            
            System.out.println("\n✅ PRUEBAS COMPLETADAS CON TECNOWEB");
            
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 