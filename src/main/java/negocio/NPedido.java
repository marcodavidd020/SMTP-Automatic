package negocio;

import data.DPedido;
import data.DCarrito;
import data.DVenta;
import java.sql.SQLException;
import java.util.List;

public class NPedido {
    
    private DPedido dPedido;

    public NPedido() {
        this.dPedido = new DPedido();
    }

    /**
     * Lista todos los pedidos del sistema (solo para administradores)
     */
    public List<String[]> list() throws SQLException {
        return dPedido.list();
    }

    /**
     * Obtiene un pedido específico por ID
     */
    public List<String[]> get(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser mayor a 0");
        }
        return dPedido.get(id);
    }

    /**
     * Lista pedidos de un cliente específico (REGLA DE NEGOCIO)
     */
    public List<String[]> obtenerPedidosPorCliente(int clienteId) throws SQLException {
        if (clienteId <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a 0");
        }
        
        List<String[]> pedidos = dPedido.listByCliente(clienteId);
        
        if (pedidos == null || pedidos.isEmpty()) {
            System.out.println("📋 Cliente ID " + clienteId + " no tiene pedidos registrados");
        } else {
            System.out.println("📋 Cliente ID " + clienteId + " tiene " + pedidos.size() + " pedidos");
        }
        
        return pedidos;
    }

    /**
     * Obtiene los detalles de productos de un pedido
     */
    public List<String[]> obtenerDetallePedido(int pedidoId) throws SQLException {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser mayor a 0");
        }
        return dPedido.getDetallePedido(pedidoId);
    }

    /**
     * Crea un pedido desde el carrito del cliente (REGLA DE NEGOCIO)
     */
    public int crearPedidoDesdeCarrito(int clienteId, int direccionId) throws SQLException {
        if (clienteId <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a 0");
        }
        if (direccionId <= 0) {
            throw new IllegalArgumentException("El ID de la dirección debe ser mayor a 0");
        }

        // Validar que el carrito no esté vacío
        DCarrito dCarrito = new DCarrito();
        List<String[]> carrito = dCarrito.obtenerCarrito(clienteId);
        
        if (carrito == null || carrito.isEmpty()) {
            throw new IllegalStateException("No se puede crear un pedido con carrito vacío");
        }

        // Validar que haya stock suficiente antes de crear el pedido
        for (String[] item : carrito) {
            int cantidad = Integer.parseInt(item[1]);
            if (cantidad <= 0) {
                throw new IllegalStateException("Cantidad inválida en el carrito para producto: " + item[0]);
            }
        }

        // Crear el pedido
        int pedidoId = dPedido.crearPedidoDesdeCarrito(clienteId, direccionId);
        
        if (pedidoId <= 0) {
            throw new SQLException("Error al crear el pedido en la base de datos");
        }

        System.out.println("✅ NEGOCIO: Pedido creado exitosamente - ID: " + pedidoId + 
                          " para cliente: " + clienteId);
        
        return pedidoId;
    }

    /**
     * Actualiza el estado de un pedido (REGLA DE NEGOCIO)
     */
    public boolean actualizarEstado(int pedidoId, String nuevoEstado) throws SQLException {
        if (pedidoId <= 0) {
            throw new IllegalArgumentException("El ID del pedido debe ser mayor a 0");
        }
        
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }

        // Validar estados permitidos
        String estadoNormalizado = nuevoEstado.toLowerCase().trim();
        if (!estadoNormalizado.equals("pendiente") && 
            !estadoNormalizado.equals("pagado") && 
            !estadoNormalizado.equals("enviado") && 
            !estadoNormalizado.equals("entregado") && 
            !estadoNormalizado.equals("cancelado")) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado + 
                ". Estados válidos: pendiente, pagado, enviado, entregado, cancelado");
        }

        return dPedido.actualizarEstado(pedidoId, estadoNormalizado);
    }

    /**
     * Verifica si existen pedidos pendientes de pago para un cliente
     */
    public boolean tienePedidosPendientes(int clienteId) throws SQLException {
        if (clienteId <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser mayor a 0");
        }

        List<String[]> pedidos = dPedido.listByCliente(clienteId);
        
        if (pedidos != null) {
            for (String[] pedido : pedidos) {
                String estado = pedido[4]; // Índice del estado en el array
                if ("pendiente".equalsIgnoreCase(estado)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    /**
     * Verifica si un pedido existe
     */
    public boolean existe(int pedidoId) throws SQLException {
        if (pedidoId <= 0) {
            return false;
        }
        return dPedido.exists(pedidoId);
    }
} 