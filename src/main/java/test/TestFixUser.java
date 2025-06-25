package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Test para resolver el problema con la tabla user
 */
public class TestFixUser {
    
    public static void main(String[] args) {
        System.out.println("🔧 DIAGNOSTICANDO PROBLEMA CON TABLA USER");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SqlConnection connection = new SqlConnection(
            DBConnection.database, 
            DBConnection.server, 
            DBConnection.port, 
            DBConnection.user, 
            DBConnection.password
        );
        
        try (Connection conn = connection.connect()) {
            System.out.println("✅ Conectado a tecnoweb");
            
            // Test 1: Probar diferentes formas de acceder a user
            System.out.println("\n1. 🔍 PROBANDO DIFERENTES ACCESOS A 'user':");
            
            String[] queries = {
                "SELECT current_user",
                "SELECT current_database()",
                "SELECT schemaname, tablename FROM pg_tables WHERE tablename = 'user'",
                "SELECT * FROM information_schema.tables WHERE table_name = 'user'",
                "SELECT column_name FROM information_schema.columns WHERE table_name = 'user'",
                "SELECT \"id\", \"nombre\", \"email\" FROM \"user\" LIMIT 1",
                "SELECT id, nombre, email FROM public.user LIMIT 1"
            };
            
            for (String query : queries) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    System.out.println("   ▶️ " + query);
                    ResultSet rs = ps.executeQuery();
                    
                    if (rs.next()) {
                        int colCount = rs.getMetaData().getColumnCount();
                        System.out.print("      ✅ Resultado: ");
                        for (int i = 1; i <= colCount; i++) {
                            if (i > 1) System.out.print(" | ");
                            System.out.print(rs.getString(i));
                        }
                        System.out.println();
                    } else {
                        System.out.println("      ✅ Sin resultados");
                    }
                } catch (SQLException e) {
                    System.out.println("      ❌ ERROR: " + e.getMessage());
                }
            }
            
            // Test 2: Buscar tabla alternativa de usuarios
            System.out.println("\n2. 🔍 BUSCANDO TABLA ALTERNATIVA DE USUARIOS:");
            
            String[] possibleTables = {
                "usuario", "usuarios", "persona", "personas", 
                "account", "accounts", "member", "members"
            };
            
            for (String tableName : possibleTables) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName)) {
                    System.out.println("   ▶️ Probando tabla: " + tableName);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("      ✅ ENCONTRADA: " + tableName + " (" + count + " registros)");
                        
                        // Verificar si tiene campo email
                        try (PreparedStatement ps2 = conn.prepareStatement("SELECT email FROM " + tableName + " LIMIT 1")) {
                            ResultSet rs2 = ps2.executeQuery();
                            if (rs2.next()) {
                                System.out.println("         📧 Con campo email: " + rs2.getString("email"));
                            }
                        } catch (Exception e) {
                            System.out.println("         ❌ Sin campo email: " + e.getMessage());
                        }
                    }
                } catch (SQLException e) {
                    System.out.println("      ❌ No existe: " + tableName);
                }
            }
            
            // Test 3: Verificar si es problema de schema
            System.out.println("\n3. 🔍 VERIFICANDO SCHEMAS:");
            
            try (PreparedStatement ps = conn.prepareStatement("SELECT schema_name FROM information_schema.schemata")) {
                System.out.println("   📋 Schemas disponibles:");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("      • " + rs.getString("schema_name"));
                }
            } catch (SQLException e) {
                System.out.println("   ❌ Error listando schemas: " + e.getMessage());
            }
            
            // Test 4: Probar insertar un usuario de prueba directamente
            System.out.println("\n4. 🔍 PROBANDO INSERTAR USUARIO DE PRUEBA:");
            
            String insertQuery = "INSERT INTO public.user (nombre, celular, email, genero, password, estado, created_at, updated_at) " +
                                "VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW()) RETURNING id";
            
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setString(1, "Test Usuario");
                ps.setString(2, "123456789");
                ps.setString(3, "test@tecnoweb.com");
                ps.setString(4, "masculino");
                ps.setString(5, "password123");
                ps.setString(6, "activo");
                
                System.out.println("   ▶️ Intentando insertar usuario de prueba...");
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int newId = rs.getInt(1);
                    System.out.println("      ✅ Usuario insertado con ID: " + newId);
                    
                    // Ahora intentar leerlo
                    try (PreparedStatement ps2 = conn.prepareStatement("SELECT * FROM public.user WHERE id = ?")) {
                        ps2.setInt(1, newId);
                        ResultSet rs2 = ps2.executeQuery();
                        if (rs2.next()) {
                            System.out.println("      ✅ Usuario leído exitosamente: " + rs2.getString("nombre"));
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("      ❌ Error insertando: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error general: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n📋 DIAGNÓSTICO COMPLETADO");
    }
} 