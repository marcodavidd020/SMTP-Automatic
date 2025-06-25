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

public class DTipoPago {

    public static final String[] HEADERS = {"id", "tipo_pago", "created_at"};

    private final SqlConnection connection;

    public DTipoPago() {
        this.connection = new SqlConnection(DBConnectionManager.getDatabase(), DBConnectionManager.getServer(), DBConnectionManager.getPort(), DBConnectionManager.getUser(), DBConnectionManager.getPassword());
    }
    
    private DTipoPago(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DTipoPago createWithGlobalConfig() {
        return new DTipoPago(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> tipoPago = new ArrayList<>();
        String query = "SELECT * FROM tipos_pago WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                tipoPago.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("tipo_pago"),
                        set.getString("created_at")
                });
            } else {
                throw new SQLException("Tipo de pago no encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return tipoPago;
    }

    public List<String[]> list() throws SQLException {
        List<String[]> tiposPago = new ArrayList<>();
        String query = "SELECT * FROM tipos_pago ORDER BY tipo_pago";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                tiposPago.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("tipo_pago"),
                        set.getString("created_at")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando tipos de pago: " + e.getMessage());
            throw e;
        }
        
        System.out.println("💳 Total tipos de pago encontrados: " + tiposPago.size());
        return tiposPago;
    }

    public void disconnect() {
        connection.closeConnection();
    }
} 