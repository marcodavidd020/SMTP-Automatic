package test;

import java.sql.SQLException;
import java.util.List;

import data.DProducto;
import negocio.NProducto;

/**
 * Test directo para verificar que DProducto y NProducto funcionan
 */
public class TestProductoDirecto {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DIRECTO DE PRODUCTOS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        // Test 1: Probar capa de datos directamente
        testCapaDatos();
        
        // Test 2: Probar capa de negocio
        testCapaNegocio();
        
        System.out.println("\n🎉 Test completado");
    }
    
    private static void testCapaDatos() {
        System.out.println("\n📊 TEST 1: CAPA DE DATOS (DProducto)");
        System.out.println("─────────────────────────────────────");
        
        try {
            DProducto dProducto = new DProducto();
            
            System.out.println("🔍 Intentando listar productos...");
            List<String[]> productos = dProducto.list();
            
            System.out.println("✅ Productos encontrados: " + productos.size());
            
            if (!productos.isEmpty()) {
                System.out.println("📝 Primeros 3 productos:");
                for (int i = 0; i < Math.min(3, productos.size()); i++) {
                    String[] prod = productos.get(i);
                    System.out.println("   " + (i+1) + ". ID: " + prod[0] + 
                                     " | Nombre: " + prod[2] + 
                                     " | Precio: $" + prod[4]);
                }
                
                // Test obtener producto específico
                String[] primerProducto = productos.get(0);
                int id = Integer.parseInt(primerProducto[0]);
                
                System.out.println("🔍 Obteniendo producto ID: " + id);
                List<String[]> producto = dProducto.get(id);
                System.out.println("✅ Producto obtenido: " + (producto.isEmpty() ? "NO" : "SÍ"));
            }
            
            dProducto.disconnect();
            
        } catch (SQLException e) {
            System.err.println("❌ Error en capa de datos: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCapaNegocio() {
        System.out.println("\n🏢 TEST 2: CAPA DE NEGOCIO (NProducto)");
        System.out.println("─────────────────────────────────────");
        
        try {
            NProducto nProducto = new NProducto();
            
            System.out.println("🔍 Intentando listar productos via negocio...");
            List<String[]> productos = nProducto.listarProductos();
            
            System.out.println("✅ Productos via negocio: " + productos.size());
            
            if (!productos.isEmpty()) {
                String[] primerProducto = productos.get(0);
                int id = Integer.parseInt(primerProducto[0]);
                
                System.out.println("🔍 Obteniendo producto ID " + id + " via negocio...");
                List<String[]> producto = nProducto.obtenerProducto(id);
                System.out.println("✅ Producto via negocio: " + (producto.isEmpty() ? "NO" : "SÍ"));
                
                // Test resumen
                System.out.println("📋 Obteniendo resumen del producto...");
                String[] resumen = nProducto.obtenerResumenProducto(id);
                System.out.println("✅ Resumen: " + resumen[1] + " ($" + resumen[2] + ")");
            }
            
            nProducto.cerrarConexiones();
            
        } catch (SQLException e) {
            System.err.println("❌ Error en capa de negocio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}