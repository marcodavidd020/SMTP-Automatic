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

public class DCliente {

    public static final String[] HEADERS = {"id", "user_id", "nit", "nombre_usuario", "email_usuario"};

    private final SqlConnection connection;

    public DCliente() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DCliente(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DCliente createWithGlobalConfig() {
        return new DCliente(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> cliente = new ArrayList<>();
        String query = "SELECT c.*, u.nombre as nombre_usuario, u.email as email_usuario " +
                      "FROM cliente c " +
                      "LEFT JOIN user u ON c.user_id = u.id " +
                      "WHERE c.id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                cliente.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        set.getString("nit"),
                        set.getString("nombre_usuario"),
                        set.getString("email_usuario")
                });
            } else {
                throw new SQLException("Cliente no encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return cliente;
    }

    public List<String[]> list() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT c.*, u.nombre as nombre_usuario, u.email as email_usuario " +
                      "FROM cliente c " +
                      "LEFT JOIN user u ON c.user_id = u.id " +
                      "ORDER BY c.id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                clientes.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        set.getString("nit"),
                        set.getString("nombre_usuario"),
                        set.getString("email_usuario")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando clientes: " + e.getMessage());
            throw e;
        }
        
        System.out.println("👥 Total clientes encontrados: " + clientes.size());
        return clientes;
    }

    public void disconnect() {
        connection.closeConnection();
    }
} 