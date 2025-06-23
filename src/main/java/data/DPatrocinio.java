package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DPatrocinio {

    public static final String[] HEADERS = {"id", "aporte", "patrocinador_id", "evento_id"};

    private final SqlConnection connection;

    public DPatrocinio() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DPatrocinio(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DPatrocinio createWithGlobalConfig() {
        return new DPatrocinio(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> patrocinio = new ArrayList<>();
        String query = "SELECT * FROM patrocinios WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            patrocinio.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    String.valueOf(set.getFloat("aporte")),
                    String.valueOf(set.getInt("patrocinador_id")),
                    String.valueOf(set.getInt("evento_id"))
            });
        }
        return patrocinio;
    }

    public List<String[]> save(float aporte, int patrocinador_id, int evento_id) throws SQLException {
        String query = "INSERT INTO patrocinios (aporte, patrocinador_id, evento_id) VALUES (?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setFloat(1, aporte);
        ps.setInt(2, patrocinador_id);
        ps.setInt(3, evento_id);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el patrocinio recién insertado.
        } else {
            throw new SQLException("Error al insertar patrocinio. No se pudo recuperar el ID del patrocinio.");
        }
    }

    public List<String[]> update(int id, float aporte) throws SQLException {
        String query = "UPDATE patrocinios SET aporte = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setFloat(1, aporte);
        ps.setInt(2, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el patrocinio");
            throw new SQLException();
        }
        return get(id);
    }

    public  List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM patrocinios WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar patrocinio");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> patrocinios = new ArrayList<>();
        String query = "SELECT * FROM patrocinios";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            patrocinios.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    String.valueOf(set.getFloat("aporte")),
                    String.valueOf(set.getInt("patrocinador_id")),
                    String.valueOf(set.getInt("evento_id"))
            });
        }
        return patrocinios;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
