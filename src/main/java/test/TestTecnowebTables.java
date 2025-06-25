package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import postgresConecction.SqlConnection;
import postgresConecction.DBConnection;

/**
 * Test para verificar la estructura de tablas en tecnoweb
 */
public class TestTecnowebTables {
    
    public static void main(String[] args) {
        System.out.println("🔍 VERIFICANDO ESTRUCTURA DE TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 Servidor: " + DBConnection.server);
        System.out.println("🗄️ Base de datos: " + DBConnection.database);
        System.out.println("👤 Usuario: " + DBConnection.user);
        
        SqlConnection connection = new SqlConnection(
            DBConnection.database, 
            DBConnection.server, 
            DBConnection.port, 
            DBConnection.user, 
            DBConnection.password
        );
        
        try (Connection conn = connection.connect()) {
            System.out.println("\n✅ Conexión exitosa a tecnoweb");
            
            // 1. Listar todas las tablas
            System.out.println("\n1. 📋 TABLAS DISPONIBLES:");
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                System.out.println("   📄 " + tableName);
            }
            
            // 2. Verificar estructura de tabla user/usuario/usuarios
            System.out.println("\n2. 🔍 BUSCANDO TABLA DE USUARIOS:");
            String[] possibleUserTables = {"user", "usuario", "usuarios", "users"};
            
            for (String tableName : possibleUserTables) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("   ✅ Tabla '" + tableName + "' encontrada (" + count + " registros)");
                        
                        // Mostrar columnas
                        ResultSet columns = metaData.getColumns(null, null, tableName, null);
                        System.out.println("      📋 Columnas:");
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String dataType = columns.getString("TYPE_NAME");
                            System.out.println("         • " + columnName + " (" + dataType + ")");
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    // Tabla no existe, continuar
                }
            }
            
            // 3. Verificar tabla cliente/clientes
            System.out.println("3. 🔍 BUSCANDO TABLA DE CLIENTES:");
            String[] possibleClientTables = {"cliente", "clientes", "client", "clients"};
            
            for (String tableName : possibleClientTables) {
                try (Statement stmt = conn.createStatement()) {
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName);
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("   ✅ Tabla '" + tableName + "' encontrada (" + count + " registros)");
                        
                        // Mostrar columnas
                        ResultSet columns = metaData.getColumns(null, null, tableName, null);
                        System.out.println("      📋 Columnas:");
                        while (columns.next()) {
                            String columnName = columns.getString("COLUMN_NAME");
                            String dataType = columns.getString("TYPE_NAME");
                            System.out.println("         • " + columnName + " (" + dataType + ")");
                        }
                        System.out.println();
                    }
                } catch (Exception e) {
                    // Tabla no existe, continuar
                }
            }
            
            // 4. Buscar cualquier tabla que contenga email
            System.out.println("4. 🔍 BUSCANDO TABLAS CON CAMPO EMAIL:");
            ResultSet allTables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (allTables.next()) {
                String tableName = allTables.getString("TABLE_NAME");
                
                ResultSet columns = metaData.getColumns(null, null, tableName, "email");
                if (columns.next()) {
                    System.out.println("   ✅ Tabla '" + tableName + "' tiene campo 'email'");
                    
                    // Mostrar todas las columnas de esta tabla
                    ResultSet allColumns = metaData.getColumns(null, null, tableName, null);
                    System.out.println("      📋 Todas las columnas:");
                    while (allColumns.next()) {
                        String columnName = allColumns.getString("COLUMN_NAME");
                        String dataType = allColumns.getString("TYPE_NAME");
                        System.out.println("         • " + columnName + " (" + dataType + ")");
                    }
                    System.out.println();
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error conectando a tecnoweb: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("📋 ANÁLISIS COMPLETADO");
    }
} 