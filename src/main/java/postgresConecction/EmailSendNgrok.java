package postgresConecction;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import librerias.Email;

/**
 * Envío de emails a través de servidor SMTP propio usando ngrok
 * @author Jairo
 */
public class EmailSendNgrok implements Runnable {

    // 🌐 CONFIGURACIÓN NGROK - Actualiza con tu URL de ngrok
    private final static String HOST = "0.tcp.ngrok.io"; // ← Cambiar por tu host ngrok
    private final static String PORT_SMTP = "12345";     // ← Cambiar por tu puerto ngrok
    
    // 📧 Configuración del email
    private final static String MAIL = "admin@mi-servidor.ngrok.io";
    private final static boolean USE_AUTH = false; // Sin autenticación
    
    private Email email;

    public EmailSendNgrok(Email emailP) {
        this.email = emailP;
    }
    
    /**
     * Constructor con configuración personalizada de ngrok
     * @param emailP El email a enviar
     * @param ngrokHost Host de ngrok (ej: 0.tcp.ngrok.io)
     * @param ngrokPort Puerto de ngrok (ej: 12345)
     */
    public EmailSendNgrok(Email emailP, String ngrokHost, String ngrokPort) {
        this.email = emailP;
        // Configuración dinámica para ngrok
        System.setProperty("smtp.host", ngrokHost);
        System.setProperty("smtp.port", ngrokPort);
    }

    @Override
    public void run() {
        // Usar configuración dinámica si está disponible
        String host = System.getProperty("smtp.host", HOST);
        String port = System.getProperty("smtp.port", PORT_SMTP);
        
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.auth", String.valueOf(USE_AUTH));
        
        // Configuración básica sin cifrado para conexión directa
        properties.setProperty("mail.smtp.starttls.enable", "false");
        properties.setProperty("mail.smtp.ssl.enable", "false");
        properties.setProperty("mail.smtp.connectiontimeout", "10000"); // 10 segundos
        properties.setProperty("mail.smtp.timeout", "10000");
        
        Session session = Session.getDefaultInstance(properties, null);
        
        System.out.println("📡 Conectando a servidor via ngrok: " + host + ":" + port);
        
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo())};

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());
            
            // Agregar headers personalizados para identificar el origen
            message.addHeader("X-Sent-Via", "Ngrok-Relay");
            message.addHeader("X-Server", "mi-servidor.ngrok.io");

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
            
            System.out.println("✅ Email enviado exitosamente via ngrok!");
            System.out.println("   📤 From: " + MAIL);
            System.out.println("   📥 To: " + email.getTo());
            System.out.println("   🌐 Via: " + host + ":" + port);
            
        } catch (NoSuchProviderException | AddressException ex) {
            Logger.getLogger(EmailSendNgrok.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("❌ Error de dirección: " + ex.getMessage());
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSendNgrok.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("❌ Error de conexión SMTP via ngrok: " + ex.getMessage()); 
            System.out.println("💡 Verifica que:");
            System.out.println("   - Tu servidor SMTP esté ejecutándose");
            System.out.println("   - ngrok esté activo en " + host + ":" + port);
            System.out.println("   - La configuración HOST y PORT_SMTP sea correcta");
        }
    }
    
    /**
     * Método estático para configurar y enviar email con ngrok
     * @param email Email a enviar
     * @param ngrokHost Host de ngrok
     * @param ngrokPort Puerto de ngrok
     */
    public static void sendViaNetwork(Email email, String ngrokHost, String ngrokPort) {
        EmailSendNgrok sender = new EmailSendNgrok(email, ngrokHost, ngrokPort);
        Thread thread = new Thread(sender);
        thread.setName("EmailSend-Ngrok-" + ngrokHost);
        thread.start();
    }
    
    /**
     * Método para mostrar instrucciones de configuración
     */
    public static void showConfiguration() {
        System.out.println("🔧 CONFIGURACIÓN NGROK:");
        System.out.println("========================");
        System.out.println("1. Ejecuta tu servidor SMTP: ./start-smtp-ngrok.sh");
        System.out.println("2. Copia la URL de ngrok (ej: tcp://0.tcp.ngrok.io:12345)");
        System.out.println("3. Actualiza EmailSendNgrok.java:");
        System.out.println("   HOST = \"0.tcp.ngrok.io\"");
        System.out.println("   PORT_SMTP = \"12345\"");
        System.out.println("4. O usa sendViaNetwork(email, host, port)");
        System.out.println("========================");
    }
} 