package servidor;

import java.io.IOException;

/**
 * Servidor completo de email bidireccional:
 * - HTTP Server para enviar emails
 * - Gmail Monitor para recibir y responder automáticamente
 * @author MARCO
 */
public class EmailServerComplete {
    
    private HTTPEmailServer httpServer;
    private GmailMonitor gmailMonitor;
    private Thread httpThread;
    private Thread monitorThread;
    
    public EmailServerComplete() {
        this.httpServer = new HTTPEmailServer();
        this.gmailMonitor = new GmailMonitor();
    }
    
    /**
     * Inicia ambos servicios: HTTP y Monitor
     */
    public void start() {
        System.out.println("🚀 INICIANDO SERVIDOR DE EMAIL COMPLETO");
        System.out.println("═══════════════════════════════════════");
        
        // Iniciar servidor HTTP en un hilo separado
        httpThread = new Thread(() -> {
            try {
                httpServer.start();
            } catch (IOException e) {
                System.err.println("❌ Error en servidor HTTP: " + e.getMessage());
            }
        });
        httpThread.setName("HTTP-Email-Server");
        httpThread.start();
        
        // Esperar un poco para que el HTTP server se inicie
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Iniciar monitor de Gmail en un hilo separado
        monitorThread = new Thread(() -> {
            gmailMonitor.startMonitoring();
        });
        monitorThread.setName("Gmail-Monitor");
        monitorThread.start();
        
        System.out.println("✅ SERVIDOR COMPLETO INICIADO");
        System.out.println("📤 Envío de emails: http://localhost:8080");
        System.out.println("📥 Monitoreo activo: marcodavidtoledo@gmail.com");
        System.out.println("🌐 URL pública: https://340c-181-188-162-193.ngrok-free.app");
        System.out.println("═══════════════════════════════════════");
        
        // Mantener el hilo principal vivo
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Detiene ambos servicios
     */
    public void stop() {
        System.out.println("🛑 DETENIENDO SERVIDOR COMPLETO...");
        
        // Detener monitor
        if (gmailMonitor != null) {
            gmailMonitor.stopMonitoring();
        }
        
        // Detener servidor HTTP
        if (httpServer != null) {
            try {
                httpServer.stop();
            } catch (IOException e) {
                System.err.println("Error deteniendo servidor HTTP: " + e.getMessage());
            }
        }
        
        // Interrumpir hilos
        if (monitorThread != null) {
            monitorThread.interrupt();
        }
        if (httpThread != null) {
            httpThread.interrupt();
        }
        
        System.out.println("✅ Servidor completo detenido");
    }
    
    /**
     * Imprime el estado actual del servidor
     */
    public void printStatus() {
        System.out.println("\n📊 ESTADO DEL SERVIDOR DE EMAIL:");
        System.out.println("═══════════════════════════════════════");
        System.out.println("📤 Servidor HTTP: " + (httpThread != null && httpThread.isAlive() ? "✅ ACTIVO" : "❌ INACTIVO"));
        System.out.println("📥 Monitor Gmail: " + (monitorThread != null && monitorThread.isAlive() ? "✅ ACTIVO" : "❌ INACTIVO"));
        System.out.println("🌐 URL pública: https://340c-181-188-162-193.ngrok-free.app");
        System.out.println("📧 Email monitoreado: marcodavidtoledo@gmail.com");
        System.out.println("═══════════════════════════════════════\n");
    }
    
    public static void main(String[] args) {
        EmailServerComplete server = new EmailServerComplete();
        
        // Configurar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔄 Cerrando servidor...");
            server.stop();
        }));
        
        // Mostrar estado cada 60 segundos
        Thread statusThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    Thread.sleep(60000); // 60 segundos
                    server.printStatus();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        statusThread.setName("Status-Monitor");
        statusThread.setDaemon(true);
        statusThread.start();
        
        // Iniciar servidor
        server.start();
    }
} 