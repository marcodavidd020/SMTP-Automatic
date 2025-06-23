package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DServicio {

    public static final String[] HEADERS = {"id", "nombre", "descripcion"};

    private final SqlConnection connection;

    public DServicio() {
        // this.connection = new SqlConnection("grupo19sa", "mail.tecnoweb.org.bo", "5432", "grup019grup019", "db_grupo19sa");
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DServicio(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DServicio createWithGlobalConfig() {
        return new DServicio(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> servicio = new ArrayList<>();
        String query = "SELECT * FROM servicios WHERE id = ?";
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                servicio.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos del servicio: " + e.getMessage());
            throw e;
        }
        return servicio;
    }

    public List<String[]> save(String nombre, String descripcion) throws SQLException {
        String query = "INSERT INTO servicios (nombre, descripcion) VALUES (?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, descripcion);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el servicio recién insertado.
        } else {
            throw new SQLException("Error al insertar servicio. No se pudo recuperar el ID del servicio.");
        }
    }

    public List<String[]> update(int id, String nombre) throws SQLException {
        String query = "UPDATE servicios SET nombre = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al actualizar servicio");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM servicios WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar servicio");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> servicios = new ArrayList<>();
        String query = "SELECT * FROM servicios";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            servicios.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion")
            });
        }
        return servicios;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
