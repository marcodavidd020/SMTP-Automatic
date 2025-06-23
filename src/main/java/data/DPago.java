package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DPago {

    public static final String[] HEADERS = {"id", "monto", "fecha", "metodo_pago", "reserva_id"};

    private final SqlConnection connection;

    public DPago() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DPago(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DPago createWithGlobalConfig() {
        return new DPago(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> pago = new ArrayList<>();
        String query = "SELECT * FROM pagos WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            pago.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    String.valueOf(set.getFloat("monto")),
                    set.getDate("fecha").toString(),
                    set.getString("metodo_pago"),
                    String.valueOf(set.getInt("reserva_id"))
            });
        }
        return pago;
    }

    public List<String[]> save(float monto, java.sql.Date fecha, String metodo_pago, int reserva_id) throws SQLException {
        String query = "INSERT INTO pagos (monto, fecha, metodo_pago, reserva_id) VALUES (?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setFloat(1, monto);
        ps.setDate(2, fecha);
        ps.setString(3, metodo_pago);
        ps.setInt(4, reserva_id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el pago recién insertado.
        } else {
            throw new SQLException("Error al insertar pago. No se pudo recuperar el ID del pago.");
        }
    }

    public List<String[]> update(int id, float monto) throws SQLException {
        String query = "UPDATE pagos SET monto = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setFloat(1, monto);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al actualizar pago");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM pagos WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar pago");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT * FROM pagos";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            pagos.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    String.valueOf(set.getFloat("monto")),
                    set.getDate("fecha").toString(),
                    set.getString("metodo_pago"),
                    String.valueOf(set.getInt("reserva_id"))
            });
        }
        return pagos;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
