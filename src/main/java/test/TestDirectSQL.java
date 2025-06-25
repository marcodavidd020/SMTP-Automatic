package test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Test directo de consultas SQL para verificar la estructura
 */
public class TestDirectSQL {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DIRECTO DE SQL EN TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SqlConnection connection = new SqlConnection(
            DBConnection.database, 
            DBConnection.server, 
            DBConnection.port, 
            DBConnection.user, 
            DBConnection.password
        );
        
        try (Connection conn = connection.connect()) {
            System.out.println("✅ Conectado a: " + DBConnection.server + "/" + DBConnection.database);
            
            // Test 1: Verificar tabla user existe y sus columnas
            System.out.println("\n1. 🔍 VERIFICANDO TABLA 'user':");
            String[] testQueries = {
                "SELECT COUNT(*) FROM user",
                "SELECT id FROM user LIMIT 1",
                "SELECT nombre FROM user LIMIT 1", 
                "SELECT email FROM user LIMIT 1",
                "SELECT * FROM user LIMIT 1"
            };
            
            for (String query : testQueries) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    System.out.println("   ▶️ " + query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.println("      ✅ OK - Resultado: " + rs.getString(1));
                    }
                } catch (SQLException e) {
                    System.out.println("      ❌ ERROR: " + e.getMessage());
                }
            }
            
            // Test 2: Verificar tabla cliente
            System.out.println("\n2. 🔍 VERIFICANDO TABLA 'cliente':");
            String[] clienteQueries = {
                "SELECT COUNT(*) FROM cliente",
                "SELECT id, user_id, nit FROM cliente LIMIT 1"
            };
            
            for (String query : clienteQueries) {
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    System.out.println("   ▶️ " + query);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.println("      ✅ OK - Resultado: " + rs.getString(1));
                    }
                } catch (SQLException e) {
                    System.out.println("      ❌ ERROR: " + e.getMessage());
                }
            }
            
            // Test 3: JOIN entre user y cliente
            System.out.println("\n3. 🔍 VERIFICANDO JOIN user-cliente:");
            String joinQuery = "SELECT u.id, u.nombre, u.email, c.nit " +
                             "FROM user u " +
                             "LEFT JOIN cliente c ON u.id = c.user_id " +
                             "LIMIT 3";
            
            try (PreparedStatement ps = conn.prepareStatement(joinQuery)) {
                System.out.println("   ▶️ " + joinQuery);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println("      ✅ Usuario: " + rs.getString("nombre") + 
                                     " (" + rs.getString("email") + ") - NIT: " + rs.getString("nit"));
                }
            } catch (SQLException e) {
                System.out.println("      ❌ ERROR JOIN: " + e.getMessage());
            }
            
            // Test 4: Verificar email específico
            System.out.println("\n4. 🔍 VERIFICANDO EMAIL ESPECÍFICO:");
            String emailQuery = "SELECT * FROM user WHERE email = ?";
            String[] emailsTest = {
                "marcodavidtoledo@gmail.com",
                "marcodavidtoledo20@gmail.com"
            };
            
            for (String email : emailsTest) {
                try (PreparedStatement ps = conn.prepareStatement(emailQuery)) {
                    ps.setString(1, email);
                    System.out.println("   ▶️ Buscando: " + email);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        System.out.println("      ✅ ENCONTRADO: " + rs.getString("nombre") + 
                                         " (ID: " + rs.getInt("id") + ")");
                    } else {
                        System.out.println("      ❌ NO ENCONTRADO: " + email);
                    }
                } catch (SQLException e) {
                    System.out.println("      ❌ ERROR: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n📋 TEST COMPLETADO");
    }
} 