package data;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DPedido {

    public static final String[] HEADERS = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega"};

    private final SqlConnection connection;

    public DPedido() {
        this.connection = new SqlConnection(DBConnectionManager.getDatabase(), DBConnectionManager.getServer(), DBConnectionManager.getPort(), DBConnectionManager.getUser(), DBConnectionManager.getPassword());
    }
    
    private DPedido(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DPedido createWithGlobalConfig() {
        return new DPedido(DBConnectionManager.createConnection());
    }

    /**
     * Crea un nuevo pedido vinculado a una dirección
     */
    public int add(int direccionId, double total, String estado) {
        String sql = "INSERT INTO \"pedido\" (\"direccion_id\", \"fecha\", \"total\", \"estado\") VALUES (?, ?, ?, ?) RETURNING \"id\"";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, direccionId);
            stmt.setDate(2, Date.valueOf(LocalDate.now()));
            stmt.setDouble(3, total);
            stmt.setString(4, estado);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ Pedido creado con ID: " + id);
                System.out.println("   💰 Total: $" + total + " - Estado: " + estado);
                return id;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error insertando pedido: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Crea un pedido completo desde el carrito del cliente
     */
    public int crearPedidoDesdeCarrito(int clienteId, int direccionId) {
        try {
            // 1. Obtener total del carrito
            DCarrito dCarrito = new DCarrito();
            double total = dCarrito.obtenerTotalCarrito(clienteId);
            
            if (total <= 0) {
                System.err.println("❌ El carrito está vacío o no tiene productos válidos");
                return -1;
            }

            // 2. Crear el pedido
            int pedidoId = add(direccionId, total, "pendiente");
            if (pedidoId <= 0) {
                return -1;
            }

            // 3. Vincular el carrito al pedido
            if (!dCarrito.vincularCarritoAPedido(clienteId, pedidoId)) {
                System.err.println("❌ Error vinculando carrito al pedido");
                return -1;
            }

            // 4. Crear nota de venta
            DVenta dVenta = new DVenta();
            int notaVentaId = dVenta.crearNotaVentaDesdePedido(pedidoId, total);
            if (notaVentaId <= 0) {
                System.err.println("❌ Error creando nota de venta");
                return -1;
            }

            // 5. Descontar stock del inventario
            if (!descontarStockDesdeCarrito(clienteId)) {
                System.err.println("❌ Error descontando stock");
                return -1;
            }

            System.out.println("🎉 Pedido completado exitosamente:");
            System.out.println("   📦 Pedido ID: " + pedidoId);
            System.out.println("   🧾 Nota Venta ID: " + notaVentaId);
            System.out.println("   💰 Total: $" + total);
            
            return pedidoId;
            
        } catch (Exception e) {
            System.err.println("❌ Error creando pedido desde carrito: " + e.getMessage());
            return -1;
        }
    }

    /**
     * Descuenta el stock de productos basado en el carrito del cliente
     */
    private boolean descontarStockDesdeCarrito(int clienteId) {
        try {
            DCarrito dCarrito = new DCarrito();
            List<String[]> carritoItems = dCarrito.obtenerCarrito(clienteId);
            
            String sqlUpdate = "UPDATE \"producto_inventario\" SET \"stock\" = \"stock\" - ? WHERE \"producto_id\" = ? AND \"almacen_id\" = 4";
            String sqlCheck = "SELECT \"stock\" FROM \"producto_inventario\" WHERE \"producto_id\" = ? AND \"almacen_id\" = 4";
            
            try (Connection conn = connection.connect()) {
                conn.setAutoCommit(false); // Transacción
                
                for (String[] item : carritoItems) {
                    // Usar el ID del producto que ahora está en el índice 4
                    int productoId = Integer.parseInt(item[4]); // ID del producto
                    int cantidad = Integer.parseInt(item[1]);   // Cantidad
                    
                    System.out.println("🔄 Descontando stock: Producto '" + item[0] + "' (ID: " + productoId + ") - Cantidad: " + cantidad);
                    
                    // Verificar stock disponible
                    try (PreparedStatement checkStmt = conn.prepareStatement(sqlCheck)) {
                        checkStmt.setInt(1, productoId);
                        ResultSet rs = checkStmt.executeQuery();
                        
                        if (rs.next()) {
                            int stockActual = rs.getInt("stock");
                            if (stockActual < cantidad) {
                                System.err.println("❌ Stock insuficiente para producto '" + item[0] + "' (ID: " + productoId + 
                                    ") (disponible: " + stockActual + ", requerido: " + cantidad + ")");
                                conn.rollback();
                                return false;
                            }
                        } else {
                            System.err.println("❌ Producto '" + item[0] + "' (ID: " + productoId + ") no encontrado en inventario");
                            conn.rollback();
                            return false;
                        }
                    }
                    
                    // Descontar stock
                    try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                        updateStmt.setInt(1, cantidad);
                        updateStmt.setInt(2, productoId);
                        updateStmt.executeUpdate();
                        
                        System.out.println("✅ Stock descontado: Producto '" + item[0] + "' (ID: " + productoId + ") - Cantidad: " + cantidad);
                    }
                }
                
                conn.commit();
                System.out.println("✅ Stock actualizado exitosamente para todos los productos");
                return true;
                
            } catch (SQLException e) {
                System.err.println("❌ Error actualizando stock: " + e.getMessage());
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error descontando stock: " + e.getMessage());
            return false;
        }
    }

    public List<String[]> get(int id) {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT \"id\", \"direccion_id\", \"fecha\", \"total\", \"estado\", \"fecha_hora_envio\", \"fecha_hora_entrega\" FROM \"pedido\" WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String[] row = {
                    rs.getString("id"),
                    rs.getString("direccion_id"),
                    rs.getString("fecha"),
                    rs.getString("total"),
                    rs.getString("estado"),
                    rs.getString("fecha_hora_envio"),
                    rs.getString("fecha_hora_entrega")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo pedido: " + e.getMessage());
        }
        return result;
    }

    public List<String[]> list() {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT \"id\", \"direccion_id\", \"fecha\", \"total\", \"estado\", \"fecha_hora_envio\", \"fecha_hora_entrega\" FROM \"pedido\" ORDER BY \"fecha\" DESC";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] row = {
                    rs.getString("id"),
                    rs.getString("direccion_id"),
                    rs.getString("fecha"),
                    rs.getString("total"),
                    rs.getString("estado"),
                    rs.getString("fecha_hora_envio"),
                    rs.getString("fecha_hora_entrega")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando pedidos: " + e.getMessage());
        }
        return result;
    }

    /**
     * Actualiza el estado de un pedido
     */
    public boolean actualizarEstado(int id, String nuevoEstado) {
        String sql = "UPDATE \"pedido\" SET \"estado\" = ? WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Estado del pedido " + id + " actualizado a: " + nuevoEstado);
                return true;
            } else {
                System.err.println("❌ No se encontró pedido con ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error actualizando estado del pedido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca un pedido como enviado
     */
    public boolean marcarComoEnviado(int id) {
        String sql = "UPDATE \"pedido\" SET \"estado\" = 'enviado', \"fecha_hora_envio\" = ? WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Pedido " + id + " marcado como enviado");
                return true;
            } else {
                System.err.println("❌ No se encontró pedido con ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error marcando pedido como enviado: " + e.getMessage());
            return false;
        }
    }

    /**
     * Marca un pedido como entregado
     */
    public boolean marcarComoEntregado(int id) {
        String sql = "UPDATE \"pedido\" SET \"estado\" = 'entregado', \"fecha_hora_entrega\" = ? WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(LocalDate.now()));
            stmt.setInt(2, id);
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("✅ Pedido " + id + " marcado como entregado");
                return true;
            } else {
                System.err.println("❌ No se encontró pedido con ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error marcando pedido como entregado: " + e.getMessage());
            return false;
        }
    }

    public boolean exists(int id) {
        String sql = "SELECT 1 FROM \"pedido\" WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error verificando pedido: " + e.getMessage());
            return false;
        }
    }
} 