package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DPatrocinador {

    public static final String[] HEADERS = {"id", "nombre", "descripcion", "email", "telefono"};

    private final SqlConnection connection;

    public DPatrocinador() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String query = "SELECT * FROM patrocinadores WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.add(new String[] {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getString("email"),
                        rs.getString("telefono")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener datos del patrocinador: " + e.getMessage());
            throw e;
        }
        return result;  // Devuelve una lista con un solo elemento o vacía
    }


    public List<String[]> save(String nombre, String descripcion, String email, String telefono) throws SQLException {
        String query = "INSERT INTO patrocinadores (nombre, descripcion, email, telefono) VALUES (?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setString(3, email);
        ps.setString(4, telefono);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el patrocinador recién insertado.
        } else {
            throw new SQLException("Error al insertar patrocinador. No se pudo recuperar el ID del patrocinador.");
        }
    }


    public List<String[]> update(int id, String nombre, String descripcion, String email, String telefono) throws SQLException {
        String query = "UPDATE patrocinadores SET nombre = ?, descripcion = ?, email = ?, telefono = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setString(3, email);
        ps.setString(4, telefono);
        ps.setInt(5, id);

        if (ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el patrocinador");
            throw new SQLException("Error al modificar el patrocinador");
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM patrocinadores WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        ps.executeUpdate();
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> patrocinadores = new ArrayList<>();
        String query = "SELECT * FROM patrocinadores";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            patrocinadores.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    set.getString("email"),
                    set.getString("telefono")
            });
        }
        return patrocinadores;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
