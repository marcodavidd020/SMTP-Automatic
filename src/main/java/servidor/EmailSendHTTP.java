package servidor;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Cliente para enviar emails a través del servidor HTTP personalizado
 * @author MARCO
 */
public class EmailSendHTTP {
    
    private static final String SERVER_URL = "https://340c-181-188-162-193.ngrok-free.app/send-email";
    
    /**
     * Envía un email a través del servidor HTTP
     * @param from Email del remitente
     * @param to Email del destinatario
     * @param subject Asunto del mensaje
     * @param message Contenido del mensaje
     * @return true si se envió exitosamente
     */
    public static boolean sendEmail(String from, String to, String subject, String message) {
        try {
            // Preparar datos del formulario
            String formData = "from=" + URLEncoder.encode(from, StandardCharsets.UTF_8) +
                            "&to=" + URLEncoder.encode(to, StandardCharsets.UTF_8) +
                            "&subject=" + URLEncoder.encode(subject, StandardCharsets.UTF_8) +
                            "&message=" + URLEncoder.encode(message, StandardCharsets.UTF_8);
            
            // Crear conexión HTTP
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("ngrok-skip-browser-warning", "true");
            connection.setDoOutput(true);
            
            // Enviar datos
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(formData);
                writer.flush();
            }
            
            // Leer respuesta
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                System.out.println("✅ Email enviado exitosamente via HTTP");
                System.out.println("   📤 From: " + from);
                System.out.println("   📥 To: " + to);
                System.out.println("   📝 Subject: " + subject);
                return true;
            } else {
                System.err.println("❌ Error HTTP: " + responseCode);
                // Leer mensaje de error
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando email: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Actualiza la URL del servidor (útil si ngrok cambia la URL)
     */
    public static void setServerUrl(String newUrl) {
        System.setProperty("email.server.url", newUrl + "/send-email");
        System.out.println("🔄 URL del servidor actualizada: " + newUrl);
    }
    
    /**
     * Obtiene la URL actual del servidor
     */
    public static String getServerUrl() {
        return System.getProperty("email.server.url", SERVER_URL);
    }
    
    public static void main(String[] args) {
        System.out.println("🧪 Probando envío via servidor HTTP...");
        
        // Test básico
        boolean success = sendEmail(
            "marcodavidtoledo@gmail.com",
            "marcodavidtoledocanna@gmail.com", 
            "Prueba desde EmailSendHTTP",
            "Este mensaje fue enviado usando la clase EmailSendHTTP que conecta con tu servidor personalizado a través de ngrok."
        );
        
        if (success) {
            System.out.println("🎉 ¡Prueba exitosa! Revisa tu bandeja de entrada.");
        } else {
            System.out.println("❌ Prueba falló. Revisa los logs.");
        }
    }
} 