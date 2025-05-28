package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DPromocion {

    public static final String[] HEADERS = {"id", "descripcion", "descuento", "fecha_inicio", "fecha_fin", "proveedor_id"};

    private final SqlConnection connection;

    public DPromocion() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> promocion = new ArrayList<>();
        String query = "SELECT * FROM promociones WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            promocion.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("descripcion"),
                    String.valueOf(set.getInt("descuento")),
                    set.getDate("fecha_inicio").toString(),
                    set.getDate("fecha_fin").toString(),
                    String.valueOf(set.getInt("proveedor_id"))
            });
        }
        return promocion;
    }

    public List<String[]> save(String descripcion, int descuento, java.sql.Date fecha_inicio,java.sql.Date fecha_fin, int proveedor_id) throws SQLException {
        String query = "INSERT INTO promociones (descripcion, descuento, fecha_inicio, fecha_fin, proveedor_id) VALUES (?, ?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, descripcion);
        ps.setInt(2, descuento);
        ps.setDate(3, fecha_inicio);
        ps.setDate(4, fecha_fin);
        ps.setInt(5, proveedor_id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar la promoción recién insertada.
        } else {
            throw new SQLException("Error al insertar promoción. No se pudo recuperar el ID de la promoción.");
        }
    }

    public List<String[]>  update(int id, int descuento) throws SQLException {
        String query = "UPDATE promociones SET descuento = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, descuento);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar la promoción");
            throw new SQLException();
        }

        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM promociones WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar promoción");
            throw new SQLException();
        }

        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "SELECT * FROM promociones";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            promociones.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("descripcion"),
                    String.valueOf(set.getInt("descuento")),
                    set.getDate("fecha_inicio").toString(),
                    set.getDate("fecha_fin").toString(),
                    String.valueOf(set.getInt("proveedor_id"))
            });
        }
        return promociones;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
