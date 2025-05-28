package data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DProveedor {

    public static final String[] HEADERS = {"id", "nombre", "telefono", "email", "direccion"};

    private final SqlConnection connection;

    public DProveedor() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> proveedor = new ArrayList<>();
        String query = "SELECT * FROM proveedores WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);

        ResultSet set = ps.executeQuery();
        if(set.next()) {
            proveedor.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("telefono"),
                    set.getString("email"),
                    set.getString("direccion")
            });
        }
        return proveedor;
    }

    public List<String[]> save(String nombre, String telefono, String email, String direccion) throws SQLException {
        String query = "INSERT INTO proveedores (nombre, telefono, email, direccion) VALUES (?, ?, ?, ?) RETURNING id";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, telefono);
        ps.setString(3, email);
        ps.setString(4, direccion);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int id = rs.getInt(1);
            return get(id);  // Utiliza el método 'get' para recuperar el proveedor recién insertado.
        } else {
            throw new SQLException("Error al insertar proveedor. No se pudo recuperar el ID del proveedor.");
        }

    }

    public List<String[]> update(int id, String nombre, String telefono, String email, String direccion) throws SQLException {
        String query = "UPDATE proveedores SET nombre = ?, telefono = ?, email = ?, direccion = ? WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setString(1, nombre);
        ps.setString(2, telefono);
        ps.setString(3, email);
        ps.setString(4, direccion);
        ps.setInt(5, id);

        if(ps.executeUpdate() == 0) {
            System.err.println("Error al modificar el proveedor");
            throw new SQLException();
        }
        return get(id);
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM proveedores WHERE id = ?";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ps.setInt(1, id);
        if(ps.executeUpdate() == 0) {
            System.err.println("Error al eliminar proveedor");
            throw new SQLException();
        }
        return list();
    }

    public List<String[]> list() throws SQLException {
        List<String[]> proveedores = new ArrayList<>();
        String query = "SELECT * FROM proveedores";
        PreparedStatement ps = connection.connect().prepareStatement(query);
        ResultSet set = ps.executeQuery();
        while (set.next()) {
            proveedores.add(new String[]{
                    String.valueOf(set.getInt("id")),
                    set.getString("nombre"),
                    set.getString("telefono"),
                    set.getString("email"),
                    set.getString("direccion")
            });
        }
        return proveedores;
    }

    public void disconnect() {
        connection.closeConnection();
    }
}
