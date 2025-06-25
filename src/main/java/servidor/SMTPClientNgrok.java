package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Cliente SMTP para conectar al servidor a través de ngrok
 * @author Jairo
 */
public class SMTPClientNgrok {
    
    private String serverHost;
    private int serverPort;
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    
    public SMTPClientNgrok(String host, int port) {
        this.serverHost = host;
        this.serverPort = port;
    }
    
    public void connect() throws IOException {
        System.out.println("🔗 Conectando a " + serverHost + ":" + serverPort + "...");
        socket = new Socket(serverHost, serverPort);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        
        // Leer saludo del servidor
        String response = reader.readLine();
        System.out.println("🔌 " + response);
    }
    
    public void sendEmail(String from, String to, String subject, String message) throws IOException {
        System.out.println("\n📧 Enviando email...");
        
        // 1. HELO
        sendCommand("HELO cliente-ngrok.local");
        
        // 2. MAIL FROM
        sendCommand("MAIL FROM:<" + from + ">");
        
        // 3. RCPT TO
        sendCommand("RCPT TO:<" + to + ">");
        
        // 4. DATA
        sendCommand("DATA");
        
        // 5. Enviar headers y mensaje
        System.out.println("📝 Enviando contenido...");
        writer.println("Subject: " + subject);
        writer.println("From: " + from);
        writer.println("To: " + to);
        writer.println("X-Sender: Cliente-Ngrok");
        writer.println(); // Línea vacía entre headers y contenido
        writer.println(message);
        writer.println("."); // Terminar mensaje
        
        String response = reader.readLine();
        System.out.println("📨 " + response);
    }
    
    public void disconnect() throws IOException {
        sendCommand("QUIT");
        if (socket != null) {
            socket.close();
        }
    }
    
    private void sendCommand(String command) throws IOException {
        System.out.println("📤 > " + command);
        writer.println(command);
        String response = reader.readLine();
        System.out.println("📥 < " + response);
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("🚀 Cliente SMTP para ngrok");
        System.out.println("==========================");
        
        // Configuración del servidor
        String host;
        int port;
        
        if (args.length >= 2) {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } else {
            System.out.print("🖥️  Host del servidor ngrok (ej: 0.tcp.ngrok.io): ");
            host = scanner.nextLine().trim();
            
            System.out.print("🔌 Puerto del servidor ngrok (ej: 12345): ");
            port = Integer.parseInt(scanner.nextLine().trim());
        }
        
        SMTPClientNgrok client = new SMTPClientNgrok(host, port);
        
        try {
            // Conectar
            client.connect();
            
            // Recopilar datos del email
            System.out.println("\n📋 Información del email:");
            System.out.print("👤 From (ej: usuario@mi-servidor.ngrok.io): ");
            String from = scanner.nextLine().trim();
            
            System.out.print("📧 To (email destinatario): ");
            String to = scanner.nextLine().trim();
            
            System.out.print("📝 Subject: ");
            String subject = scanner.nextLine().trim();
            
            System.out.println("✍️  Mensaje (termina con línea vacía):");
            StringBuilder message = new StringBuilder();
            String line;
            while (!(line = scanner.nextLine()).isEmpty()) {
                message.append(line).append("\n");
            }
            
            // Enviar email
            client.sendEmail(from, to, subject, message.toString());
            
            // Desconectar
            client.disconnect();
            
            System.out.println("\n✅ ¡Email enviado exitosamente a través de ngrok!");
            System.out.println("🔍 Revisa la bandeja de entrada del destinatario");
            
        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("💡 Verifica que:");
            System.err.println("   - El servidor esté ejecutándose");
            System.err.println("   - ngrok esté activo");
            System.err.println("   - El host y puerto sean correctos");
        } finally {
            scanner.close();
        }
    }
} 