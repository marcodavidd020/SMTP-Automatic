package servidor;

import java.util.Date;
import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.search.FlagTerm;

/**
 * Monitor que revisa emails entrantes en Gmail y responde automáticamente
 * @author Jairo
 */
public class GmailMonitor {
    
    // 🔧 CONFIGURACIÓN DE GMAIL IMAP
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_PORT = "993";
    private static final String GMAIL_USERNAME = "marcodavidtoledo@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "muknnpzrymdkduss";
    
    private Session session;
    private Store store;
    private Folder inbox;
    private GmailRelay responder;
    private boolean monitoring = false;
    
    public GmailMonitor() {
        this.responder = new GmailRelay();
        setupIMAPConnection();
    }
    
    private void setupIMAPConnection() {
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", IMAP_HOST);
        props.put("mail.imaps.port", IMAP_PORT);
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.protocols", "TLSv1.2");
        props.put("mail.imaps.ssl.trust", "*");
        
        this.session = Session.getInstance(props);
    }
    
    /**
     * Inicia el monitoreo de emails entrantes
     */
    public void startMonitoring() {
        monitoring = true;
        System.out.println("📬 Iniciando monitoreo de emails entrantes...");
        System.out.println("📧 Monitoreando: " + GMAIL_USERNAME);
        System.out.println("🔄 Revisando cada 30 segundos");
        
        try {
            // Conectar al servidor IMAP
            store = session.getStore("imaps");
            store.connect(IMAP_HOST, GMAIL_USERNAME, GMAIL_APP_PASSWORD);
            
            // Abrir bandeja de entrada
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);
            
            System.out.println("✅ Conectado exitosamente a Gmail IMAP");
            System.out.println("📊 Total de emails en bandeja: " + inbox.getMessageCount());
            
            // Monitoreo continuo
            while (monitoring) {
                checkForNewEmails();
                Thread.sleep(30000); // Revisar cada 30 segundos
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en monitoreo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
    
    /**
     * Revisa emails nuevos (no leídos)
     */
    private void checkForNewEmails() {
        try {
            // Buscar emails no leídos
            Message[] unreadMessages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
            
            if (unreadMessages.length > 0) {
                System.out.println("📨 Encontrados " + unreadMessages.length + " emails nuevos");
                
                for (Message message : unreadMessages) {
                    processNewEmail(message);
                    // Marcar como leído
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error revisando emails: " + e.getMessage());
        }
    }
    
    /**
     * Procesa un email nuevo y envía respuesta automática
     */
    private void processNewEmail(Message message) {
        try {
            // Obtener información del email
            String from = ((InternetAddress) message.getFrom()[0]).getAddress();
            String subject = message.getSubject();
            String content = getTextContent(message);
            Date receivedDate = message.getReceivedDate();
            
            System.out.println("\n📧 Nuevo email recibido:");
            System.out.println("   👤 From: " + from);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🕐 Received: " + receivedDate);
            System.out.println("   💬 Content preview: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
            
            // Verificar si no es un email automático (evitar loops)
            if (isAutoReplyEmail(subject, from)) {
                System.out.println("🔄 Email automático detectado, omitiendo respuesta");
                return;
            }
            
            // Generar respuesta automática
            String autoReply = generateAutoReply(from, subject, content);
            String replySubject = "Re: " + subject;
            
            // Enviar respuesta
            responder.sendEmail(GMAIL_USERNAME, from, replySubject, autoReply);
            
            System.out.println("✅ Respuesta automática enviada a: " + from);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando email: " + e.getMessage());
        }
    }
    
    /**
     * Extrae el contenido de texto del email
     */
    private String getTextContent(Message message) throws Exception {
        if (message.isMimeType("text/plain")) {
            return (String) message.getContent();
        } else if (message.isMimeType("text/html")) {
            return (String) message.getContent();
        } else if (message.isMimeType("multipart/*")) {
            Multipart multipart = (Multipart) message.getContent();
            return getTextFromMultipart(multipart);
        } else {
            return "Contenido no de texto";
        }
    }
    
    private String getTextFromMultipart(Multipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        int count = multipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString());
            }
        }
        return result.toString();
    }
    
    /**
     * Detecta si es un email automático para evitar loops
     */
    private boolean isAutoReplyEmail(String subject, String from) {
        // Evitar responder a emails automáticos
        String subjectLower = subject.toLowerCase();
        return subjectLower.contains("auto-reply") || 
               subjectLower.contains("automatic") ||
               subjectLower.contains("no-reply") ||
               subjectLower.contains("[mi-servidor]") ||
               from.contains("noreply") ||
               from.contains("no-reply");
    }
    
    /**
     * Genera respuesta automática con información del proyecto
     */
    private String generateAutoReply(String senderEmail, String originalSubject, String originalContent) {
        return "¡Hola!\n\n" +
               "Gracias por contactarme. He recibido tu mensaje y te respondo automáticamente.\n\n" +
               "📋 INFORMACIÓN DEL PROYECTO:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "🚀 Proyecto: Sistema de Email HTTP Personalizado\n" +
               "👨‍💻 Desarrollador: Jairo Jairo Jairo\n" +
               "📧 Email: JairoJairoJairo@gmail.com\n" +
               "🌐 Servidor: https://340c-181-188-162-193.ngrok-free.app\n\n" +
               "🔧 CARACTERÍSTICAS:\n" +
               "• ✅ Servidor HTTP de emails independiente\n" +
               "• ✅ Interfaz web para envío de emails\n" +
               "• ✅ API REST para aplicaciones\n" +
               "• ✅ Monitoreo automático de emails entrantes\n" +
               "• ✅ Respuestas automáticas personalizadas\n" +
               "• ✅ Relay a través de Gmail\n" +
               "• ✅ Acceso global con ngrok\n\n" +
               "📊 TECNOLOGÍAS UTILIZADAS:\n" +
               "• Java + JavaMail API\n" +
               "• Servidor HTTP personalizado\n" +
               "• Gmail SMTP/IMAP\n" +
               "• ngrok para túneles\n" +
               "• HTML/CSS para interfaz web\n\n" +
               "🎯 TU MENSAJE ORIGINAL:\n" +
               "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
               "Asunto: " + originalSubject + "\n" +
               "Contenido: " + (originalContent.length() > 200 ? originalContent.substring(0, 200) + "..." : originalContent) + "\n\n" +
               "Si necesitas una respuesta personalizada, te contactaré pronto.\n\n" +
               "¡Saludos!\n" +
               "Jairo\n\n" +
               "---\n" +
               "🤖 Este es un mensaje automático generado por mi servidor HTTP personalizado.\n" +
               "📅 Fecha: " + new Date() + "\n" +
               "🔗 Prueba el servidor: https://340c-181-188-162-193.ngrok-free.app";
    }
    
    /**
     * Detiene el monitoreo
     */
    public void stopMonitoring() {
        monitoring = false;
        System.out.println("🛑 Deteniendo monitoreo de emails...");
    }
    
    /**
     * Cierra conexiones
     */
    private void closeConnections() {
        try {
            if (inbox != null && inbox.isOpen()) {
                inbox.close(false);
            }
            if (store != null && store.isConnected()) {
                store.close();
            }
            System.out.println("🔌 Conexiones IMAP cerradas");
        } catch (Exception e) {
            System.err.println("Error cerrando conexiones: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        GmailMonitor monitor = new GmailMonitor();
        
        // Agregar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.stopMonitoring();
        }));
        
        // Iniciar monitoreo
        monitor.startMonitoring();
    }
} 