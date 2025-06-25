package negocio;

import java.sql.SQLException;
import java.util.List;

import data.DProducto;

/**
 * Capa de negocio para productos
 * Maneja la lógica de negocio relacionada con productos
 */
public class NProducto {
    
    private final DProducto dProducto;
    
    public NProducto() {
        this.dProducto = new DProducto();
    }
    
    /**
     * Obtiene un producto por su ID
     */
    public List<String[]> obtenerProducto(int id) throws SQLException {
        try {
            System.out.println("🛍️ [NEGOCIO] Obteniendo producto ID: " + id);
            List<String[]> resultado = dProducto.get(id);
            
            if (resultado.isEmpty()) {
                throw new SQLException("Producto no encontrado con ID: " + id);
            }
            
            System.out.println("✅ [NEGOCIO] Producto obtenido exitosamente");
            return resultado;
            
        } catch (SQLException e) {
            System.err.println("❌ [NEGOCIO] Error obteniendo producto: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Lista todos los productos disponibles
     */
    public List<String[]> listarProductos() throws SQLException {
        try {
            System.out.println("🛍️ [NEGOCIO] Listando todos los productos");
            List<String[]> productos = dProducto.list();
            
            System.out.println("✅ [NEGOCIO] Se encontraron " + productos.size() + " productos");
            return productos;
            
        } catch (SQLException e) {
            System.err.println("❌ [NEGOCIO] Error listando productos: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Lista productos por categoría
     */
    public List<String[]> listarProductosPorCategoria(int categoriaId) throws SQLException {
        try {
            System.out.println("🛍️ [NEGOCIO] Listando productos de categoría: " + categoriaId);
            List<String[]> productos = dProducto.listByCategory(categoriaId);
            
            System.out.println("✅ [NEGOCIO] Se encontraron " + productos.size() + " productos en la categoría");
            return productos;
            
        } catch (SQLException e) {
            System.err.println("❌ [NEGOCIO] Error listando productos por categoría: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Valida que un producto existe y tiene stock disponible
     */
    public boolean validarDisponibilidad(int productoId) throws SQLException {
        try {
            List<String[]> producto = obtenerProducto(productoId);
            // Aquí podrías agregar lógica adicional para verificar stock
            return !producto.isEmpty();
            
        } catch (SQLException e) {
            System.err.println("❌ [NEGOCIO] Error validando disponibilidad del producto: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Obtiene información resumida de un producto para display
     */
    public String[] obtenerResumenProducto(int productoId) throws SQLException {
        try {
            List<String[]> producto = obtenerProducto(productoId);
            if (!producto.isEmpty()) {
                String[] data = producto.get(0);
                return new String[]{
                    data[0], // ID
                    data[2], // Nombre
                    data[4], // Precio venta
                    data[7]  // Categoría
                };
            }
            throw new SQLException("Producto no encontrado: " + productoId);
            
        } catch (SQLException e) {
            System.err.println("❌ [NEGOCIO] Error obteniendo resumen del producto: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Cierra las conexiones de la capa de datos
     */
    public void cerrarConexiones() {
        if (dProducto != null) {
            dProducto.disconnect();
        }
    }
}