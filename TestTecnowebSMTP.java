import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * Test simple de conectividad SMTP a tecnoweb
 */
public class TestTecnowebSMTP {
    
    private static final String HOST = "mail.tecnoweb.org.bo";
    private static final int[] PUERTOS = {25, 587, 2525, 465};
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST DE CONECTIVIDAD SMTP TECNOWEB");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 Host: " + HOST);
        
        for (int puerto : PUERTOS) {
            testPort(HOST, puerto);
        }
        
        System.out.println("\n💡 RECOMENDACIONES:");
        System.out.println("   ✅ Usar el puerto que responda más rápido");
        System.out.println("   📧 Puerto 25: SMTP estándar");
        System.out.println("   📧 Puerto 587: SMTP submission (más común)");
        System.out.println("   📧 Puerto 2525: SMTP alternativo");
        System.out.println("   📧 Puerto 465: SMTPS (SSL)");
    }
    
    private static void testPort(String host, int port) {
        System.out.print("\n🔍 Probando puerto " + port + "... ");
        
        try (Socket socket = new Socket()) {
            long startTime = System.currentTimeMillis();
            socket.connect(new java.net.InetSocketAddress(host, port), 10000);
            long endTime = System.currentTimeMillis();
            
            System.out.println("✅ CONECTADO (" + (endTime - startTime) + "ms)");
            
            if (port == 25) {
                System.out.println("   📧 Puerto SMTP estándar - Sin cifrado");
            } else if (port == 587) {
                System.out.println("   📧 Puerto SMTP submission - Más compatible");
            } else if (port == 2525) {
                System.out.println("   📧 Puerto SMTP alternativo");
            } else if (port == 465) {
                System.out.println("   📧 Puerto SMTPS - Requiere SSL");
            }
            
        } catch (SocketTimeoutException e) {
            System.out.println("❌ TIMEOUT (>10s)");
        } catch (Exception e) {
            System.out.println("❌ ERROR: " + e.getMessage());
        }
    }
} 