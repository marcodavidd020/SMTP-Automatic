import java.sql.SQLException;
import java.util.List;

import data.DProducto;
import librerias.Interpreter;
import librerias.Instruccion;
import librerias.analex.Token;

/**
 * Test completo para simular exactamente el flujo del sistema de email
 */
public class TestAnalexCompleto {
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST COMPLETO: FLUJO EMAIL PRODUCTO GET");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        try {
            // Simular exactamente lo que hace el sistema de email
            String comando = "producto get";
            String sender = "test@example.com";
            
            System.out.println("📧 Comando recibido: '" + comando + "'");
            System.out.println("👤 Remitente: " + sender);
            
            // Test 1: Verificar que el constructor Token funciona
            System.out.println("\n🔍 TEST 1: Constructor Token");
            Token tokenProducto = new Token("producto");
            System.out.println("Token 'producto':");
            System.out.println("  - Name: " + tokenProducto.getName() + " (esperado: " + Token.CU + ")");
            System.out.println("  - Attribute: " + tokenProducto.getAttribute() + " (esperado: " + Token.PRODUCTO + ")");
            System.out.println("  - String: " + tokenProducto.getStringToken(tokenProducto.getAttribute()));
            
            Token tokenGet = new Token("get");
            System.out.println("Token 'get':");
            System.out.println("  - Name: " + tokenGet.getName() + " (esperado: " + Token.ACTION + ")");
            System.out.println("  - Attribute: " + tokenGet.getAttribute() + " (esperado: " + Token.GET + ")");
            System.out.println("  - String: " + tokenGet.getStringToken(tokenGet.getAttribute()));
            
            // Test 2: Usar Instruccion e Interpreter como en el sistema real
            System.out.println("\n🔍 TEST 2: Instruccion e Interpreter");
            
            Instruccion instruccion = new Instruccion(comando);
            System.out.println("✅ Instruccion creada");
            
            // Test 3: Probar DProducto directamente (esto sabemos que funciona)
            System.out.println("\n🔍 TEST 3: Verificar DProducto");
            DProducto dProducto = new DProducto();
            List<String[]> productos = dProducto.list();
            System.out.println("✅ DProducto funciona - " + productos.size() + " productos");
            dProducto.disconnect();
            
            // Test 4: Crear listener de prueba
            System.out.println("\n🔍 TEST 4: Simular Listener");
            TestListener listener = new TestListener();
            
            Interpreter interpreter = new Interpreter(instruccion, sender);
            interpreter.setListener(listener);
            
            System.out.println("🚀 Ejecutando interpreter...");
            interpreter.run();
            
            if (listener.wasProductoCalled()) {
                System.out.println("✅ SUCCESS: Método producto() fue llamado correctamente");
            } else {
                System.err.println("❌ FAIL: Método producto() NO fue llamado");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en test: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🎉 Test completado");
    }
}

// Listener de prueba para capturar las llamadas
class TestListener implements interfaces.ICasoUsoListener {
    
    private boolean productoCalled = false;
    
    public boolean wasProductoCalled() {
        return productoCalled;
    }
    
    @Override
    public void usuario(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a usuario()");
    }
    
    @Override
    public void producto(librerias.ParamsAction event) {
        System.out.println("📞 ✅ Llamada a producto() - ÉXITO!");
        System.out.println("  - Sender: " + event.getSender());
        System.out.println("  - Action: " + event.getAction());
        System.out.println("  - Command: " + event.getCommand());
        System.out.println("  - Params count: " + event.countParams());
        productoCalled = true;
    }
    
    @Override
    public void pago(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a pago()");
    }
    
    @Override
    public void proveedor(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a proveedor()");
    }
    
    @Override
    public void patrocinador(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a patrocinador()");
    }
    
    @Override
    public void patrocinio(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a patrocinio()");
    }
    
    @Override
    public void rol(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a rol()");
    }
    
    @Override
    public void servicio(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a servicio()");
    }
    
    @Override
    public void error(librerias.ParamsAction event) {
        System.err.println("📞 ❌ Llamada a error()");
        System.err.println("  - Message: " + event.getMessage());
    }
    
    @Override
    public void help(librerias.ParamsAction event) {
        System.out.println("📞 Llamada a help()");
    }
} 