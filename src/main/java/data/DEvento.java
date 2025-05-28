package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DEvento {

    public static final String[] HEADERS = {"id", "nombre", "descripcion", "capacidad", "precio_entrada", "fecha", "hora", "ubicacion", "estado", "imagen", "servicio_id"};

    private final SqlConnection connection;

    public DEvento() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> evento = new ArrayList<>();
        String query = "SELECT * FROM eventos WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            evento.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    String.valueOf(set.getInt("capacidad")),
                    String.valueOf(set.getFloat("precio_entrada")),
                    set.getDate("fecha").toString(),
                    set.getTime("hora").toString(),
                    set.getString("ubicacion"),
                    set.getString("estado"),
                    set.getString("imagen"),
                    String.valueOf(set.getInt("servicio_id"))
            });
        }
        return evento;
    }

    public List<String[]> save(String nombre, String descripcion, int capacidad, float precio_entrada, java.sql.Date fecha, java.sql.Time hora, String ubicacion, String estado, String imagen, int servicio_id) throws SQLException {
        String query = "INSERT INTO eventos (nombre, descripcion, capacidad, precio_entrada, fecha, hora, ubicacion, estado, imagen, servicio_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setInt(3, capacidad);
        ps.setFloat(4, precio_entrada);
        ps.setDate(5, fecha);
        ps.setTime(6, hora);
        ps.setString(7, ubicacion);
        ps.setString(8, estado);
        ps.setString(9, imagen);
        ps.setInt(10, servicio_id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el evento recién insertado.
        } else {
            throw new SQLException("Error al insertar evento. No se pudo recuperar el ID del evento.");
        }
    }

    public List<String[]> update(int id, String nombre, String descripcion, int capacidad, float precio_entrada) throws SQLException {
        String query = "UPDATE eventos SET nombre = ?, descripcion = ?, capacidad = ?, precio_entrada = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, descripcion);
        ps.setInt(3, capacidad);
        ps.setFloat(4, precio_entrada);
        ps.setInt(5, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el evento");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM eventos WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar evento");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> eventos = new ArrayList<>();
        String query = "SELECT * FROM eventos";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
        while (set.next()) {
            eventos.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("descripcion"),
                    String.valueOf(set.getInt("capacidad")),
                    String.valueOf(set.getFloat("precio_entrada")),
                    set.getDate("fecha").toString(),
                    set.getTime("hora").toString(),
                    set.getString("ubicacion"),
                    set.getString("estado"),
                    set.getString("imagen"),
                    String.valueOf(set.getInt("servicio_id"))
            });
        }
        } catch (SQLException e) {
            System.err.println("Error de conexión al intentar listar eventos.");
            throw e;
        }
        return eventos;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
