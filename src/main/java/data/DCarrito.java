package data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;

/**
 * DAO para manejar operaciones del carrito de compras
 * 
 * @author MARCO
 */
public class DCarrito {

    public static final String[] HEADERS = { "ID", "Cliente ID", "Fecha", "Total", "Estado" };
    public static final String[] DETALLE_HEADERS = { "Producto", "Cantidad", "Precio Unit.", "Subtotal" };

    private Connection connection;

    public DCarrito() {
        try {
            this.connection = DriverManager.getConnection(DBConnection.url, DBConnection.user, DBConnection.password);
            this.connection.setAutoCommit(true);
            System.out.println("🗄️ DCarrito: Conexión establecida con autocommit habilitado");
        } catch (SQLException e) {
            System.err.println("Error conectando a la base de datos en DCarrito: " + e.getMessage());
        }
    }
    
    private DCarrito(Connection customConnection) {
        this.connection = customConnection;
        try {
            this.connection.setAutoCommit(true);
            System.out.println("🗄️ DCarrito: Conexión global establecida con autocommit habilitado");
        } catch (SQLException e) {
            System.err.println("Error configurando conexión en DCarrito: " + e.getMessage());
        }
    }
    
    public static DCarrito createWithGlobalConfig() {
        try {
            Connection conn = DriverManager.getConnection(
                DBConnectionManager.getUrl(), 
                DBConnectionManager.getUser(), 
                DBConnectionManager.getPassword()
            );
            return new DCarrito(conn);
        } catch (SQLException e) {
            System.err.println("Error creando DCarrito con configuración global: " + e.getMessage());
            return new DCarrito(); // Fallback a local
        }
    }

    /**
     * Crea un carrito para un cliente si no existe
     */
    public int crearCarrito(int clienteId) throws SQLException {
        System.out.println("🔍 DCarrito: Verificando carrito activo para cliente " + clienteId);
        
        // Verificar si ya tiene un carrito activo
        String checkSql = "SELECT id FROM carritos WHERE cliente_id = ? AND estado = 'activo'";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, clienteId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int carritoId = rs.getInt("id");
                System.out.println("✅ DCarrito: Carrito activo encontrado: " + carritoId);
                return carritoId; // Ya tiene carrito activo
            }
        }

        System.out.println("🆕 DCarrito: No hay carrito activo, creando nuevo carrito para cliente " + clienteId);
        
        // Crear nuevo carrito
        String sql = "INSERT INTO carritos (cliente_id, fecha, total, estado) VALUES (?, CURRENT_DATE, 0, 'activo') RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int nuevoCarritoId = rs.getInt("id");
                System.out.println("✅ DCarrito: Nuevo carrito creado con ID: " + nuevoCarritoId);
                return nuevoCarritoId;
            }
            throw new SQLException("Error creando carrito");
        }
    }

    /**
     * Agrega un producto al carrito
     */
    public boolean agregarProducto(int clienteId, int productoId, int cantidad) throws SQLException {
        System.out.println("🛒 DCarrito: Agregando producto " + productoId + " (cantidad: " + cantidad + ") para cliente " + clienteId);
        
        // Obtener o crear carrito
        int carritoId = crearCarrito(clienteId);
        System.out.println("🛒 DCarrito: Carrito ID obtenido/creado: " + carritoId);

        // Verificar stock disponible
        if (!verificarStock(productoId, cantidad)) {
            System.out.println("❌ DCarrito: Stock insuficiente para producto " + productoId);
            throw new SQLException("Stock insuficiente para el producto ID: " + productoId);
        }
        System.out.println("✅ DCarrito: Stock verificado correctamente");

        // Verificar si el producto ya está en el carrito
        String checkSql = "SELECT cantidad FROM detalle_carritos WHERE carrito_id = ? AND producto_id = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, carritoId);
            checkStmt.setInt(2, productoId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                // Actualizar cantidad existente
                int cantidadActual = rs.getInt("cantidad");
                System.out.println("🔄 DCarrito: Producto ya existe, actualizando cantidad de " + cantidadActual + " a " + (cantidadActual + cantidad));
                return actualizarCantidadProducto(carritoId, productoId, cantidadActual + cantidad);
            }
        }

        // Obtener precio del producto
        double precio = obtenerPrecioProducto(productoId);
        double total = precio * cantidad;
        System.out.println("💰 DCarrito: Precio unitario: $" + precio + ", Total: $" + total);

        // Insertar nuevo detalle
        String sql = "INSERT INTO detalle_carritos (carrito_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            stmt.setInt(2, productoId);
            stmt.setInt(3, cantidad);
            stmt.setDouble(4, precio);
            stmt.setDouble(5, total);

            int affected = stmt.executeUpdate();
            System.out.println("📝 DCarrito: Filas afectadas en detalle_carritos: " + affected);
            
            if (affected > 0) {
                actualizarTotalCarrito(carritoId);
                System.out.println("✅ DCarrito: Producto agregado exitosamente al carrito " + carritoId);
                return true;
            }
        }
        System.out.println("❌ DCarrito: No se pudo agregar el producto");
        return false;
    }

    /**
     * Obtiene el contenido del carrito del cliente
     */
    public List<String[]> obtenerCarrito(int clienteId) throws SQLException {
        List<String[]> resultado = new ArrayList<>();

        String sql = """
                    SELECT p.nombre, dc.cantidad, p.precio_venta, dc.subtotal, dc.producto_id
                    FROM carritos c
                    JOIN detalle_carritos dc ON c.id = dc.carrito_id
                    JOIN productos p ON dc.producto_id = p.id
                    WHERE c.cliente_id = ? AND c.estado = 'activo'
                    ORDER BY p.nombre
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                resultado.add(new String[] {
                        rs.getString("nombre"),
                        String.valueOf(rs.getInt("cantidad")),
                        String.format("$%.2f", rs.getDouble("precio_venta")),
                        String.format("$%.2f", rs.getDouble("subtotal"))
                });
            }
        }

        return resultado;
    }

    /**
     * Obtiene el total del carrito
     */
    public double obtenerTotalCarrito(int clienteId) throws SQLException {
        String sql = "SELECT total FROM carritos WHERE cliente_id = ? AND estado = 'activo'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }

    /**
     * Remueve un producto del carrito
     */
    public boolean removerProducto(int clienteId, int productoId) throws SQLException {
        String sql = """
                    DELETE FROM detalle_carritos
                    WHERE carrito_id = (SELECT id FROM carritos WHERE cliente_id = ? AND estado = 'activo')
                    AND producto_id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            stmt.setInt(2, productoId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                // Actualizar total del carrito
                int carritoId = obtenerCarritoId(clienteId);
                if (carritoId > 0) {
                    actualizarTotalCarrito(carritoId);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Vacía el carrito del cliente
     */
    public boolean vaciarCarrito(int clienteId) throws SQLException {
        String sql = """
                    DELETE FROM detalle_carritos
                    WHERE carrito_id = (SELECT id FROM carritos WHERE cliente_id = ? AND estado = 'activo')
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            int affected = stmt.executeUpdate();

            if (affected > 0) {
                // Actualizar total a 0
                int carritoId = obtenerCarritoId(clienteId);
                if (carritoId > 0) {
                    actualizarTotalCarrito(carritoId);
                }
                return true;
            }
        }
        return false;
    }

    // Métodos auxiliares

    private boolean verificarStock(int productoId, int cantidadSolicitada) throws SQLException {
        String sql = "SELECT stock FROM ProductoAlmacen WHERE producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("stock") >= cantidadSolicitada;
            }
        }
        return false;
    }

    private double obtenerPrecioProducto(int productoId) throws SQLException {
        String sql = "SELECT precio_venta FROM productos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("precio_venta");
            }
        }
        throw new SQLException("Producto no encontrado: " + productoId);
    }

    private boolean actualizarCantidadProducto(int carritoId, int productoId, int nuevaCantidad) throws SQLException {
        // Verificar stock
        if (!verificarStock(productoId, nuevaCantidad)) {
            throw new SQLException("Stock insuficiente para la cantidad solicitada");
        }

        double precio = obtenerPrecioProducto(productoId);
        double nuevoTotal = precio * nuevaCantidad;

        String sql = "UPDATE detalle_carritos SET cantidad = ?, subtotal = ? WHERE carrito_id = ? AND producto_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, nuevaCantidad);
            stmt.setDouble(2, nuevoTotal);
            stmt.setInt(3, carritoId);
            stmt.setInt(4, productoId);

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                actualizarTotalCarrito(carritoId);
                return true;
            }
        }
        return false;
    }

    private void actualizarTotalCarrito(int carritoId) throws SQLException {
        String sql = "UPDATE carritos SET total = (SELECT COALESCE(SUM(subtotal), 0) FROM detalle_carritos WHERE carrito_id = ?) WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            stmt.setInt(2, carritoId);
            stmt.executeUpdate();
        }
    }

    private int obtenerCarritoId(int clienteId) throws SQLException {
        String sql = "SELECT id FROM carritos WHERE cliente_id = ? AND estado = 'activo'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }
}