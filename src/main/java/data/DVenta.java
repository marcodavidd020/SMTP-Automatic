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
 * DAO para manejar operaciones de ventas y checkout
 * @author Jairo
 */
public class DVenta {
    
    public static final String[] HEADERS = {"ID", "Fecha", "Total", "Estado", "Cliente"};
    
    private Connection connection;
    
    public DVenta() {
        try {
            this.connection = DriverManager.getConnection(DBConnectionManager.getUrl(), DBConnectionManager.getUser(), DBConnectionManager.getPassword());
        } catch (SQLException e) {
            System.err.println("Error conectando a la base de datos en DVenta: " + e.getMessage());
        }
    }
    
    private DVenta(Connection customConnection) {
        this.connection = customConnection;
    }
    
    public static DVenta createWithGlobalConfig() {
        try {
            Connection conn = DriverManager.getConnection(
                DBConnectionManager.getUrl(), 
                DBConnectionManager.getUser(), 
                DBConnectionManager.getPassword()
            );
            return new DVenta(conn);
        } catch (SQLException e) {
            System.err.println("Error creando DVenta con configuración global: " + e.getMessage());
            return new DVenta(); // Fallback a local
        }
    }
    
    /**
     * Procesa el checkout del carrito y crea una nota de venta
     */
    public int procesarCheckout(int clienteId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // 1. Verificar que el cliente tenga un carrito activo
            int carritoId = obtenerCarritoActivo(clienteId);
            if (carritoId == 0) {
                throw new SQLException("No hay carrito activo para el cliente");
            }
            
            // 2. Verificar que el carrito no esté vacío
            if (carritoVacio(carritoId)) {
                throw new SQLException("El carrito está vacío");
            }
            
            // 3. Verificar stock de todos los productos
            verificarStockCarrito(carritoId);
            
            // 4. Calcular total del carrito
            double total = calcularTotalCarrito(carritoId);
            
            // 5. Crear nota de venta
            String sqlVenta = "INSERT INTO NotaVenta (fecha, total, estado, cliente_id) VALUES (CURRENT_DATE, ?, 'pendiente', ?) RETURNING id";
            int ventaId;
            try (PreparedStatement stmt = connection.prepareStatement(sqlVenta)) {
                stmt.setDouble(1, total);
                stmt.setInt(2, clienteId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    ventaId = rs.getInt("id");
                } else {
                    throw new SQLException("Error creando nota de venta");
                }
            }
            
            // 6. Copiar detalles del carrito a detalle de venta
            copiarDetallesCarritoAVenta(carritoId, ventaId);
            
            // 7. Marcar carrito como procesado
            marcarCarritoProcesado(carritoId);
            
            connection.commit();
            System.out.println("✅ Checkout procesado exitosamente. Nota de venta ID: " + ventaId);
            
            return ventaId;
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Completa la venta con método de pago y actualiza stock
     */
    public boolean completarVenta(int ventaId, int tipoPagoId, int clienteId) throws SQLException {
        connection.setAutoCommit(false);
        
        try {
            // 1. Verificar que la venta existe y está pendiente
            if (!ventaExisteYPendiente(ventaId, clienteId)) {
                throw new SQLException("Venta no encontrada o ya procesada");
            }
            
            // 2. Crear registro de pago
            int pagoId = crearPago(tipoPagoId, ventaId);
            
            // 3. Actualizar stock de productos
            actualizarStockProductos(ventaId);
            
            // 4. Marcar venta como completada
            String sqlActualizar = "UPDATE NotaVenta SET estado = 'completada' WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(sqlActualizar)) {
                stmt.setInt(1, ventaId);
                stmt.executeUpdate();
            }
            
            connection.commit();
            System.out.println("✅ Venta completada exitosamente. Pago ID: " + pagoId);
            
            return true;
            
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    /**
     * Obtiene el historial de ventas del cliente
     */
    public List<String[]> obtenerHistorialVentas(int clienteId) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        
        String sql = """
            SELECT nv.id, nv.fecha, nv.total, nv.estado, 
                   CASE WHEN p.id IS NOT NULL THEN tp.tipo_pago ELSE 'Sin pago' END as metodo_pago
            FROM NotaVenta nv
            LEFT JOIN pago p ON nv.id = p.nota_venta_id
            LEFT JOIN tipos_pago tp ON p.tipo_pago_id = tp.id
            WHERE nv.cliente_id = ?
            ORDER BY nv.fecha DESC, nv.id DESC
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                resultado.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getDate("fecha").toString(),
                    String.format("$%.2f", rs.getDouble("total")),
                    rs.getString("estado"),
                    rs.getString("metodo_pago")
                });
            }
        }
        
        return resultado;
    }
    
    /**
     * Obtiene los detalles de una venta específica
     */
    public List<String[]> obtenerDetalleVenta(int ventaId, int clienteId) throws SQLException {
        List<String[]> resultado = new ArrayList<>();
        
        String sql = """
            SELECT p.nombre, dv.cantidad, dv.total / dv.cantidad as precio_unitario, dv.total
            FROM Detalle_Venta dv
            JOIN productos p ON dv.producto_id = p.id
            JOIN NotaVenta nv ON dv.nota_venta_id = nv.id
            WHERE nv.id = ? AND nv.cliente_id = ?
            ORDER BY p.nombre
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.setInt(2, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                resultado.add(new String[]{
                    rs.getString("nombre"),
                    String.valueOf(rs.getInt("cantidad")),
                    String.format("$%.2f", rs.getDouble("precio_unitario")),
                    String.format("$%.2f", rs.getDouble("total"))
                });
            }
        }
        
        return resultado;
    }
    
    // Métodos auxiliares
    
    private int obtenerCarritoActivo(int clienteId) throws SQLException {
        String sql = "SELECT id FROM Carrito WHERE cliente_id = ? AND estado = 'activo'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return 0;
    }
    
    private boolean carritoVacio(int carritoId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Detalle_carrito WHERE carrito_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        }
        return true;
    }
    
    private void verificarStockCarrito(int carritoId) throws SQLException {
        String sql = """
            SELECT dc.producto_id, dc.cantidad, pa.stock, p.nombre
            FROM Detalle_carrito dc
            JOIN ProductoAlmacen pa ON dc.producto_id = pa.producto_id
            JOIN productos p ON dc.producto_id = p.id
            WHERE dc.carrito_id = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int cantidad = rs.getInt("cantidad");
                int stock = rs.getInt("stock");
                String nombre = rs.getString("nombre");
                
                if (cantidad > stock) {
                    throw new SQLException("Stock insuficiente para " + nombre + ". Disponible: " + stock + ", Solicitado: " + cantidad);
                }
            }
        }
    }
    
    private double calcularTotalCarrito(int carritoId) throws SQLException {
        String sql = "SELECT total FROM Carrito WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        }
        return 0.0;
    }
    
    private void copiarDetallesCarritoAVenta(int carritoId, int ventaId) throws SQLException {
        String sql = """
            INSERT INTO Detalle_Venta (nota_venta_id, producto_id, cantidad, total)
            SELECT ?, producto_id, cantidad, total
            FROM Detalle_carrito
            WHERE carrito_id = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.setInt(2, carritoId);
            stmt.executeUpdate();
        }
    }
    
    private void marcarCarritoProcesado(int carritoId) throws SQLException {
        String sql = "UPDATE Carrito SET estado = 'procesado' WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, carritoId);
            stmt.executeUpdate();
        }
    }
    
    private boolean ventaExisteYPendiente(int ventaId, int clienteId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM NotaVenta WHERE id = ? AND cliente_id = ? AND estado = 'pendiente'";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.setInt(2, clienteId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    private int crearPago(int tipoPagoId, int ventaId) throws SQLException {
        String sql = "INSERT INTO pago (fechapago, estado, pago_facil_id, tipo_pago_id, nota_venta_id) VALUES (CURRENT_DATE, 'completado', NULL, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, tipoPagoId);
            stmt.setInt(2, ventaId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Error creando pago");
    }
    
    private void actualizarStockProductos(int ventaId) throws SQLException {
        String sql = """
            UPDATE ProductoAlmacen 
            SET stock = stock - dv.cantidad
            FROM Detalle_Venta dv
            WHERE ProductoAlmacen.producto_id = dv.producto_id 
            AND dv.nota_venta_id = ?
        """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            int affected = stmt.executeUpdate();
            System.out.println("📦 Stock actualizado para " + affected + " productos");
        }
    }
} 