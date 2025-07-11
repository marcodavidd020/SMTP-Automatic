package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DCategoria {

    public static final String[] HEADERS = {"id", "nombre", "descripcion"};

    private final SqlConnection connection;

    public DCategoria() {
        this.connection = new SqlConnection(DBConnectionManager.getDatabase(), DBConnectionManager.getServer(), DBConnectionManager.getPort(), DBConnectionManager.getUser(), DBConnectionManager.getPassword());
    }
    
    private DCategoria(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DCategoria createWithGlobalConfig() {
        return new DCategoria(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> categoria = new ArrayList<>();
        String query = "SELECT * FROM categoria WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                categoria.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("descripcion")
                });
            } else {
                throw new SQLException("Categoría no encontrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return categoria;
    }

    public List<String[]> list() throws SQLException {
        List<String[]> categorias = new ArrayList<>();
        String query = "SELECT * FROM categoria ORDER BY nombre";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                categorias.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("descripcion")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando categorías: " + e.getMessage());
            throw e;
        }
        
        System.out.println("📂 Total categorías encontradas: " + categorias.size());
        return categorias;
    }

    public void disconnect() {
        connection.closeConnection();
    }
} 