package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DRol {

    public static final String[] HEADERS = {"id", "nombre"};

    private final SqlConnection connection;

    public DRol() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DRol(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DRol createWithGlobalConfig() {
        return new DRol(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> rol = null;
        String query = "SELECT * FROM roles WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rol = new ArrayList<>();
                rol.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos del rol: " + e.getMessage());
            throw e;
        }
        return rol;
    }

    public List<String[]> save(String nombre) throws SQLException {
        String query = "INSERT INTO roles (nombre) VALUES (?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el rol recién insertado.
        } else {
            throw new SQLException("Error al insertar rol. No se pudo recuperar el ID del rol.");
        }
    }

    public List<String[]> update(int id, String nombre) throws SQLException {
        String query = "UPDATE roles SET nombre = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el rol");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM roles WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar rol");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> roles = new ArrayList<>();
        String query = "SELECT * FROM roles";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            roles.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre")
            });
        }
        return roles;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
