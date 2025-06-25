package postgresConecction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase para probar la conexión a la base de datos
 * 
 * @author Jairo
 */
public class TestConnection {

    public static void main(String[] args) {
        System.out.println("🔧 DIAGNÓSTICO DE CONEXIÓN A BASE DE DATOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        // Mostrar configuración actual
        System.out.println("📊 CONFIGURACIÓN ACTUAL:");
        System.out.println("   🗄️ Base de datos: " + DBConnection.database);
        System.out.println("   🖥️ Servidor: " + DBConnection.server);
        System.out.println("   🔌 Puerto: " + DBConnection.port);
        System.out.println("   👤 Usuario: " + DBConnection.user);
        System.out.println("   🔗 URL: " + DBConnection.url);
        System.out.println();

        // Test 1: Verificar driver PostgreSQL
        System.out.println("🧪 TEST 1: Verificando driver PostgreSQL...");
        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("   ✅ Driver PostgreSQL encontrado");
        } catch (ClassNotFoundException e) {
            System.out.println("   ❌ Driver PostgreSQL NO encontrado");
            System.out.println("   💡 Solución: Agregar postgresql-xx.x.x.jar al classpath");
            return;
        }

        // Test 2: Probar conexión básica
        System.out.println("\n🧪 TEST 2: Probando conexión básica...");
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DBConnection.url, DBConnection.user, DBConnection.password);
            System.out.println("   ✅ Conexión exitosa a la base de datos");

            // Test 3: Verificar que la base de datos no esté vacía
            System.out.println("\n🧪 TEST 3: Verificando tablas existentes...");
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'");
            ResultSet rs = ps.executeQuery();

            boolean hayTablas = false;
            System.out.println("   📋 Tablas encontradas:");
            while (rs.next()) {
                hayTablas = true;
                System.out.println("      - " + rs.getString("table_name"));
            }

            if (!hayTablas) {
                System.out.println("   ⚠️ No se encontraron tablas en la base de datos");
                System.out.println("   💡 Ejecuta: psql -U postgres -d EcommerceTool -f sql/init_database.sql");
            }

            // Test 4: Verificar tabla usuarios específicamente
            System.out.println("\n🧪 TEST 4: Verificando tabla 'usuarios'...");
            try {
                PreparedStatement psUsuarios = conn.prepareStatement("SELECT COUNT(*) FROM usuarios");
                ResultSet rsUsuarios = psUsuarios.executeQuery();
                if (rsUsuarios.next()) {
                    int count = rsUsuarios.getInt(1);
                    System.out.println("   ✅ Tabla 'usuarios' existe con " + count + " registros");
                } else {
                    System.out.println("   ❌ Tabla 'usuarios' existe pero está vacía");
                }
            } catch (SQLException e) {
                System.out.println("   ❌ Tabla 'usuarios' NO existe");
                System.out.println("   💡 Error: " + e.getMessage());
                System.out.println("   💡 Ejecuta el script de inicialización");
            }

        } catch (SQLException e) {
            System.out.println("   ❌ Error de conexión: " + e.getMessage());
            System.out.println("\n🔧 POSIBLES CAUSAS:");
            System.out.println("   1. PostgreSQL no está ejecutándose");
            System.out.println("   2. Base de datos 'EcommerceTool' no existe");
            System.out.println("   3. Usuario/contraseña incorrectos");
            System.out.println("   4. Puerto incorrecto");

            System.out.println("\n💡 SOLUCIONES:");
            System.out.println("   1. sudo systemctl start postgresql");
            System.out.println("   2. sudo -u postgres createdb EcommerceTool");
            System.out.println("   3. Verificar credenciales en DBConnection.java");

        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("\n🔌 Conexión cerrada correctamente");
                } catch (SQLException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }

        System.out.println("\n🎉 Diagnóstico completado");
    }

    /**
     * Test rápido de conexión (para usar en otras clases)
     */
    public static boolean testConnection() {
        try (Connection conn = DriverManager.getConnection(DBConnection.url, DBConnection.user,
                DBConnection.password)) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            return false;
        }
    }
}