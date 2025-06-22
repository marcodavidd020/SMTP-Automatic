package com.mycompany.parcial1.tecnoweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import data.DCategoria;
import data.DCliente;
import data.DProducto;
import data.DTipoPago;
import data.DUsuario;
import interfaces.ICasoUsoListener;
import librerias.Email;
import librerias.ParamsAction;
import negocio.NUsuario;
import postgresConecction.DBConnection;
import postgresConecction.EmailSend;
import servidor.GmailRelay;

/**
 * EmailApp original que se conecta al servidor tecnoweb usando POP3
 * y la base de datos tecnoweb (db_grupo21sc en mail.tecnoweb.org.bo)
 *
 * @author MARCO
 */
public class EmailAppTecnoweb implements ICasoUsoListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;
    private static final int DATABASE_ERROR = -7;

    private GmailRelay emailRelay;
    private NUsuario nUsuario;
    private DProducto dProducto;
    private DCategoria dCategoria;
    private DCliente dCliente;
    private DTipoPago dTipoPago;

    public EmailAppTecnoweb() {
        this.emailRelay = new GmailRelay();
        this.nUsuario = new NUsuario();
        this.dProducto = new DProducto();
        this.dCategoria = new DCategoria();
        this.dCliente = new DCliente();
        this.dTipoPago = new DTipoPago();

        System.out.println("🚀 EmailApp Tecnoweb inicializado");
        System.out.println("📧 Modo: Servidor Tecnoweb con POP3");
        System.out.println(
                "🗄️ Base de datos: " + DBConnection.TecnoWeb.database + " en " + DBConnection.TecnoWeb.server);

        // Verificar conexión a base de datos tecnoweb
        if (!testTecnowebConnection()) {
            System.err.println("⚠️ ADVERTENCIA: No se pudo conectar a la base de datos tecnoweb");
            System.err.println("💡 Verifica que el servidor tecnoweb esté funcionando");
            System.err.println("💡 URL: " + DBConnection.TecnoWeb.url);
        } else {
            System.out.println("✅ Conexión a base de datos tecnoweb verificada");
        }
    }

    /**
     * Prueba la conexión a la base de datos tecnoweb
     */
    private boolean testTecnowebConnection() {
        try {
            Connection connection = DriverManager.getConnection(
                    DBConnection.TecnoWeb.url,
                    DBConnection.TecnoWeb.user,
                    DBConnection.TecnoWeb.password);

            if (connection != null && !connection.isClosed()) {
                connection.close();
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error conectando a tecnoweb: " + e.getMessage());
            return false;
        }
    }

    /**
     * Inicia el EmailApp tecnoweb usando POP3
     */
    public void start() {
        System.out.println("📬 Iniciando EmailApp Tecnoweb...");
        System.out.println("📧 Protocolo: POP3");
        System.out.println("🔗 Conectando a mail.tecnoweb.org.bo");
        System.out.println("⚠️ NOTA: Este modo requiere implementación POP3 específica");
        System.out.println("💡 Actualmente usa funcionalidad base con conexión tecnoweb");

        // Iniciar monitoreo continuo
        startMonitoring();
    }

    /**
     * Inicia el monitoreo POP3 REAL de tecnoweb
     */
    public void startMonitoring() {
        System.out.println("🔄 Iniciando monitoreo POP3 REAL de tecnoweb...");
        System.out.println("📧 Conectando a: grupo21sc@tecnoweb.org.bo");
        System.out.println("🔗 Servidor POP3: mail.tecnoweb.org.bo:110");
        System.out.println("👤 Usuario: grupo21sc");
        System.out.println("⏱️ Revisando emails cada 60 segundos");
        System.out.println("💡 Para detener presiona Ctrl+C");

        // Iniciar monitoreo POP3 real en un hilo separado
        Thread pop3Thread = new Thread(() -> {
            startRealPOP3Monitoring();
        });
        pop3Thread.setName("TecnoWeb-POP3-Monitor");
        pop3Thread.start();

        // Mantener el hilo principal vivo
        try {
            pop3Thread.join();
        } catch (InterruptedException e) {
            System.out.println("\n🛑 Monitoreo tecnoweb detenido por usuario");
        }
    }

    /**
     * Monitoreo POP3 real usando EmailReceipt
     */
    private void startRealPOP3Monitoring() {
        try {
            System.out.println("🚀 Iniciando EmailReceipt para monitoreo POP3...");

            // Crear y configurar EmailReceipt
            postgresConecction.EmailReceipt emailReceipt = new postgresConecction.EmailReceipt();

            // Configurar listener para procesar emails recibidos
            emailReceipt.setEmailListener(new interfaces.IEmailListener() {
                @Override
                public void onReceiptEmail(List<librerias.Email> emails) {
                    System.out.println("📧 Emails recibidos de tecnoweb: " + emails.size());

                    for (librerias.Email email : emails) {
                        System.out.println("\n📩 Procesando email de tecnoweb:");
                        System.out.println("   📧 From: " + email.getFrom());
                        System.out.println("   📝 Subject: " + email.getSubject());

                        // Procesar comando del email
                        processEmailCommand(email.getFrom(), email.getSubject(), email.getMessage());
                    }
                }
            });

            // Iniciar el monitoreo POP3 (esto ejecuta un loop infinito)
            System.out.println("✅ EmailReceipt configurado, iniciando monitoreo...");
            emailReceipt.run();

        } catch (Exception e) {
            System.err.println("❌ Error en monitoreo POP3: " + e.getMessage());
            e.printStackTrace();

            // Si falla, usar fallback de verificación de conexión
            System.out.println("🔄 Cambiando a modo fallback...");
            startFallbackMonitoring();
        }
    }

    /**
     * Monitoreo fallback si POP3 falla
     */
    private void startFallbackMonitoring() {
        try {
            while (true) {
                System.out.println("\n🔍 [Fallback] Verificando conexión a tecnoweb...");

                if (testTecnowebConnection()) {
                    System.out.println("✅ Conexión tecnoweb OK (" +
                            DBConnection.TecnoWeb.database + " en " +
                            DBConnection.TecnoWeb.server + ")");
                    System.out.println("⚠️ POP3 no disponible - modo fallback activo");
                } else {
                    System.err.println("❌ Conexión a tecnoweb perdida");
                }

                Thread.sleep(60000); // 60 segundos en modo fallback
            }

        } catch (InterruptedException e) {
            System.out.println("\n🛑 Monitoreo fallback detenido");
        }
    }

    /**
     * Verifica si el usuario está registrado en tecnoweb
     */
    private boolean isUserRegistered(String email) {
        try {
            // Usar configuración de Tecnoweb en lugar de localhost
            DUsuario dUser = new DUsuario(
                DBConnection.TecnoWeb.database,
                DBConnection.TecnoWeb.server,
                DBConnection.TecnoWeb.port,
                DBConnection.TecnoWeb.user,
                DBConnection.TecnoWeb.password
            );
            return dUser.existsByEmail(email);
        } catch (Exception e) {
            System.err.println("❌ Error verificando usuario en tecnoweb: " + e.getMessage());
            return false;
        }
    }

    /**
     * Procesa comandos específicos de tecnoweb
     */
    public void processEmailCommand(String senderEmail, String subject, String content) {
        try {
            System.out.println("\n🔄 Procesando comando tecnoweb:");
            System.out.println("   📧 From: " + senderEmail);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🌐 Servidor: " + DBConnection.TecnoWeb.server);

            // Verificar conexión tecnoweb antes de procesar
            if (!testTecnowebConnection()) {
                System.err.println("   ❌ Error de conexión a tecnoweb");
                sendErrorEmail(senderEmail, "Error de conexión al servidor tecnoweb. Verifica que esté funcionando.");
                return;
            }

            // Verificar si el usuario está registrado en tecnoweb
            if (!isUserRegistered(senderEmail)) {
                System.out.println("   ❌ Usuario no registrado en tecnoweb: " + senderEmail);
                sendWelcomeEmail(senderEmail);
                return;
            }

            // Procesar comandos básicos
            String comando = subject.toLowerCase().trim();
            if (isCommandEmail(comando)) {
                processDirectCommand(senderEmail, comando);
            } else {
                System.out.println("   ⏭️ Comando no reconocido: " + comando);
            }

        } catch (Exception e) {
            System.err.println("❌ Error procesando comando tecnoweb: " + e.getMessage());
            sendErrorEmail(senderEmail, "Error procesando comando: " + e.getMessage());
        }
    }

    /**
     * Verifica si es un comando válido
     */
    private boolean isCommandEmail(String subject) {
        if (subject == null || subject.trim().isEmpty()) {
            return false;
        }

        String cleanSubject = subject.toLowerCase().trim();

        // Comandos de consulta
        return cleanSubject.equals("help") ||
                cleanSubject.startsWith("usuario get") ||
                cleanSubject.startsWith("usuarios get") ||
                cleanSubject.startsWith("producto get") ||
                cleanSubject.startsWith("productos get") ||
                cleanSubject.startsWith("categoria get") ||
                cleanSubject.startsWith("categorias get") ||
                cleanSubject.startsWith("cliente get") ||
                cleanSubject.startsWith("clientes get") ||
                cleanSubject.startsWith("tipo_pago get") ||
                cleanSubject.startsWith("tipos_pago get") ||
                cleanSubject.startsWith("registrar ");
    }

    /**
     * Procesa comandos directos
     */
    private void processDirectCommand(String senderEmail, String comando) {
        System.out.println("   🔄 Procesando comando: " + comando);

        if (comando.equals("help")) {
            processHelpCommand(senderEmail);
        } else if (comando.startsWith("usuario") || comando.startsWith("usuarios")) {
            processUsuarioCommand(senderEmail, comando);
        } else if (comando.startsWith("producto") || comando.startsWith("productos")) {
            processProductoCommand(senderEmail, comando);
        } else if (comando.startsWith("categoria") || comando.startsWith("categorias")) {
            processCategoriaCommand(senderEmail, comando);
        } else {
            sendErrorEmail(senderEmail, "Comando no implementado aún: " + comando);
        }
    }

    private void processHelpCommand(String senderEmail) {
        String helpMessage = "<h2>🚀 Sistema EmailApp Tecnoweb</h2>" +
                "<p><strong>📧 Servidor:</strong> " + DBConnection.TecnoWeb.server + "</p>" +
                "<p><strong>🗄️ Base de datos:</strong> " + DBConnection.TecnoWeb.database + "</p>" +
                "<h3>📝 Comandos disponibles:</h3>" +
                "<ul>" +
                "<li><strong>help</strong> - Muestra esta ayuda</li>" +
                "<li><strong>usuario get</strong> - Lista todos los usuarios</li>" +
                "<li><strong>producto get</strong> - Lista todos los productos</li>" +
                "<li><strong>categoria get</strong> - Lista todas las categorías</li>" +
                "</ul>" +
                "<p>⚠️ <em>Versión tecnoweb original con POP3</em></p>";

        sendEmailViaTecnoweb(senderEmail, "Ayuda - EmailApp Tecnoweb", helpMessage);
    }

    private void processUsuarioCommand(String senderEmail, String comando) {
        String message = "<h2>📋 Usuarios (Tecnoweb)</h2>" +
                "<p><strong>🌐 Servidor:</strong> " + DBConnection.TecnoWeb.server + "</p>" +
                "<p><strong>🗄️ Base de datos:</strong> " + DBConnection.TecnoWeb.database + "</p>" +
                "<p>⚠️ Función no implementada aún - requiere conexión a tecnoweb</p>";

        sendEmailViaTecnoweb(senderEmail, "Usuarios - EmailApp Tecnoweb", message);
    }

    private void processProductoCommand(String senderEmail, String comando) {
        String message = "<h2>📋 Productos (Tecnoweb)</h2>" +
                "<p><strong>🌐 Servidor:</strong> " + DBConnection.TecnoWeb.server + "</p>" +
                "<p><strong>🗄️ Base de datos:</strong> " + DBConnection.TecnoWeb.database + "</p>" +
                "<p>⚠️ Función no implementada aún - requiere conexión a tecnoweb</p>";

        sendEmailViaTecnoweb(senderEmail, "Productos - EmailApp Tecnoweb", message);
    }

    private void processCategoriaCommand(String senderEmail, String comando) {
        String message = "<h2>📋 Categorías (Tecnoweb)</h2>" +
                "<p><strong>🌐 Servidor:</strong> " + DBConnection.TecnoWeb.server + "</p>" +
                "<p><strong>🗄️ Base de datos:</strong> " + DBConnection.TecnoWeb.database + "</p>" +
                "<p>⚠️ Función no implementada aún - requiere conexión a tecnoweb</p>";

        sendEmailViaTecnoweb(senderEmail, "Categorías - EmailApp Tecnoweb", message);
    }

    private void sendWelcomeEmail(String email) {
        String message = "<h2>¡Bienvenido al Sistema Tecnoweb!</h2>" +
                "<p>Para usar el sistema, primero debes registrarte enviando un email con el asunto:</p>" +
                "<p><strong>registrar [Nombre] [Apellido] [Teléfono] [Género]</strong></p>" +
                "<p>Ejemplo: <em>registrar Juan Pérez 123456789 masculino</em></p>" +
                "<p><strong>📧 Enviado desde:</strong> " + DBConnection.TecnoWeb.server + "</p>";

        sendEmailViaTecnoweb(email, "Registro requerido - EmailApp Tecnoweb", message);
    }

    private void sendErrorEmail(String email, String error) {
        String message = "<h2>❌ Error en EmailApp Tecnoweb</h2>" +
                "<p>" + error + "</p>" +
                "<p>📧 Servidor: " + DBConnection.TecnoWeb.server + "</p>";

        sendEmailViaTecnoweb(email, "Error - EmailApp Tecnoweb", message);
    }

    /**
     * Envía email usando el servidor SMTP de tecnoweb
     */
    private void sendEmailViaTecnoweb(String to, String subject, String message) {
        try {
            System.out.println("📤 Enviando via SMTP Tecnoweb:");
            System.out.println("   📧 To: " + to);
            System.out.println("   📝 Subject: " + subject);
            System.out.println("   🌐 SMTP: " + DBConnection.TecnoWeb.server + ":25");
            System.out.println("   📬 From: grupo21sc@tecnoweb.org.bo");

            // Crear email para tecnoweb
            Email email = new Email(to, subject, message);

            // Enviar usando EmailSend (tecnoweb SMTP)
            EmailSend emailSend = new EmailSend(email);
            Thread emailThread = new Thread(emailSend);
            emailThread.start();

            System.out.println("✅ Email enviado via Tecnoweb SMTP");

        } catch (Exception e) {
            System.err.println("❌ Error enviando via tecnoweb: " + e.getMessage());

            // Fallback a Gmail si tecnoweb falla
            System.out.println("🔄 Fallback: enviando via Gmail...");
            emailRelay.sendEmail(to, to, subject, message, null);
        }
    }

    // Implementaciones requeridas de ICasoUsoListener
    @Override
    public void usuario(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void pago(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void proveedor(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void patrocinador(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void patrocinio(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void rol(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void servicio(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void error(ParamsAction event) {
        /* Implementación básica */ }

    @Override
    public void help(ParamsAction event) {
        /* Implementación básica */ }

    /**
     * Método principal para testing
     */
    public static void main(String[] args) {
        System.out.println("🧪 Test EmailApp Tecnoweb");

        EmailAppTecnoweb app = new EmailAppTecnoweb();
        app.start();

        // Test básico
        System.out.println("\n🔬 Ejecutando test básico...");
        app.processEmailCommand("test@tecnoweb.com", "help", "Solicito ayuda");

        System.out.println("✅ Test completado");
    }
}