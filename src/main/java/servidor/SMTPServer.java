package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor SMTP personalizado que actúa como relay usando Gmail
 *
 * @author MARCO
 */
public class SMTPServer {

    private static final int SMTP_PORT = 2525; // Puerto personalizado para evitar conflictos
    private static final String SERVER_NAME = "mi-servidor.local";
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running = false;

    public SMTPServer() {
        this.threadPool = Executors.newFixedThreadPool(10);
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(SMTP_PORT);
        running = true;

        System.out.println("🚀 Servidor SMTP iniciado en puerto " + SMTP_PORT);
        System.out.println("📧 Dominio del servidor: " + SERVER_NAME);
        System.out.println("💡 Usa direcciones como: usuario@" + SERVER_NAME);
        System.out.println("⏳ Esperando conexiones...\n");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new SMTPClientHandler(clientSocket));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    public void stop() throws IOException {
        running = false;
        if (serverSocket != null) {
            serverSocket.close();
        }
        threadPool.shutdown();
        System.out.println("🛑 Servidor SMTP detenido");
    }

    public static void main(String[] args) {
        SMTPServer server = new SMTPServer();

        // Agregar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                server.stop();
            } catch (IOException e) {
                System.err.println("Error cerrando servidor: " + e.getMessage());
            }
        }));

        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Error iniciando servidor: " + e.getMessage());
        }
    }
}
