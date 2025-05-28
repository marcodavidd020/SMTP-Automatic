package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Servidor HTTP que recibe emails via POST y los envía a través de Gmail
 * Funciona con ngrok HTTP (gratuito)
 * @author MARCO
 */
public class HTTPEmailServer {
    
    private static final int HTTP_PORT = 8080;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private boolean running = false;
    
    public HTTPEmailServer() {
        this.threadPool = Executors.newFixedThreadPool(10);
    }
    
    public void start() throws IOException {
        serverSocket = new ServerSocket(HTTP_PORT);
        running = true;
        
        System.out.println("🚀 Servidor HTTP Email iniciado en puerto " + HTTP_PORT);
        System.out.println("📧 Endpoint: http://localhost:" + HTTP_PORT + "/send-email");
        System.out.println("💡 Usar con ngrok: ngrok http " + HTTP_PORT);
        System.out.println("⏳ Esperando solicitudes...\n");
        
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                threadPool.submit(new HTTPEmailHandler(clientSocket));
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
        System.out.println("🛑 Servidor HTTP Email detenido");
    }
    
    private static class HTTPEmailHandler implements Runnable {
        private final Socket clientSocket;
        
        public HTTPEmailHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }
        
        @Override
        public void run() {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true)) {
                
                String requestLine = reader.readLine();
                if (requestLine == null) return;
                
                System.out.println("📨 Solicitud: " + requestLine);
                
                // Leer headers
                Map<String, String> headers = new HashMap<>();
                String line;
                while (!(line = reader.readLine()).isEmpty()) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        headers.put(parts[0].trim().toLowerCase(), parts[1].trim());
                    }
                }
                
                if (requestLine.startsWith("POST /send-email")) {
                    handleSendEmail(reader, writer, headers);
                } else if (requestLine.startsWith("GET /")) {
                    handleGetRequest(writer);
                } else {
                    sendErrorResponse(writer, 404, "Not Found");
                }
                
            } catch (IOException e) {
                System.err.println("Error manejando solicitud HTTP: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
            }
        }
        
        private void handleSendEmail(BufferedReader reader, PrintWriter writer, Map<String, String> headers) throws IOException {
            int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
            
            // Leer el cuerpo de la solicitud
            char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            String requestBody = new String(body);
            
            System.out.println("📝 Cuerpo de la solicitud: " + requestBody);
            
            // Parsear parámetros del cuerpo (formato: from=...&to=...&subject=...&message=...)
            Map<String, String> params = parseFormData(requestBody);
            
            String from = params.get("from");
            String to = params.get("to");
            String subject = params.get("subject");
            String message = params.get("message");
            
            System.out.println("🔍 Parámetros parseados:");
            System.out.println("   From: " + from);
            System.out.println("   To: " + to);
            System.out.println("   Subject: " + subject);
            System.out.println("   Message: " + (message != null ? message.substring(0, Math.min(100, message.length())) + "..." : "null"));
            
            if (from == null || to == null || subject == null || message == null) {
                String errorMsg = "Missing parameters - from: " + (from != null) + ", to: " + (to != null) + ", subject: " + (subject != null) + ", message: " + (message != null);
                System.err.println("❌ " + errorMsg);
                sendErrorResponse(writer, 400, errorMsg);
                return;
            }
            
            try {
                System.out.println("📧 Enviando email...");
                // Enviar email usando Gmail Relay
                GmailRelay relay = new GmailRelay();
                relay.sendEmail(from, to, subject, message);
                
                System.out.println("✅ Email enviado exitosamente!");
                // Respuesta exitosa
                sendSuccessResponse(writer, from, to, subject);
                
            } catch (Exception e) {
                System.err.println("❌ Error enviando email: " + e.getMessage());
                e.printStackTrace();
                sendErrorResponse(writer, 500, "Error sending email: " + e.getMessage());
            }
        }
        
        private void handleGetRequest(PrintWriter writer) {
            String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Servidor Email HTTP</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
                "        h1 { color: #333; text-align: center; }\n" +
                "        form { margin-top: 30px; }\n" +
                "        label { display: block; margin: 15px 0 5px; font-weight: bold; color: #555; }\n" +
                "        input, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 14px; }\n" +
                "        textarea { height: 100px; }\n" +
                "        button { background: #007bff; color: white; padding: 12px 30px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; margin-top: 20px; }\n" +
                "        button:hover { background: #0056b3; }\n" +
                "        .info { background: #e7f3ff; padding: 15px; border-radius: 5px; margin-bottom: 20px; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <h1>Servidor Email HTTP</h1>\n" +
                "        <div class=\"info\">\n" +
                "            <strong>Servidor funcionando</strong><br>\n" +
                "            Envia emails a traves de Gmail usando este formulario\n" +
                "        </div>\n" +
                "        <form method=\"POST\" action=\"/send-email\">\n" +
                "            <label for=\"from\">From (tu email):</label>\n" +
                "            <input type=\"email\" name=\"from\" value=\"admin@mi-servidor.ngrok.io\" required>\n" +
                "            \n" +
                "            <label for=\"to\">To (destinatario):</label>\n" +
                "            <input type=\"email\" name=\"to\" value=\"marcodavidtoledocanna@gmail.com\" required>\n" +
                "            \n" +
                "            <label for=\"subject\">Subject:</label>\n" +
                "            <input type=\"text\" name=\"subject\" value=\"Prueba desde mi servidor HTTP\" required>\n" +
                "            \n" +
                "            <label for=\"message\">Message:</label>\n" +
                "            <textarea name=\"message\" required>Hola! Este es un mensaje enviado desde mi servidor HTTP personalizado.\n\nEl servidor recibe la solicitud HTTP y la convierte en un email usando Gmail.\n\nFunciona perfectamente con ngrok!</textarea>\n" +
                "            \n" +
                "            <button type=\"submit\">Enviar Email</button>\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
            
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("Content-Length: " + html.length());
            writer.println();
            writer.print(html);
        }
        
        private void sendSuccessResponse(PrintWriter writer, String from, String to, String subject) {
            String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <title>Email Enviado</title>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style>\n" +
                "        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }\n" +
                "        .container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }\n" +
                "        .success { background: #d4edda; color: #155724; padding: 20px; border-radius: 5px; text-align: center; }\n" +
                "        .details { margin: 20px 0; padding: 15px; background: #f8f9fa; border-radius: 5px; }\n" +
                "        a { color: #007bff; text-decoration: none; }\n" +
                "        a:hover { text-decoration: underline; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"success\">\n" +
                "            <h1>Email enviado exitosamente!</h1>\n" +
                "        </div>\n" +
                "        <div class=\"details\">\n" +
                "            <strong>From:</strong> " + from + "<br>\n" +
                "            <strong>To:</strong> " + to + "<br>\n" +
                "            <strong>Subject:</strong> " + subject + "\n" +
                "        </div>\n" +
                "        <p><a href=\"/\">Enviar otro email</a></p>\n" +
                "    </div>\n" +
                "</body>\n" +
                "</html>";
            
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("Content-Length: " + html.length());
            writer.println();
            writer.print(html);
        }
        
        private void sendErrorResponse(PrintWriter writer, int code, String message) {
            String response = "HTTP/1.1 " + code + " " + message + "\r\nContent-Type: text/plain\r\n\r\n" + message;
            writer.print(response);
        }
        
        private Map<String, String> parseFormData(String formData) {
            Map<String, String> params = new HashMap<>();
            try {
                String[] pairs = formData.split("&");
                for (String pair : pairs) {
                    String[] keyValue = pair.split("=", 2);
                    if (keyValue.length == 2) {
                        String key = URLDecoder.decode(keyValue[0], "UTF-8");
                        String value = URLDecoder.decode(keyValue[1], "UTF-8");
                        params.put(key, value);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                System.err.println("Error decodificando parámetros: " + e.getMessage());
            }
            return params;
        }
    }
    
    public static void main(String[] args) {
        HTTPEmailServer server = new HTTPEmailServer();
        
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