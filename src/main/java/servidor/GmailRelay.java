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
 * Relay que envía emails a través de Gmail usando las credenciales configuradas
 *
 * @author Jairo
 */
public class GmailRelay {

    // 🔧 CONFIGURACIÓN DE TUS CREDENCIALES DE GMAIL
    private static final String GMAIL_SMTP_HOST = "smtp.gmail.com";
    private static final String GMAIL_SMTP_PORT = "587";
    private static final boolean USE_SSL = true;

    // ⚠️ CAMBIAR ESTAS CREDENCIALES POR LAS TUYAS:
    private static final String GMAIL_USERNAME = "marcodavidtoledo@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "muknnpzrymdkduss"; // Contraseña de aplicación

    private final Session session;

    public GmailRelay() {
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
     * Envía un email a través de Gmail
     *
     * @param from Dirección del remitente (se mostrará como from pero se
     * enviará desde Gmail)
     * @param to Dirección del destinatario
     * @param subject Asunto del mensaje
     * @param content Contenido del mensaje
     */
    public void sendEmail(String from, String to, String subject, String content) {
        sendEmail(from, to, subject, content, null);
    }

    /**
     * Envía un email a través de Gmail con soporte para responder a un email específico
     *
     * @param from Dirección del remitente
     * @param to Dirección del destinatario
     * @param subject Asunto del mensaje
     * @param content Contenido del mensaje
     * @param inReplyTo Message-ID del email original (puede ser null)
     */
    public void sendEmail(String from, String to, String subject, String content, String inReplyTo) {
        try {
            MimeMessage message = new MimeMessage(session);

            // Usar Gmail como remitente real, pero mostrar el from original en Reply-To
            message.setFrom(new InternetAddress(GMAIL_USERNAME));
            message.setReplyTo(new InternetAddress[]{new InternetAddress(from)});
            message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse(to));

            // Si es una respuesta, agregar headers apropiados
            if (inReplyTo != null && !inReplyTo.trim().isEmpty()) {
                message.addHeader("In-Reply-To", inReplyTo);
                message.addHeader("References", inReplyTo);
                // Asegurarse de que empiece con "Re: " si no lo tiene
                if (!subject.toLowerCase().startsWith("re:")) {
                    subject = "Re: " + subject;
                }
                System.out.println("   💬 Respondiendo al mensaje: " + inReplyTo);
            }

            // Usar prefijo más limpio para respuestas de comandos
            if (subject.contains("[Comando:")) {
                message.setSubject(subject); // Ya tiene el formato correcto
            } else {
                message.setSubject("[Sistema CRUD] " + subject);
            }

            // Agregar header personalizado para identificar el servidor
            message.addHeader("X-Original-From", from);
            message.addHeader("X-Server", "sistema-crud-email");
            message.addHeader("X-Auto-Reply", "true");

            // Detectar si el contenido es HTML
            boolean isHtml = content.trim().startsWith("<!DOCTYPE html") || content.trim().startsWith("<html");
            
            if (isHtml) {
                // Si es HTML, enviarlo como HTML
                message.setContent(content, "text/html; charset=utf-8");
                System.out.println("   🎨 Enviando como HTML");
            } else {
                // Si es texto plano, agregar información del servidor solo si no es respuesta de comando
                if (inReplyTo != null) {
                    // Es una respuesta - mantener contenido limpio
                    message.setText(content, "utf-8");
                    System.out.println("   💬 Enviando respuesta limpia");
                } else {
                    // Email nuevo - agregar información del servidor
                    String fullContent = "📧 Sistema CRUD via Email\n"
                            + "👤 Respuesta para: " + to + "\n"
                            + "🤖 Procesado automáticamente\n"
                            + "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n"
                            + content;
                    message.setText(fullContent, "utf-8");
                    System.out.println("   📄 Enviando como texto plano");
                }
            }

            Transport.send(message);

            System.out.println("✅ Email enviado exitosamente:");
            System.out.println("   📤 From: " + from + " (via " + GMAIL_USERNAME + ")");
            System.out.println("   📥 To: " + to);
            System.out.println("   📝 Subject: " + subject);
            if (inReplyTo != null) {
                System.out.println("   💬 En respuesta a: " + inReplyTo);
            }

        } catch (MessagingException e) {
            System.err.println("❌ Error enviando email: " + e.getMessage());
            Logger.getLogger(GmailRelay.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Método específico para responder a emails (alias más claro)
     *
     * @param to Destinatario
     * @param originalSubject Asunto original del email
     * @param content Contenido de la respuesta
     * @param originalMessageId Message-ID del email original
     */
    public void replyToEmail(String to, String originalSubject, String content, String originalMessageId) {
        String replySubject = originalSubject.toLowerCase().startsWith("re:") ? 
            originalSubject : "Re: " + originalSubject;
        
        sendEmail(GMAIL_USERNAME, to, replySubject, content, originalMessageId);
        System.out.println("   🔄 Respuesta enviada como reply al email original");
    }

    /**
     * Configura las credenciales de Gmail dinámicamente
     *
     * @param username Tu email de Gmail
     * @param appPassword Tu contraseña de aplicación de Gmail
     */
    public static void configurarCredenciales(String username, String appPassword) {
        System.out.println("💡 Para configurar las credenciales:");
        System.out.println("   1. Edita la clase GmailRelay.java");
        System.out.println("   2. Cambia GMAIL_USERNAME por: " + username);
        System.out.println("   3. Cambia GMAIL_APP_PASSWORD por: " + appPassword);
        System.out.println("   4. Recompila el proyecto");
    }
}
