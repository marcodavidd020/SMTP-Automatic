package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente SMTP para probar el servidor personalizado
 *
 * @author Jairo
 */
public class SMTPClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 2525;

    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;

    public void connect() throws IOException {
        socket = new Socket(SERVER_HOST, SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);

        // Leer saludo del servidor
        String response = reader.readLine();
        System.out.println("🔌 " + response);
    }

    public void sendEmail(String from, String to, String subject, String message) throws IOException {
        // 1. HELO
        sendCommand("HELO cliente.local");

        // 2. MAIL FROM
        sendCommand("MAIL FROM:<" + from + ">");

        // 3. RCPT TO
        sendCommand("RCPT TO:<" + to + ">");

        // 4. DATA
        sendCommand("DATA");

        // 5. Enviar headers y mensaje
        writer.println("Subject: " + subject);
        writer.println("From: " + from);
        writer.println("To: " + to);
        writer.println(); // Línea vacía entre headers y contenido
        writer.println(message);
        writer.println("."); // Terminar mensaje

        String response = reader.readLine();
        System.out.println("📨 " + response);
    }

    public void disconnect() throws IOException {
        sendCommand("QUIT");
        socket.close();
    }

    private void sendCommand(String command) throws IOException {
        System.out.println("📤 Enviando: " + command);
        writer.println(command);
        String response = reader.readLine();
        System.out.println("📥 Respuesta: " + response);
    }

    public static void main(String[] args) {
        SMTPClient client = new SMTPClient();

        try {
            System.out.println("🚀 Conectando al servidor SMTP personalizado...");
            client.connect();

            // Enviar email de prueba
            client.sendEmail(
                    "JairoJairoJairo@gmail.com",
                    "JairoJairoJairo@gmail.com",
                    "Prueba desde mi servidor",
                    "¡Hola! Este es un mensaje enviado desde mi propio servidor SMTP.\n\n"
                    + "El servidor recibe el mensaje y lo reenvía a través de Gmail.\n"
                    + "¡Funciona perfectamente!"
            );

            client.disconnect();
            System.out.println("✅ Email enviado correctamente!");

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            System.err.println("💡 Asegúrate de que el servidor esté ejecutándose");
        }
    }
}
