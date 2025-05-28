package servidor;

import java.util.Calendar;
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
import javax.mail.search.AndTerm;
import javax.mail.search.ComparisonTerm;
import javax.mail.search.FlagTerm;
import javax.mail.search.ReceivedDateTerm;

/**
 * Monitor que revisa SOLO emails recientes (últimas 24 horas) para responder rápidamente
 * @author MARCO
 */
public class GmailMonitorRecientes {
    
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
    
    public GmailMonitorRecientes() {
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
     * Inicia el monitoreo de emails entrantes RECIENTES
     */
    public void startMonitoring() {
        monitoring = true;
        System.out.println("📬 Iniciando monitoreo SELECTIVO de emails...");
        System.out.println("📧 Monitoreando: " + GMAIL_USERNAME);
        System.out.println("🔄 Solo emails de las últimas 24 horas");
        System.out.println("🎯 Solo responde a asuntos específicos:");
        System.out.println("   • 'test smtp'");
        System.out.println("   • 'test'");
        System.out.println("   • 'prueba smtp'");
        System.out.println("   • 'prueba'");
        System.out.println("   • que contenga 'consulta sobre tu proyecto'");
        System.out.println("⏱️ Revisando cada 10 segundos");
        
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
                checkForRecentEmails();
                Thread.sleep(10000); // Revisar cada 10 segundos
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error en monitoreo: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeConnections();
        }
    }
    
    /**
     * Revisa SOLO emails recientes (últimas 24 horas) y no leídos
     */
    private void checkForRecentEmails() {
        try {
            // 🔄 REFRESCAR LA CARPETA para detectar emails nuevos
            if (inbox.isOpen()) {
                inbox.close(false);
                inbox.open(Folder.READ_WRITE);
            }
            
            // Calcular fecha de hace 24 horas
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.HOUR_OF_DAY, -24);
            Date yesterday = calendar.getTime();
            
            // Buscar emails no leídos Y recientes
            FlagTerm unreadTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            ReceivedDateTerm recentTerm = new ReceivedDateTerm(ComparisonTerm.GT, yesterday);
            AndTerm searchTerm = new AndTerm(unreadTerm, recentTerm);
            
            Message[] recentUnreadMessages = inbox.search(searchTerm);
            
            if (recentUnreadMessages.length > 0) {
                System.out.println("📨 Encontrados " + recentUnreadMessages.length + " emails recientes nuevos");
                
                for (Message message : recentUnreadMessages) {
                    processNewEmail(message);
                    // Marcar como leído después de procesar
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            } else {
                System.out.print("🔍 Buscando emails recientes... ");
                System.out.println("(Sin emails nuevos) - " + new Date());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error revisando emails recientes: " + e.getMessage());
            // Si hay error, intentar reconectar
            try {
                reconnectIfNeeded();
            } catch (Exception reconnectError) {
                System.err.println("❌ Error reconectando: " + reconnectError.getMessage());
            }
        }
    }
    
    /**
     * Reconecta a IMAP si es necesario
     */
    private void reconnectIfNeeded() throws Exception {
        if (!store.isConnected()) {
            System.out.println("🔄 Reconectando a Gmail IMAP...");
            store.connect(IMAP_HOST, GMAIL_USERNAME, GMAIL_APP_PASSWORD);
        }
        
        if (!inbox.isOpen()) {
            inbox.open(Folder.READ_WRITE);
        }
    }
    
    /**
     * Procesa un email nuevo y envía respuesta automática SOLO si cumple criterios específicos
     */
    private void processNewEmail(Message message) {
        try {
            // Obtener información del email
            String from = ((InternetAddress) message.getFrom()[0]).getAddress();
            String subject = message.getSubject();
            String content = getTextContent(message);
            Date receivedDate = message.getReceivedDate();
            
            System.out.println("\n📨 Email detectado:");
            System.out.println("   👤 From: " + from);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🕐 Received: " + receivedDate);
            
            // ⭐ FILTRO PRINCIPAL: Solo responder a asuntos específicos
            if (!isTargetEmail(subject)) {
                System.out.println("   ⏭️ Asunto no coincide con criterios, omitiendo respuesta");
                return;
            }
            
            // Verificar si no es un email automático (evitar loops)
            if (isAutoReplyEmail(subject, from)) {
                System.out.println("   🔄 Email automático detectado, omitiendo respuesta");
                return;
            }
            
            System.out.println("   🎯 ¡CRITERIOS CUMPLIDOS! Enviando respuesta automática...");
            System.out.println("   💬 Content preview: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content));
            
            // Generar respuesta automática
            String autoReply = generateAutoReply(from, subject, content);
            String replySubject = "Re: " + subject;
            
            // Enviar respuesta
            responder.sendEmail(GMAIL_USERNAME, from, replySubject, autoReply);
            
            System.out.println("   ✅ Respuesta automática enviada a: " + from);
            System.out.println("   🎉 PROCESAMIENTO COMPLETADO!\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando email: " + e.getMessage());
        }
    }
    
    /**
     * Determina si el email debe recibir respuesta automática basado en el asunto
     */
    private boolean isTargetEmail(String subject) {
        if (subject == null) return false;
        
        String subjectLower = subject.toLowerCase().trim();
        
        // 🎯 CRITERIOS ESPECÍFICOS: Solo responder a estos asuntos
        return subjectLower.equals("test smtp") ||
               subjectLower.equals("test") ||
               subjectLower.equals("prueba smtp") ||
               subjectLower.equals("prueba") ||
               subjectLower.contains("consulta sobre tu proyecto") ||
               subjectLower.contains("test smtp");
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
               "👨‍💻 Desarrollador: Marco David Toledo\n" +
               "📧 Email: marcodavidtoledo@gmail.com\n" +
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
               "Marco\n\n" +
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
        System.out.println("🛑 Deteniendo monitoreo de emails recientes...");
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
        GmailMonitorRecientes monitor = new GmailMonitorRecientes();
        
        // Agregar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.stopMonitoring();
        }));
        
        // Iniciar monitoreo
        monitor.startMonitoring();
    }
} 