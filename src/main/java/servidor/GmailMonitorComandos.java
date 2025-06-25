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

import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;
import postgresConecction.DBConnectionManager;

/**
 * Monitor que revisa emails recientes y procesa comandos usando
 * EmailAppIndependiente
 * 
 * @author Jairo
 */
public class GmailMonitorComandos {

    // 🔧 CONFIGURACIÓN DE GMAIL IMAP
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_PORT = "993";
    private static final String GMAIL_USERNAME = "JairoJairoJairo@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "muknnpzrymdkduss";

    private Session session;
    private Store store;
    private Folder inbox;
    private GmailRelay responder;
    private EmailAppIndependiente emailApp;
    private boolean monitoring = false;

    public GmailMonitorComandos() {
        this.responder = new GmailRelay();
        
        // Usar la configuración global establecida previamente
        boolean useTecnoweb = DBConnectionManager.getActiveConfig() == DBConnectionManager.ConfigType.TECNOWEB;
        this.emailApp = new EmailAppIndependiente(useTecnoweb);
        
        System.out.println("📧 GmailMonitorComandos: Usando configuración " + 
            (useTecnoweb ? "TECNOWEB" : "LOCAL"));
        
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
        System.out.println("📬 Iniciando monitoreo HÍBRIDO de emails...");
        System.out.println("📧 Monitoreando: " + GMAIL_USERNAME);
        System.out.println("🔄 Solo emails de las últimas 24 horas");
        System.out.println("🎯 Procesamiento DUAL:");
        System.out.println("   • Comandos específicos → EmailAppIndependiente");
        System.out.println("   • Otros asuntos → Respuesta automática estándar");
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
     * Procesa un email nuevo con lógica DUAL: comandos + respuestas automáticas
     */
    private void processNewEmail(Message message) {
        try {
            // Obtener información del email
            String from = ((InternetAddress) message.getFrom()[0]).getAddress();
            String subject = message.getSubject();
            String content = getTextContent(message);
            Date receivedDate = message.getReceivedDate();

            // 🆕 CAPTURAR MESSAGE-ID para respuestas
            String messageId = message.getHeader("Message-ID") != null ? message.getHeader("Message-ID")[0] : null;

            System.out.println("\n📨 Email detectado:");
            System.out.println("   👤 From: " + from);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🕐 Received: " + receivedDate);
            if (messageId != null) {
                System.out.println("   🆔 Message-ID: " + messageId);
            }
            System.out.println(
                    "   💬 Content preview: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content));

            // 🎯 NUEVA LÓGICA: Buscar comandos en ASUNTO Y CONTENIDO
            String commandFound = extractCommand(subject, content);

            if (commandFound != null) {
                System.out.println("   🤖 ¡COMANDO DETECTADO: " + commandFound + "!");
                System.out.println("   📍 Encontrado en: " + (isCommandEmail(subject) ? "asunto" : "contenido"));

                // 🆕 PROCESAR COMANDO CON SOPORTE PARA RESPUESTA
                processEmailCommandWithReply(from, commandFound, content, subject, messageId);
                return;
            }

            // 🎯 FILTRO PARA RESPUESTAS AUTOMÁTICAS ESTÁNDAR
            if (!isTargetEmail(subject)) {
                System.out.println("   ⏭️ No se encontraron comandos válidos, omitiendo respuesta");
                return;
            }

            // Verificar si no es un email automático (evitar loops)
            if (isAutoReplyEmail(subject, from)) {
                System.out.println("   🔄 Email automático detectado, omitiendo respuesta");
                return;
            }

            System.out.println("   🎯 ¡CRITERIOS CUMPLIDOS! Enviando respuesta automática estándar...");
            System.out.println(
                    "   💬 Content preview: " + (content.length() > 100 ? content.substring(0, 100) + "..." : content));

            // Generar respuesta automática estándar
            String autoReply = generateAutoReply(from, subject, content);

            // 🆕 RESPONDER AL EMAIL ORIGINAL en lugar de crear uno nuevo
            if (messageId != null) {
                responder.replyToEmail(from, subject, autoReply, messageId);
                System.out.println("   📧 Respuesta enviada como REPLY al email original");
            } else {
                // Fallback: envío tradicional si no hay Message-ID
                String replySubject = "Re: " + subject;
                responder.sendEmail(GMAIL_USERNAME, from, replySubject, autoReply);
                System.out.println("   📧 Respuesta enviada como email nuevo (sin Message-ID)");
            }

            System.out.println("   ✅ Respuesta automática enviada a: " + from);
            System.out.println("   🎉 PROCESAMIENTO COMPLETADO!\n");

        } catch (Exception e) {
            System.err.println("❌ Error procesando email: " + e.getMessage());
        }
    }

    /**
     * 🆕 Procesa comandos de email con soporte para respuesta como reply
     */
    private void processEmailCommandWithReply(String from, String command, String content, String originalSubject,
            String messageId) {
        try {
            // Procesar comando usando el nuevo método que soporta reply
            System.out.println("   💬 Configurando respuesta como reply al mensaje: " + messageId);

            // Usar el método sobrecargado que acepta originalSubject y messageId
            emailApp.processEmailCommand(from, command, content, originalSubject, messageId);

            System.out.println("   📧 Comando procesado con soporte para reply");

        } catch (Exception e) {
            System.err.println("❌ Error procesando comando con reply: " + e.getMessage());
            // Fallback: usar el método original
            emailApp.processEmailCommand(from, command, content);
        }
    }

    /**
     * 🎯 NUEVO: Extrae comando del asunto O contenido del email
     */
    private String extractCommand(String subject, String content) {
        // 1. Primero buscar en el asunto
        if (isCommandEmail(subject)) {
            return subject.trim();
        }

        // 2. Luego buscar en el contenido
        String commandInContent = extractCommandFromContent(content);
        if (commandInContent != null) {
            return commandInContent;
        }

        return null;
    }

    /**
     * 🔍 Extrae comando del contenido del email (para respuestas)
     */
    private String extractCommandFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        // Limpiar contenido: quitar HTML, quotes, etc.
        String cleanContent = cleanEmailContent(content);

        // Buscar líneas que contengan comandos válidos
        String[] lines = cleanContent.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            // Verificar si la línea es un comando válido
            if (isValidCommandLine(line)) {
                System.out.println("   🔍 Comando encontrado en contenido: " + line);
                return line;
            }
        }

        return null;
    }

    /**
     * 🧹 Limpia el contenido del email para extraer texto plano
     */
    private String cleanEmailContent(String content) {
        if (content == null)
            return "";

        // Quitar HTML tags
        String cleaned = content.replaceAll("<[^>]+>", " ");

        // Decodificar entidades HTML comunes
        cleaned = cleaned.replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&#39;", "'");

        // Quitar quoted-printable encoding
        cleaned = cleaned.replace("=C3=A9", "é")
                .replace("=C3=B1", "ñ")
                .replace("=C3=A1", "á")
                .replace("=C3=AD", "í")
                .replace("=C3=B3", "ó")
                .replace("=C3=BA", "ú");

        // Quitar líneas de citado (que empiecen con >)
        String[] lines = cleaned.split("\n");
        StringBuilder result = new StringBuilder();

        for (String line : lines) {
            line = line.trim();
            // Omitir líneas vacías, de citado, de headers de email
            if (!line.isEmpty() &&
                    !line.startsWith(">") &&
                    !line.startsWith("El ") &&
                    !line.contains("escribió:") &&
                    !line.startsWith("On ") &&
                    !line.contains("wrote:")) {
                result.append(line).append("\n");
            }
        }

        return result.toString().trim();
    }

    /**
     * ✅ Verifica si una línea contiene un comando válido
     */
    private boolean isValidCommandLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return false;
        }

        String lineLower = line.toLowerCase().trim();

        // Comandos exactos (singular y plural)
        if (lineLower.equals("help") ||
                lineLower.equals("usuario get") || lineLower.equals("usuarios get") ||
                lineLower.equals("producto get") || lineLower.equals("productos get") ||
                lineLower.equals("categoria get") || lineLower.equals("categorias get") ||
                lineLower.equals("cliente get") || lineLower.equals("clientes get") ||
                lineLower.equals("tipo_pago get") || lineLower.equals("tipos_pago get") ||
                // 🛒 COMANDOS DEL CARRITO
                lineLower.equals("carrito get") ||
                lineLower.equals("carrito clear") ||
                lineLower.equals("checkout") ||
                lineLower.equals("ventas get") ||
                lineLower.equals("pago get")) {
            return true;
        }

        // Comandos que empiezan con... (singular y plural)
        if (lineLower.startsWith("registrar ") ||
                lineLower.startsWith("usuario ") || lineLower.startsWith("usuarios ") ||
                lineLower.startsWith("producto ") || lineLower.startsWith("productos ") ||
                lineLower.startsWith("categoria ") || lineLower.startsWith("categorias ") ||
                lineLower.startsWith("cliente ") || lineLower.startsWith("clientes ") ||
                lineLower.startsWith("tipo_pago ") || lineLower.startsWith("tipos_pago ") ||
                // 🛒 COMANDOS DEL CARRITO CON PARÁMETROS
                lineLower.startsWith("carrito add ") ||
                lineLower.startsWith("carrito remove ") ||
                lineLower.startsWith("ventas get ") ||
                lineLower.startsWith("pago ")) {
            return true;
        }

        return false;
    }

    /**
     * Verifica si el email contiene un comando específico para
     * EmailAppIndependiente
     */
    private boolean isCommandEmail(String subject) {
        if (subject == null)
            return false;
        String subjectLower = subject.toLowerCase().trim();

        // 🎯 COMANDOS ESPECÍFICOS para EmailAppIndependiente (singular y plural)
        return subjectLower.startsWith("registrar ") ||
                subjectLower.startsWith("usuario ") || subjectLower.startsWith("usuarios ") ||
                subjectLower.equals("usuario get") || subjectLower.equals("usuarios get") ||
                subjectLower.startsWith("producto ") || subjectLower.startsWith("productos ") ||
                subjectLower.equals("producto get") || subjectLower.equals("productos get") ||
                subjectLower.startsWith("categoria ") || subjectLower.startsWith("categorias ") ||
                subjectLower.equals("categoria get") || subjectLower.equals("categorias get") ||
                subjectLower.startsWith("cliente ") || subjectLower.startsWith("clientes ") ||
                subjectLower.equals("cliente get") || subjectLower.equals("clientes get") ||
                subjectLower.startsWith("tipo_pago ") || subjectLower.startsWith("tipos_pago ") ||
                subjectLower.equals("tipo_pago get") || subjectLower.equals("tipos_pago get") ||
                subjectLower.equals("help") ||
                // 🛒 COMANDOS DEL CARRITO Y E-COMMERCE
                subjectLower.startsWith("carrito ") ||
                subjectLower.equals("carrito get") ||
                subjectLower.equals("carrito clear") ||
                subjectLower.equals("checkout") ||
                subjectLower.startsWith("ventas ") ||
                subjectLower.equals("ventas get") ||
                subjectLower.startsWith("pago ");
    }

    /**
     * Determina si el email debe recibir respuesta automática estándar
     */
    private boolean isTargetEmail(String subject) {
        if (subject == null)
            return false;

        String subjectLower = subject.toLowerCase().trim();

        // 🎯 CRITERIOS PARA RESPUESTA AUTOMÁTICA ESTÁNDAR Y COMANDOS
        return subjectLower.equals("test smtp") ||
                subjectLower.equals("test") ||
                subjectLower.equals("prueba smtp") ||
                subjectLower.equals("prueba") ||
                subjectLower.contains("consulta sobre tu proyecto") ||
                subjectLower.contains("test smtp") ||
                // ✅ COMANDOS DEL SISTEMA (singular y plural)
                subjectLower.startsWith("registrar ") ||
                subjectLower.equals("help") ||
                subjectLower.startsWith("usuario ") || subjectLower.startsWith("usuarios ") ||
                subjectLower.equals("usuario get") || subjectLower.equals("usuarios get") ||
                subjectLower.startsWith("producto ") || subjectLower.startsWith("productos ") ||
                subjectLower.equals("producto get") || subjectLower.equals("productos get") ||
                subjectLower.startsWith("categoria ") || subjectLower.startsWith("categorias ") ||
                subjectLower.equals("categoria get") || subjectLower.equals("categorias get") ||
                subjectLower.startsWith("cliente ") || subjectLower.startsWith("clientes ") ||
                subjectLower.equals("cliente get") || subjectLower.equals("clientes get") ||
                subjectLower.startsWith("tipo_pago ") || subjectLower.startsWith("tipos_pago ") ||
                subjectLower.equals("tipo_pago get") || subjectLower.equals("tipos_pago get") ||
                // 🛒 COMANDOS DEL CARRITO Y E-COMMERCE
                subjectLower.startsWith("carrito ") ||
                subjectLower.equals("carrito get") ||
                subjectLower.equals("carrito clear") ||
                subjectLower.equals("checkout") ||
                subjectLower.startsWith("ventas ") ||
                subjectLower.equals("ventas get") ||
                subjectLower.startsWith("pago ");
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
                subjectLower.contains("[servidor independiente]") ||
                subjectLower.contains("[comando:") ||
                from.contains("noreply") ||
                from.contains("no-reply");
    }

    /**
     * Genera respuesta automática estándar con información del proyecto
     */
    private String generateAutoReply(String senderEmail, String originalSubject, String originalContent) {
        return "¡Hola!\n\n" +
                "Gracias por contactarme. He recibido tu mensaje y te respondo automáticamente.\n\n" +
                "📋 INFORMACIÓN DEL PROYECTO:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "🚀 Proyecto: Sistema de Email HTTP Personalizado + CRUD via Email\n" +
                "👨‍💻 Desarrollador: Jairo Jairo Jairo\n" +
                "📧 Email: JairoJairoJairo@gmail.com\n" +
                "🌐 Servidor: https://340c-181-188-162-193.ngrok-free.app\n\n" +
                "🔧 CARACTERÍSTICAS:\n" +
                "• ✅ Servidor HTTP de emails independiente\n" +
                "• ✅ Interfaz web para envío de emails\n" +
                "• ✅ API REST para aplicaciones\n" +
                "• ✅ Monitoreo automático de emails entrantes\n" +
                "• ✅ Respuestas automáticas personalizadas\n" +
                "• ✅ CRUD via comandos de email\n" +
                "• ✅ Base de datos PostgreSQL local\n" +
                "• ✅ Relay a través de Gmail\n" +
                "• ✅ Acceso global con ngrok\n\n" +
                "📊 TECNOLOGÍAS UTILIZADAS:\n" +
                "• Java + JavaMail API\n" +
                "• PostgreSQL Database\n" +
                "• Servidor HTTP personalizado\n" +
                "• Gmail SMTP/IMAP\n" +
                "• ngrok para túneles\n" +
                "• HTML/CSS para interfaz web\n\n" +
                "🎯 COMANDOS DISPONIBLES VIA EMAIL:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "📧 Envía un email con el comando en el asunto:\n" +
                "• 'usuario get' - Lista todos los usuarios\n" +
                "• 'usuario get 1' - Obtiene usuario por ID\n" +
                "• 'help' - Muestra comandos disponibles\n\n" +
                "🎯 TU MENSAJE ORIGINAL:\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Asunto: " + originalSubject + "\n" +
                "Contenido: "
                + (originalContent.length() > 200 ? originalContent.substring(0, 200) + "..." : originalContent)
                + "\n\n" +
                "Si necesitas una respuesta personalizada o ejecutar comandos CRUD, te contactaré pronto.\n\n" +
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
        System.out.println("🛑 Deteniendo monitoreo de emails híbrido...");
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
        GmailMonitorComandos monitor = new GmailMonitorComandos();

        // Agregar shutdown hook para cerrar limpiamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.stopMonitoring();
        }));

        System.out.println("🎯 MONITOR HÍBRIDO INICIADO:");
        System.out.println("📧 Procesa comandos CRUD + respuestas automáticas");
        System.out.println("🗄️ Base de datos: EcommerceTool en localhost");
        System.out.println("📬 Solo emails de las últimas 24 horas");

        // Iniciar monitoreo
        monitor.startMonitoring();
    }
}