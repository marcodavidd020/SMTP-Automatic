package test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Test para descubrir la estructura REAL de tecnoweb
 */
public class TestRealStructure {
    
    public static void main(String[] args) {
        System.out.println("🔍 DESCUBRIENDO ESTRUCTURA REAL DE TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        SqlConnection connection = new SqlConnection(
            DBConnection.database, 
            DBConnection.server, 
            DBConnection.port, 
            DBConnection.user, 
            DBConnection.password
        );
        
        try (Connection conn = connection.connect()) {
            System.out.println("✅ Conectado a tecnoweb");
            
            DatabaseMetaData metaData = conn.getMetaData();
            
            // 1. Buscar tablas que contengan 'user' o 'usuario'
            System.out.println("\n1. 🔍 BUSCANDO TABLAS DE USUARIOS:");
            ResultSet tables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                if (tableName.toLowerCase().contains("user") || 
                    tableName.toLowerCase().contains("usuario")) {
                    
                    System.out.println("   📄 Tabla encontrada: " + tableName);
                    
                    // Mostrar columnas de esta tabla
                    ResultSet columns = metaData.getColumns(null, null, tableName, null);
                    System.out.println("      📋 Columnas:");
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String dataType = columns.getString("TYPE_NAME");
                        System.out.println("         • " + columnName + " (" + dataType + ")");
                    }
                    
                    // Probar consulta SELECT * para ver contenido
                    try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + tableName + " LIMIT 3")) {
                        ResultSet rs = ps.executeQuery();
                        System.out.println("      📊 Datos de muestra:");
                        int colCount = rs.getMetaData().getColumnCount();
                        while (rs.next()) {
                            System.out.print("         ");
                            for (int i = 1; i <= colCount; i++) {
                                if (i > 1) System.out.print(" | ");
                                System.out.print(rs.getString(i));
                            }
                            System.out.println();
                        }
                    } catch (Exception e) {
                        System.out.println("      ❌ Error leyendo datos: " + e.getMessage());
                    }
                    System.out.println();
                }
            }
            
            // 2. Buscar la tabla correcta con campo email
            System.out.println("2. 🔍 BUSCANDO TABLAS CON CAMPO 'email':");
            ResultSet allTables = metaData.getTables(null, null, null, new String[]{"TABLE"});
            
            while (allTables.next()) {
                String tableName = allTables.getString("TABLE_NAME");
                
                ResultSet emailColumn = metaData.getColumns(null, null, tableName, "email");
                if (emailColumn.next()) {
                    System.out.println("   ✅ Tabla '" + tableName + "' tiene campo 'email'");
                    
                    // Mostrar todas las columnas
                    ResultSet allColumns = metaData.getColumns(null, null, tableName, null);
                    System.out.println("      📋 Todas las columnas:");
                    while (allColumns.next()) {
                        String columnName = allColumns.getString("COLUMN_NAME");
                        String dataType = allColumns.getString("TYPE_NAME");
                        System.out.println("         • " + columnName + " (" + dataType + ")");
                    }
                    
                    // Probar consulta para ver datos
                    try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM " + tableName)) {
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            System.out.println("      📊 Total registros: " + count);
                        }
                    } catch (Exception e) {
                        System.out.println("      ❌ Error contando: " + e.getMessage());
                    }
                    
                    // Mostrar algunos emails de muestra
                    try (PreparedStatement ps = conn.prepareStatement("SELECT email FROM " + tableName + " LIMIT 3")) {
                        ResultSet rs = ps.executeQuery();
                        System.out.println("      📧 Emails de muestra:");
                        while (rs.next()) {
                            System.out.println("         • " + rs.getString("email"));
                        }
                    } catch (Exception e) {
                        System.out.println("      ❌ Error leyendo emails: " + e.getMessage());
                    }
                    System.out.println();
                }
            }
            
            // 3. Verificar específicamente la tabla 'users' (plural)
            System.out.println("3. 🔍 VERIFICANDO TABLA 'users' (plural):");
            try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    System.out.println("   ✅ Tabla 'users' encontrada con " + rs.getInt(1) + " registros");
                    
                    // Verificar estructura
                    ResultSet columns = metaData.getColumns(null, null, "users", null);
                    System.out.println("      📋 Columnas de 'users':");
                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String dataType = columns.getString("TYPE_NAME");
                        System.out.println("         • " + columnName + " (" + dataType + ")");
                    }
                }
            } catch (SQLException e) {
                System.out.println("   ❌ Tabla 'users' no encontrada: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n📋 ANÁLISIS COMPLETADO");
    }
} 