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

/**
 * Monitor completo que maneja TODOS los comandos correctamente
 * @author Jairo
 */
public class GmailMonitorCompleto {
    
    // 🔧 CONFIGURACIÓN DE GMAIL IMAP
    private static final String IMAP_HOST = "imap.gmail.com";
    private static final String IMAP_PORT = "993";
    private static final String GMAIL_USERNAME = "marcodavidtoledo@gmail.com";
    private static final String GMAIL_APP_PASSWORD = "muknnpzrymdkduss";
    
    private Session session;
    private Store store;
    private Folder inbox;
    private GmailRelay responder;
    private EmailAppIndependiente emailApp;
    private boolean monitoring = false;
    
    public GmailMonitorCompleto() {
        this.responder = new GmailRelay();
        this.emailApp = new EmailAppIndependiente();
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
     * Inicia el monitoreo completo de emails
     */
    public void startMonitoring() {
        monitoring = true;
        System.out.println("📬 Iniciando MONITOR COMPLETO de emails...");
        System.out.println("📧 Monitoreando: " + GMAIL_USERNAME);
        System.out.println("🎯 COMANDOS SOPORTADOS:");
        System.out.println("   • registrar nombre apellido telefono genero");
        System.out.println("   • usuario get [id]");
        System.out.println("   • producto get [id]");
        System.out.println("   • categoria get [id]");
        System.out.println("   • cliente get [id]");
        System.out.println("   • tipo_pago get [id]");
        System.out.println("   • help");
        System.out.println("🔧 CARACTERÍSTICAS:");
        System.out.println("   ✅ Validación de comandos");
        System.out.println("   ✅ Sugerencias de corrección");
        System.out.println("   ✅ Registro automático de email");
        System.out.println("   ✅ HTML moderno");
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
     * Revisa emails recientes
     */
    private void checkForRecentEmails() {
        try {
            // Refrescar carpeta
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
                System.out.println("📨 Encontrados " + recentUnreadMessages.length + " emails nuevos");
                
                for (Message message : recentUnreadMessages) {
                    processNewEmail(message);
                    // Marcar como leído después de procesar
                    message.setFlag(Flags.Flag.SEEN, true);
                }
            } else {
                System.out.print("🔍 Monitoreando... ");
                System.out.println("(Sin emails nuevos) - " + new Date());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error revisando emails: " + e.getMessage());
            try {
                reconnectIfNeeded();
            } catch (Exception reconnectError) {
                System.err.println("❌ Error reconectando: " + reconnectError.getMessage());
            }
        }
    }
    
    /**
     * Reconecta si es necesario
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
     * Procesa un email nuevo con validación completa
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
            
            // Verificar si no es un email automático
            if (isAutoReplyEmail(subject, from)) {
                System.out.println("   🔄 Email automático detectado, omitiendo");
                return;
            }
            
            // ✅ PROCESAR COMANDO CON VALIDACIÓN COMPLETA
            processCommandWithValidation(from, subject, content);
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando email: " + e.getMessage());
        }
    }
    
    /**
     * Procesa comandos con validación y sugerencias de error
     */
    private void processCommandWithValidation(String from, String subject, String content) {
        try {
            if (subject == null || subject.trim().isEmpty()) {
                sendErrorWithSuggestions(from, "Comando vacío", 
                    "El asunto del email está vacío. Ejemplos válidos:\n" +
                    "• registrar Juan Pérez 123456789 masculino\n" +
                    "• usuario get\n" +
                    "• help");
                return;
            }
            
            String comando = subject.toLowerCase().trim();
            String[] parts = comando.split("\\s+");
            
            System.out.println("   🎯 Procesando comando: " + comando);
            
            if (parts.length == 0) {
                sendErrorWithSuggestions(from, "Comando inválido", "No se pudo analizar el comando.");
                return;
            }
            
            String action = parts[0];
            
            // ✅ COMANDO REGISTRAR (con email automático)
            if ("registrar".equals(action)) {
                processRegistrarCommand(from, parts, comando);
                return;
            }
            
            // ✅ OTROS COMANDOS (requieren autenticación)
            if (isValidCommand(action)) {
                emailApp.processEmailCommand(from, subject, content);
                System.out.println("   ✅ Comando procesado por EmailApp");
                return;
            }
            
            // ❌ COMANDO NO RECONOCIDO
            sendCommandNotRecognized(from, comando);
            
        } catch (Exception e) {
            System.err.println("❌ Error validando comando: " + e.getMessage());
            sendErrorWithSuggestions(from, "Error interno", 
                "Ocurrió un error procesando tu comando. Intenta de nuevo.");
        }
    }
    
    /**
     * Procesa comando de registro con validación completa
     */
    private void processRegistrarCommand(String from, String[] parts, String comando) {
        try {
            // Validar número de parámetros
            if (parts.length < 5) {
                String error = "❌ Faltan parámetros para el registro\n\n";
                error += "📝 **Comando recibido:** " + comando + "\n";
                error += "📝 **Parámetros encontrados:** " + (parts.length - 1) + " de 4 requeridos\n\n";
                error += "✅ **Formato correcto:**\n";
                error += "`registrar nombre apellido telefono genero`\n\n";
                error += "✅ **Ejemplos válidos:**\n";
                error += "• `registrar Juan Pérez 123456789 masculino`\n";
                error += "• `registrar María González 987654321 femenino`\n";
                error += "• `registrar Carlos Ruiz 555123456 masculino`\n\n";
                error += "ℹ️ **Tu email se registrará automáticamente:** " + from;
                
                sendErrorWithSuggestions(from, "Error en registro", error);
                return;
            }
            
            if (parts.length > 5) {
                String error = "❌ Demasiados parámetros para el registro\n\n";
                error += "📝 **Comando recibido:** " + comando + "\n";
                error += "📝 **Parámetros encontrados:** " + (parts.length - 1) + " (máximo 4)\n\n";
                error += "✅ **Formato correcto:**\n";
                error += "`registrar nombre apellido telefono genero`\n\n";
                error += "💡 **Tip:** Si tu nombre/apellido tiene espacios, úsalos sin espacios o con guiones:\n";
                error += "• `registrar Juan-Carlos Pérez-López 123456789 masculino`";
                
                sendErrorWithSuggestions(from, "Error en registro", error);
                return;
            }
            
            // Extraer parámetros
            String nombre = parts[1];
            String apellido = parts[2];
            String telefono = parts[3];
            String genero = parts[4];
            
            // Validar género
            if (!genero.equals("masculino") && !genero.equals("femenino") && 
                !genero.equals("m") && !genero.equals("f")) {
                String error = "❌ Género inválido: " + genero + "\n\n";
                error += "✅ **Valores válidos para género:**\n";
                error += "• `masculino` o `m`\n";
                error += "• `femenino` o `f`\n\n";
                error += "📝 **Tu comando:** " + comando + "\n";
                error += "🔧 **Corrección:** registrar " + nombre + " " + apellido + " " + telefono + " masculino";
                
                sendErrorWithSuggestions(from, "Error en género", error);
                return;
            }
            
            // Normalizar género
            if ("m".equals(genero)) genero = "masculino";
            if ("f".equals(genero)) genero = "femenino";
            
            // Validar teléfono (básico)
            if (telefono.length() < 6 || !telefono.matches("\\d+")) {
                String error = "❌ Teléfono inválido: " + telefono + "\n\n";
                error += "✅ **Formato válido:**\n";
                error += "• Solo números\n";
                error += "• Mínimo 6 dígitos\n";
                error += "• Ejemplos: 123456789, 67733399, 987654321\n\n";
                error += "📝 **Tu comando:** " + comando + "\n";
                error += "🔧 **Sugerencia:** registrar " + nombre + " " + apellido + " 123456789 " + genero;
                
                sendErrorWithSuggestions(from, "Error en teléfono", error);
                return;
            }
            
            System.out.println("   ✅ Comando válido, procesando registro...");
            System.out.println("   👤 Nombre: " + nombre + " " + apellido);
            System.out.println("   📞 Teléfono: " + telefono);
            System.out.println("   ⚧ Género: " + genero);
            System.out.println("   📧 Email: " + from + " (automático)");
            
            // ✅ PROCESAR REGISTRO CON EMAIL AUTOMÁTICO
            emailApp.processEmailCommand(from, "registrar " + nombre + " " + apellido + " " + telefono + " " + genero, "");
            System.out.println("   🎉 Registro enviado a EmailApp");
            
        } catch (Exception e) {
            System.err.println("❌ Error procesando registro: " + e.getMessage());
            sendErrorWithSuggestions(from, "Error en registro", 
                "Error interno procesando el registro: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si es un comando válido
     */
    private boolean isValidCommand(String action) {
        return action.equals("usuario") || action.equals("producto") || 
               action.equals("categoria") || action.equals("cliente") || 
               action.equals("tipo_pago") || action.equals("pedido") || 
               action.equals("help");
    }
    
    /**
     * Envía error cuando comando no es reconocido
     */
    private void sendCommandNotRecognized(String from, String comando) {
        String error = "❌ Comando no reconocido: `" + comando + "`\n\n";
        error += "✅ **Comandos disponibles:**\n\n";
        error += "📝 **Registro:**\n";
        error += "• `registrar nombre apellido telefono genero`\n\n";
        error += "📊 **Consultas:**\n";
        error += "• `usuario get` - Lista todos los usuarios\n";
        error += "• `usuario get 5` - Usuario específico por ID\n";
        error += "• `producto get` - Lista todos los productos\n";
        error += "• `categoria get` - Lista todas las categorías\n";
        error += "• `cliente get` - Lista todos los clientes\n";
        error += "• `tipo_pago get` - Lista tipos de pago\n";
        error += "• `pedido get` - Lista todos los pedidos\n";
        error += "• `pedido add URL_MAPS referencia` - Crear pedido con dirección\n\n";
        error += "❓ **Ayuda:**\n";
        error += "• `help` - Ver ayuda completa\n\n";
        error += "💡 **¿Quisiste decir?**\n";
        
        // Sugerencias inteligentes
        if (comando.contains("register")) {
            error += "• `registrar` (en lugar de register)\n";
        }
        if (comando.contains("user")) {
            error += "• `usuario get` (en lugar de user)\n";
        }
        if (comando.contains("product")) {
            error += "• `producto get` (en lugar de product)\n";
        }
        if (comando.contains("order")) {
            error += "• `pedido get` (en lugar de order)\n";
        }
        if (comando.contains("ayuda")) {
            error += "• `help` (en lugar de ayuda)\n";
        }
        
        sendErrorWithSuggestions(from, "Comando no reconocido", error);
    }
    
    /**
     * Envía email de error con sugerencias
     */
    private void sendErrorWithSuggestions(String email, String title, String mensaje) {
        try {
            // Usar HtmlRes para generar error moderno
            Class<?> htmlResClass = Class.forName("librerias.HtmlRes");
            java.lang.reflect.Method errorMethod = htmlResClass.getMethod("generateError", String.class, String.class);
            String htmlError = (String) errorMethod.invoke(null, title, mensaje);
            
            responder.sendEmail("servidor-completo@localhost", email, 
                "[Error] " + title, htmlError);
            
            System.out.println("   📧 Error enviado con sugerencias");
            
        } catch (Exception e) {
            System.err.println("❌ Error enviando sugerencias: " + e.getMessage());
        }
    }
    
    /**
     * Extrae contenido de texto del email
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
     * Detecta emails automáticos
     */
    private boolean isAutoReplyEmail(String subject, String from) {
        String subjectLower = subject.toLowerCase();
        return subjectLower.contains("auto-reply") || 
               subjectLower.contains("automatic") ||
               subjectLower.contains("no-reply") ||
               subjectLower.contains("[mi-servidor]") ||
               subjectLower.contains("[emailapp]") ||
               from.contains("noreply") ||
               from.contains("no-reply");
    }
    
    /**
     * Detiene el monitoreo
     */
    public void stopMonitoring() {
        monitoring = false;
        System.out.println("🛑 Deteniendo monitor completo...");
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
            System.out.println("🔌 Conexiones cerradas");
        } catch (Exception e) {
            System.err.println("Error cerrando conexiones: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        GmailMonitorCompleto monitor = new GmailMonitorCompleto();
        
        // Shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            monitor.stopMonitoring();
        }));
        
        // Iniciar monitoreo
        monitor.startMonitoring();
    }
} 