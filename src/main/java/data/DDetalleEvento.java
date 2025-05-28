package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DDetalleEvento {

    public static final String[] HEADERS = {"evento_id", "servicio_id", "costo_servicio"};

    private final SqlConnection connection;

    public DDetalleEvento() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int evento_id, int servicio_id) throws SQLException {
        List<String[]> detalleEvento = new ArrayList<>();
        String query = "SELECT * FROM detalle_eventos WHERE evento_id = ? AND servicio_id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, evento_id);
        ps.setInt(2, servicio_id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            detalleEvento.add(new String[]{
                    String.valueOf(set.getInt("evento_id")),
                    String.valueOf(set.getInt("servicio_id")),
                    String.valueOf(set.getFloat("costo_servicio"))
            });
        }
        return detalleEvento;
    }

    public List<String[]> save(int evento_id, int servicio_id, float costo_servicio) throws SQLException {
        String query = "INSERT INTO detalle_eventos (evento_id, servicio_id, costo_servicio) VALUES (?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, evento_id);
        ps.setInt(2, servicio_id);
        ps.setFloat(3, costo_servicio);

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return get(evento_id, servicio_id);
        } else {
            throw new SQLException("Error al insertar detalle del evento. No se pudo recuperar el ID del detalle del evento.");
        }

    }

    public List<String[]> update(int evento_id, int servicio_id, float costo_servicio) throws SQLException {
        String query = "UPDATE detalle_eventos SET costo_servicio = ? WHERE evento_id = ? AND servicio_id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setFloat(1, costo_servicio);
        ps.setInt(2, evento_id);
        ps.setInt(3, servicio_id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al actualizar detalle del evento");
            throw new SQLException();
        }

        return get(evento_id, servicio_id);
    }

    public List<String[]> delete(int evento_id, int servicio_id) throws SQLException {
        String query = "DELETE FROM detalle_eventos WHERE evento_id = ? AND servicio_id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, evento_id);
        ps.setInt(2, servicio_id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar detalle del evento");
            throw new SQLException();
        }

        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> detalles = new ArrayList<>();
        String query = "SELECT * FROM detalle_eventos";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            detalles.add(new String[]{
                    String.valueOf(set.getInt("evento_id")),
                    String.valueOf(set.getInt("servicio_id")),
                    String.valueOf(set.getFloat("costo_servicio"))
            });
        }
        return detalles;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
