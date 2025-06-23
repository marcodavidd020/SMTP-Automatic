package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DProducto {

    public static final String[] HEADERS = {"id", "cod_producto", "nombre", "precio_compra", "precio_venta", "imagen", "descripcion", "categoria"};

    private final SqlConnection connection;

    public DProducto() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    // Constructor privado para uso con configuración global
    private DProducto(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    // Constructor que usa configuración global del manager
    public static DProducto createWithGlobalConfig() {
        return new DProducto(DBConnectionManager.createConnection());
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> producto = new ArrayList<>();
        String query = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                      "LEFT JOIN categorias c ON p.categoria_id = c.id WHERE p.id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                producto.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("cod_producto"),
                        set.getString("nombre"),
                        String.valueOf(set.getBigDecimal("precio_compra")),
                        String.valueOf(set.getBigDecimal("precio_venta")),
                        set.getString("imagen"),
                        set.getString("descripcion"),
                        set.getString("categoria_nombre") != null ? set.getString("categoria_nombre") : "Sin categoría"
                });
            } else {
                throw new SQLException("Producto no encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return producto;
    }

    public List<String[]> list() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                      "LEFT JOIN categorias c ON p.categoria_id = c.id ORDER BY p.id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                productos.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("cod_producto"),
                        set.getString("nombre"),
                        String.valueOf(set.getBigDecimal("precio_compra")),
                        String.valueOf(set.getBigDecimal("precio_venta")),
                        set.getString("imagen"),
                        set.getString("descripcion"),
                        set.getString("categoria_nombre") != null ? set.getString("categoria_nombre") : "Sin categoría"
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando productos: " + e.getMessage());
            throw e;
        }
        
        System.out.println("📦 Total productos encontrados: " + productos.size());
        return productos;
    }

    public List<String[]> listByCategory(int categoriaId) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.*, c.nombre as categoria_nombre FROM productos p " +
                      "LEFT JOIN categorias c ON p.categoria_id = c.id " +
                      "WHERE p.categoria_id = ? ORDER BY p.nombre";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, categoriaId);
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                productos.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("cod_producto"),
                        set.getString("nombre"),
                        String.valueOf(set.getBigDecimal("precio_compra")),
                        String.valueOf(set.getBigDecimal("precio_venta")),
                        set.getString("imagen"),
                        set.getString("descripcion"),
                        set.getString("categoria_nombre") != null ? set.getString("categoria_nombre") : "Sin categoría"
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando productos por categoría: " + e.getMessage());
            throw e;
        }
        
        return productos;
    }

    public void disconnect() {
        connection.closeConnection();
    }
} 