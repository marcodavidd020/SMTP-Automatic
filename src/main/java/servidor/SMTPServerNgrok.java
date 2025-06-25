package servidor;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor SMTP optimizado para ngrok - acceso desde cualquier lugar
 * @author Jairo
 */
public class SMTPServerNgrok {

    private static final int SMTP_PORT = 2525;
    private static String SERVER_DOMAIN = "mi-servidor.ngrok.io"; // Se actualizará con ngrok
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running = false;

    public SMTPServerNgrok() {
        this.threadPool = Executors.newFixedThreadPool(20); // Más threads para ngrok
    }

    public void start() throws IOException {
        // Escuchar en todas las interfaces para ngrok
        serverSocket = new ServerSocket(SMTP_PORT, 50, InetAddress.getByName("0.0.0.0"));
        running = true;

        System.out.println("🚀 Servidor SMTP iniciado para ngrok");
        System.out.println("🌐 Puerto local: " + SMTP_PORT);
        System.out.println("📧 Dominio local: " + SERVER_DOMAIN);
        System.out.println("💡 Iniciando ngrok...");
        
        // Mostrar instrucciones para ngrok
        printNgrokInstructions();
        
        System.out.println("⏳ Esperando conexiones...\n");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                System.out.println("🔗 Nueva conexión desde: " + clientIP);
                threadPool.submit(new SMTPClientHandlerNgrok(clientSocket, SERVER_DOMAIN));
            } catch (IOException e) {
                if (running) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    private void printNgrokInstructions() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 INSTRUCCIONES PARA NGROK:");
        System.out.println("=".repeat(60));
        System.out.println("1. En otra terminal ejecuta:");
        System.out.println("   ngrok tcp " + SMTP_PORT);
        System.out.println("");
        System.out.println("2. Copia la URL que te dé ngrok, ejemplo:");
        System.out.println("   tcp://0.tcp.ngrok.io:12345");
        System.out.println("");
        System.out.println("3. Usa esta configuración en tus clientes:");
        System.out.println("   HOST: 0.tcp.ngrok.io");
        System.out.println("   PORT: 12345");
        System.out.println("   EMAIL: admin@mi-servidor.ngrok.io");
        System.out.println("=".repeat(60) + "\n");
    }

    public void updateDomain(String ngrokDomain) {
        SERVER_DOMAIN = ngrokDomain;
        System.out.println("🔄 Dominio actualizado: " + SERVER_DOMAIN);
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
        SMTPServerNgrok server = new SMTPServerNgrok();

        // Verificar si se pasó un dominio de ngrok como argumento
        if (args.length > 0) {
            server.updateDomain(args[0]);
        }

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