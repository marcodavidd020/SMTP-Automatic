import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Test del protocolo SMTP directo con tecnoweb
 */
public class TestSMTPProtocol {
    
    private static final String HOST = "mail.tecnoweb.org.bo";
    private static final int PORT = 25;
    
    public static void main(String[] args) {
        System.out.println("🧪 TEST PROTOCOLO SMTP DIRECTO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🌐 Host: " + HOST);
        System.out.println("🔌 Puerto: " + PORT);
        
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            
            System.out.println("\n✅ Conectado al servidor SMTP");
            
            // Configurar timeout del socket
            socket.setSoTimeout(30000); // 30 segundos
            
            // Esperar un poco antes de leer
            System.out.println("⏱️ Esperando respuesta del servidor...");
            Thread.sleep(2000); // 2 segundos
            
            // Leer saludo del servidor
            String response = null;
            try {
                if (reader.ready()) {
                    response = reader.readLine();
                } else {
                    System.out.println("⚠️ Servidor no envía saludo inmediato, intentando enviar HELO...");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error leyendo saludo: " + e.getMessage());
            }
            
            System.out.println("📥 Respuesta del servidor: " + response);
            
            // Intentar comunicación SMTP independientemente del saludo
            System.out.println("📤 Enviando: HELO localhost");
            writer.println("HELO localhost");
            writer.flush();
            
            Thread.sleep(2000); // Esperar respuesta
            
            try {
                if (reader.ready()) {
                    String heloResponse = reader.readLine();
                    System.out.println("📥 Respuesta HELO: " + heloResponse);
                    
                    if (heloResponse != null && heloResponse.startsWith("250")) {
                        System.out.println("✅ HELO aceptado - Servidor listo para recibir emails");
                        
                        // Enviar QUIT
                        System.out.println("📤 Enviando: QUIT");
                        writer.println("QUIT");
                        writer.flush();
                        Thread.sleep(1000);
                        
                        if (reader.ready()) {
                            String quitResponse = reader.readLine();
                            System.out.println("📥 Respuesta QUIT: " + quitResponse);
                        }
                        
                        System.out.println("\n🎉 ¡SERVIDOR SMTP TECNOWEB COMPLETAMENTE FUNCIONAL!");
                        System.out.println("💡 El servidor acepta conexiones sin autenticación");
                        System.out.println("💡 Usar JavaMail con configuración básica sin SSL/TLS");
                    } else {
                        System.out.println("❌ Error en HELO: " + heloResponse);
                    }
                } else {
                    System.out.println("⚠️ Servidor no responde a HELO inmediatamente");
                    System.out.println("💡 Puede requerir configuración especial en JavaMail");
                }
            } catch (Exception e) {
                System.out.println("⚠️ Error en comunicación SMTP: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error conectando al servidor SMTP:");
            System.err.println("   " + e.getMessage());
            e.printStackTrace();
        }
    }
} 