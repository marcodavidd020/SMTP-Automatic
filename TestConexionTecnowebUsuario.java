package test;

import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;
import data.DUsuario;
import java.sql.*;

public class TestConexionTecnowebUsuario {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST: Verificación de Usuario en Base de Datos TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        String emailBuscado = "marcodavidtoledo20@gmail.com";
        
        // 1. Configurar conexión a Tecnoweb
        System.out.println("\n1️⃣ CONFIGURANDO CONEXIÓN A TECNOWEB:");
        DBConnectionManager.setActiveConfig(DBConnectionManager.ConfigType.TECNOWEB);
        DBConnectionManager.printCurrentConfig();
        
        // 2. Probar conexión directa
        System.out.println("\n2️⃣ PROBANDO CONEXIÓN DIRECTA:");
        testConexionDirecta();
        
        // 3. Verificar usuario usando DUsuario
        System.out.println("\n3️⃣ VERIFICANDO USUARIO CON DUsuario:");
        testUsuarioConDUsuario(emailBuscado);
        
        // 4. Verificar usuario con consulta SQL directa
        System.out.println("\n4️⃣ VERIFICANDO CON CONSULTA SQL DIRECTA:");
        testUsuarioConSQLDirecto(emailBuscado);
        
        // 5. Listar algunos usuarios para ver la estructura
        System.out.println("\n5️⃣ LISTANDO USUARIOS PARA VER ESTRUCTURA:");
        listarAlgunosUsuarios();
        
        System.out.println("\n✅ TEST COMPLETADO");
    }
    
    private static void testConexionDirecta() {
        try {
            String url = DBConnectionManager.getUrl();
            String user = DBConnectionManager.getUser();
            String password = DBConnectionManager.getPassword();
            
            System.out.println("🔗 Intentando conectar a: " + url);
            System.out.println("👤 Usuario: " + user);
            
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Conexión directa exitosa!");
            
            // Verificar que estamos en la BD correcta
            DatabaseMetaData metaData = conn.getMetaData();
            System.out.println("🗄️ Base de datos conectada: " + metaData.getURL());
            
            conn.close();
            
        } catch (SQLException e) {
            System.err.println("❌ Error en conexión directa: " + e.getMessage());
            if (e.getMessage().contains("remaining connection slots")) {
                System.err.println("💡 El servidor tecnoweb está sobrecargado (connection slots)");
            }
        }
    }
    
    private static void testUsuarioConDUsuario(String email) {
        try {
            DUsuario dUsuario = new DUsuario();
            
            System.out.println("🔍 Buscando usuario: " + email);
            
            // Método 1: existsByEmail
            boolean existe = dUsuario.existsByEmail(email);
            System.out.println("📋 existsByEmail(): " + (existe ? "✅ EXISTE" : "❌ NO EXISTE"));
            
            if (existe) {
                // Método 2: getByEmail
                try {
                    var usuario = dUsuario.getByEmail(email);
                    System.out.println("📄 Usuario encontrado:");
                    System.out.println("   ID: " + usuario.get(0)[0]);
                    System.out.println("   Nombre: " + usuario.get(0)[1]);
                    System.out.println("   Email: " + usuario.get(0)[2]);
                    System.out.println("   Created: " + usuario.get(0)[3]);
                } catch (SQLException e) {
                    System.err.println("❌ Error obteniendo datos del usuario: " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error verificando usuario con DUsuario: " + e.getMessage());
            if (e.getMessage().contains("remaining connection slots")) {
                System.err.println("💡 Tecnoweb sobrecargado - connection slots agotados");
            }
        }
    }
    
    private static void testUsuarioConSQLDirecto(String email) {
        try {
            SqlConnection sqlConn = new SqlConnection(
                DBConnectionManager.getDatabase(),
                DBConnectionManager.getServer(),
                DBConnectionManager.getPort(),
                DBConnectionManager.getUser(),
                DBConnectionManager.getPassword()
            );
            
            Connection conn = sqlConn.connect();
            
            // Consulta directa
            String query = "SELECT * FROM user WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            
            System.out.println("🔍 Ejecutando: " + query);
            System.out.println("📧 Email: " + email);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                System.out.println("✅ USUARIO ENCONTRADO en SQL directo:");
                System.out.println("   ID: " + rs.getInt("id"));
                System.out.println("   Nombre: " + rs.getString("nombre"));
                System.out.println("   Email: " + rs.getString("email"));
                System.out.println("   Celular: " + rs.getString("celular"));
                System.out.println("   Género: " + rs.getString("genero"));
                System.out.println("   Estado: " + rs.getString("estado"));
                System.out.println("   Created: " + rs.getString("created_at"));
            } else {
                System.out.println("❌ USUARIO NO ENCONTRADO en SQL directo");
            }
            
            ps.close();
            conn.close();
            sqlConn.closeConnection();
            
        } catch (SQLException e) {
            System.err.println("❌ Error en consulta SQL directa: " + e.getMessage());
            if (e.getMessage().contains("remaining connection slots")) {
                System.err.println("💡 Tecnoweb sobrecargado");
            } else if (e.getMessage().contains("column") && e.getMessage().contains("does not exist")) {
                System.err.println("💡 Posible problema de estructura de tabla");
            }
        }
    }
    
    private static void listarAlgunosUsuarios() {
        try {
            DUsuario dUsuario = new DUsuario();
            
            System.out.println("📋 Listando usuarios para ver estructura...");
            var usuarios = dUsuario.list();
            
            System.out.println("👥 Total usuarios encontrados: " + usuarios.size());
            
            // Mostrar primeros 5 usuarios
            int count = Math.min(5, usuarios.size());
            for (int i = 0; i < count; i++) {
                String[] user = usuarios.get(i);
                System.out.println("   " + (i+1) + ". ID:" + user[0] + " | " + user[1] + " | " + user[2]);
            }
            
            if (usuarios.size() > 5) {
                System.out.println("   ... y " + (usuarios.size() - 5) + " usuarios más");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error listando usuarios: " + e.getMessage());
        }
    }
} 