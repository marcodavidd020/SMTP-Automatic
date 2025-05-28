package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DReserva {

    public static final String[] HEADERS = {"id", "codigo", "fecha", "costo_entrada", "cantidad", "costo_total", "estado", "usuario_id", "evento_id"};

    private final SqlConnection connection;

    public DReserva() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> reserva = new ArrayList<>();
        String query = "SELECT * FROM reservas WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            reserva.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("codigo"),
                    set.getDate("fecha").toString(),
                    String.valueOf(set.getFloat("costo_entrada")),
                    String.valueOf(set.getInt("cantidad")),
                    String.valueOf(set.getFloat("costo_total")),
                    set.getString("estado"),
                    String.valueOf(set.getInt("usuario_id")),
                    String.valueOf(set.getInt("evento_id"))
            });
        }
        return reserva;
    }

    public List<String[]> save(String codigo, java.sql.Date fecha, float costo_entrada, int cantidad, float costo_total, String estado, int usuario_id, int evento_id) throws SQLException {
        String query = "INSERT INTO reservas (codigo, fecha, costo_entrada, cantidad, costo_total, estado, usuario_id, evento_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, codigo);
        ps.setDate(2, fecha);
        ps.setFloat(3, costo_entrada);
        ps.setInt(4, cantidad);
        ps.setFloat(5, costo_total);
        ps.setString(6, estado);
        ps.setInt(7, usuario_id);
        ps.setInt(8, evento_id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar la reserva recién insertada.
        } else {
            throw new SQLException("Error al insertar reserva. No se pudo recuperar el ID de la reserva.");
        }
    }

    public List<String[]> update(int id, String estado) throws SQLException {
        String query = "UPDATE reservas SET estado = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, estado);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar la reserva");
            throw new SQLException();
        }

        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM reservas WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar reserva");
            throw new SQLException();
        }

        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> reservas = new ArrayList<>();
        String query = "SELECT * FROM reservas";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            reservas.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("codigo"),
                    set.getDate("fecha").toString(),
                    String.valueOf(set.getFloat("costo_entrada")),
                    String.valueOf(set.getInt("cantidad")),
                    String.valueOf(set.getFloat("costo_total")),
                    set.getString("estado"),
                    String.valueOf(set.getInt("usuario_id")),
                    String.valueOf(set.getInt("evento_id"))
            });
        }
        return reservas;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
