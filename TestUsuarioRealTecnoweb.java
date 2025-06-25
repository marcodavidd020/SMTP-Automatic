package test;

import postgresConecction.DBConnectionManager;
import java.sql.*;

public class TestUsuarioRealTecnoweb {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST: Verificación REAL del Usuario en TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String emailBuscado = "marcodavidtoledo20@gmail.com";
        
        // Configurar conexión a Tecnoweb
        DBConnectionManager.setActiveConfig(DBConnectionManager.ConfigType.TECNOWEB);
        
        System.out.println("📧 Email a verificar: " + emailBuscado);
        System.out.println("🗄️ Base de datos: " + DBConnectionManager.getDatabase());
        System.out.println("🖥️ Servidor: " + DBConnectionManager.getServer());
        
        // Test con manejo cuidadoso de conexiones
        verificarUsuarioConManejoCuidadoso(emailBuscado);
    }
    
    private static void verificarUsuarioConManejoCuidadoso(String email) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            // 1. Conectar
            System.out.println("\n1️⃣ Estableciendo conexión...");
            String url = DBConnectionManager.getUrl();
            String user = DBConnectionManager.getUser();
            String password = DBConnectionManager.getPassword();
            
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión establecida exitosamente");
            
            // 2. Preparar consulta
            System.out.println("\n2️⃣ Preparando consulta...");
            String query = "SELECT id, nombre, email, celular, genero, estado, created_at FROM \"user\" WHERE email = ?";
            ps = conn.prepareStatement(query);
            ps.setString(1, email);
            
            System.out.println("📝 Query: " + query);
            System.out.println("📧 Parámetro: " + email);
            
            // 3. Ejecutar consulta
            System.out.println("\n3️⃣ Ejecutando consulta...");
            rs = ps.executeQuery();
            
            // 4. Procesar resultado
            System.out.println("\n4️⃣ Procesando resultado...");
            if (rs.next()) {
                System.out.println("✅ ¡USUARIO ENCONTRADO EN TECNOWEB!");
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                System.out.println("   🆔 ID: " + rs.getInt("id"));
                System.out.println("   👤 Nombre: " + rs.getString("nombre"));
                System.out.println("   📧 Email: " + rs.getString("email"));
                System.out.println("   📞 Celular: " + rs.getString("celular"));
                System.out.println("   ⚧ Género: " + rs.getString("genero"));
                System.out.println("   📊 Estado: " + rs.getString("estado"));
                System.out.println("   📅 Creado: " + rs.getString("created_at"));
                System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                
                // Verificar si tiene cliente asociado
                verificarClienteAsociado(conn, rs.getInt("id"));
                
            } else {
                System.out.println("❌ Usuario NO encontrado en Tecnoweb");
            }
            
        } catch (SQLException e) {
            if (e.getMessage().contains("remaining connection slots")) {
                System.err.println("⚠️ Error de conexión: Tecnoweb sobrecargado");
                System.err.println("💡 La consulta puede haberse ejecutado parcialmente");
            } else {
                System.err.println("❌ Error SQL: " + e.getMessage());
            }
        } finally {
            // 5. Cerrar recursos cuidadosamente (ignorando errores de cierre)
            System.out.println("\n5️⃣ Cerrando recursos...");
            
            if (rs != null) {
                try {
                    rs.close();
                    System.out.println("✅ ResultSet cerrado");
                } catch (SQLException e) {
                    System.out.println("⚠️ Error cerrando ResultSet: " + e.getMessage());
                }
            }
            
            if (ps != null) {
                try {
                    ps.close();
                    System.out.println("✅ PreparedStatement cerrado");
                } catch (SQLException e) {
                    System.out.println("⚠️ Error cerrando PreparedStatement: " + e.getMessage());
                }
            }
            
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("✅ Connection cerrada");
                } catch (SQLException e) {
                    System.out.println("⚠️ Error cerrando Connection: " + e.getMessage());
                    System.out.println("💡 Esto es común cuando Tecnoweb está sobrecargado");
                }
            }
        }
    }
    
    private static void verificarClienteAsociado(Connection conn, int userId) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            System.out.println("\n🔍 Verificando cliente asociado...");
            String query = "SELECT id, nit FROM cliente WHERE user_id = ?";
            ps = conn.prepareStatement(query);
            ps.setInt(1, userId);
            
            rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("✅ Cliente encontrado:");
                System.out.println("   🆔 Cliente ID: " + rs.getInt("id"));
                System.out.println("   📄 NIT: " + rs.getString("nit"));
            } else {
                System.out.println("❌ No hay cliente asociado al usuario");
            }
            
        } catch (SQLException e) {
            System.err.println("⚠️ Error verificando cliente: " + e.getMessage());
        } finally {
            if (rs != null) {
                try { rs.close(); } catch (SQLException e) { /* ignore */ }
            }
            if (ps != null) {
                try { ps.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }
} 