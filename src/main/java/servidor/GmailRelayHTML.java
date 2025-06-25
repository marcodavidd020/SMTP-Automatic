package servidor;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Relay especializado para enviar emails HTML correctamente
 *
 * @author Jairo
 */
public class GmailRelayHTML {

    // 🔧 CONFIGURACIÓN DE TUS CREDENCIALES DE GMAIL
    private static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
    private static final String GMAIL_SMTP_PORT = "587";
    private static final boolean USE_SSL = true;

    // ⚠️ CAMBIAR ESTAS CREDENCIALES POR LAS TUYAS:
    private static final String GMAIL_USERNAME = "JairoJairoJairo@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "muknnpzrymdkduss"; // Contraseña de aplicación

    private final Session session;

    public GmailRelayHTML() {
        Properties props = new Properties();
        props.put("mail.smtp.host", GMAIL_SMTP_HOST);
        props.put("mail.smtp.port", GMAIL_SMTP_PORT);
        props.put("mail.smtp.auth", "true");

        if (USE_SSL) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        }

        this.session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(GMAIL_USERNAME, GMAIL_APP_PASSWORD);
            }
        });
    }

    /**
     * Envía un email a través de Gmail con soporte HTML completo
     *
     * @param from Dirección del remitente (se mostrará como from pero se
     * enviará desde Gmail)
     * @param to Dirección del destinatario
     * @param subject Asunto del mensaje
     * @param content Contenido del mensaje (HTML o texto)
     */
    public void sendEmail(String from, String to, String subject, String content) {
        try {
            MimeMessage message = new MimeMessage(session);

            // Usar Gmail como remitente real, pero mostrar el from original en Reply-To
            message.setFrom(new InternetAddress(GMAIL_USERNAME));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));

            // Agregar información del servidor en el asunto
            message.setSubject("[EmailApp] " + subject);

            // Agregar header personalizado para identificar el servidor
            message.addHeader("X-Original-From", from);
            message.addHeader("X-Server", "emailapp-independiente");

            // ✅ DETECTAR Y ENVIAR HTML CORRECTAMENTE
            boolean isHtml = content.trim().startsWith("<!DOCTYPE html") || content.trim().startsWith("<html");
            
            if (isHtml) {
                // ✅ Si es HTML, enviarlo como HTML sin modificar
                message.setContent(content, "text/html; charset=utf-8");
                System.out.println("   🎨 Enviando como HTML moderno");
            } else {
                // Si es texto plano, agregar información del servidor
                String fullContent = "📧 EmailApp Independiente\n"
                        + "👤 Respuesta automática para: " + to + "\n"
                        + "🤖 Comando procesado por: " + from + "\n"
                        + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
                        + content;
                message.setText(fullContent, "utf-8");
                System.out.println("   📄 Enviando como texto plano");
            }

            Transport.send(message);

            System.out.println("✅ Email HTML enviado exitosamente:");
            System.out.println("   📤 From: " + from + " (via " + GMAIL_USERNAME + ")");
            System.out.println("   📥 To: " + to);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🎨 Tipo: " + (isHtml ? "HTML" : "Texto"));

        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email HTML: " + e.getMessage());
            Logger.getLogger(GmailRelayHTML.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Método específico para enviar emails HTML
     */
    public void sendHtmlEmail(String from, String to, String subject, String htmlContent) {
        try {
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(GMAIL_USERNAME));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("[Sistema Moderno] " + subject);

            // ✅ FORZAR COMO HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("✅ Email HTML puro enviado:");
            System.out.println("   📧 " + to);
            System.out.println("   🎨 HTML moderno confirmado");

        } catch (MessagingException e) {
            System.err.println("❌ Error enviando HTML: " + e.getMessage());
        }
    }
} 