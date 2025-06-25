package negocio;

import java.sql.SQLException;
import java.util.List;
import data.DCarrito;

public class NCarrito {
    private DCarrito dCarrito;

    public NCarrito() {
        this.dCarrito = new DCarrito();
    }

    public boolean agregarProducto(int clienteId, int productoId, int cantidad) throws SQLException {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }
        return dCarrito.agregarProducto(clienteId, productoId, cantidad);
    }

    public List<String[]> obtenerCarrito(int clienteId) throws SQLException {
        return dCarrito.obtenerCarrito(clienteId);
    }

    public double obtenerTotalCarrito(int clienteId) throws SQLException {
        return dCarrito.obtenerTotalCarrito(clienteId);
    }

    public boolean removerProducto(int clienteId, int productoId) throws SQLException {
        return dCarrito.removerProducto(clienteId, productoId);
    }

    public boolean vaciarCarrito(int clienteId) throws SQLException {
        return dCarrito.vaciarCarrito(clienteId);
    }
} 