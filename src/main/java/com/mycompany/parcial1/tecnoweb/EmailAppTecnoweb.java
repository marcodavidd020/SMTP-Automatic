package com.mycompany.parcial1.tecnoweb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data.DCarrito;
import data.DCategoria;
import data.DCliente;
import data.DPedido;
import data.DProducto;
import data.DTipoPago;
import data.DUsuario;
import data.DVenta;
import data.DDireccion;
import interfaces.ICasoUsoListener;
import librerias.Email;
import librerias.HtmlRes;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.NCarrito;
import negocio.NCategoria;
import negocio.NPedido;
import negocio.NUsuario;
import postgresConecction.DBConnectionManager;
import postgresConecction.DBConnection;
import postgresConecction.EmailSend;
import servidor.GmailRelay;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * EmailApp original que se conecta al servidor tecnoweb usando POP3
 * y la base de datos tecnoweb (db_grupo21sc en mail.tecnoweb.org.bo)
 *
 * @author Jairo
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
    private DUsuario dUsuario;
    private DProducto dProducto;
    private NCategoria nCategoria;
    private DCliente dCliente;
    private DTipoPago dTipoPago;
    private NCarrito nCarrito;
    private DVenta dVenta;
    private DPedido dPedido;
    private NPedido nPedido;

    public EmailAppTecnoweb() {
        // Configurar base de datos para usar TECNOWEB
        DBConnectionManager.setActiveConfig(DBConnectionManager.ConfigType.TECNOWEB);
        
        this.emailRelay = new GmailRelay();
        this.nUsuario = new NUsuario(true);  // usar tecnoweb
        this.dUsuario = DUsuario.createWithGlobalConfig();
        this.dProducto = DProducto.createWithGlobalConfig();
        this.nCategoria = new NCategoria();  // Usa configuración global internamente
        this.dCliente = DCliente.createWithGlobalConfig();
        this.dTipoPago = DTipoPago.createWithGlobalConfig();
        this.nCarrito = new NCarrito();      // Usa configuración global internamente
        this.dVenta = DVenta.createWithGlobalConfig();
        this.dPedido = DPedido.createWithGlobalConfig();
        this.nPedido = new NPedido();

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

        // Comandos de consulta básicos
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
                cleanSubject.startsWith("registrar ") ||
                // Comandos del sistema de carrito y e-commerce
                cleanSubject.startsWith("carrito ") ||
                cleanSubject.equals("carrito get") ||
                cleanSubject.equals("checkout") ||
                cleanSubject.startsWith("pago ") ||
                cleanSubject.startsWith("ventas ") ||
                cleanSubject.startsWith("compras ") ||
                cleanSubject.equals("ventas get") ||
                cleanSubject.equals("compras get") ||
                // Comandos del sistema de pedidos
                cleanSubject.startsWith("pedido ") ||
                cleanSubject.equals("pedido get");
    }

    /**
     * Procesa comandos directos
     */
    private void processDirectCommand(String senderEmail, String comando) {
        System.out.println("   🔄 Procesando comando: " + comando);

        String[] parts = comando.split("\\s+");
        String entity = parts.length > 0 ? parts[0].toLowerCase() : "";
        String action = parts.length > 1 ? parts[1] : "get";
        String param = parts.length > 2 ? parts[2] : null;

        switch (entity) {
            case "help":
                processHelpCommand(senderEmail);
                break;
            case "registrar":
                processRegistrarCommand(senderEmail, comando);
                break;
            case "usuario":
            case "usuarios":
                processUsuarioCommand(senderEmail, comando);
                break;
            case "producto":
            case "productos":
                processProductoCommand(senderEmail, comando);
                break;
            case "categoria":
            case "categorias":
                processCategoriaCommand(senderEmail, comando);
                break;
            case "cliente":
            case "clientes":
                processClienteCommand(senderEmail, comando);
                break;
            case "tipo_pago":
            case "tipos_pago":
                processTipoPagoCommand(senderEmail, comando);
                break;
            case "carrito":
                processCarritoCommand(senderEmail, action, param, comando, null, null);
                break;
            case "pedido":
                processPedidoCommand(senderEmail, action, param, comando, null, null);
                break;
            case "checkout":
                processCheckoutCommand(senderEmail, comando);
                break;
            case "pago":
                processPagoCommand(senderEmail, action, param, comando);
                break;
            case "ventas":
            case "compras":
                processVentasCommand(senderEmail, action, param, comando);
                break;
            default:
                sendErrorEmail(senderEmail, "Comando no implementado: " + comando);
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

    /**
     * ✅ IMPLEMENTACIÓN DEL COMANDO REGISTRAR
     * Formato: registrar [Nombre] [Apellido] [Teléfono] [Género]
     * Ejemplo: registrar Marco Toledo 67733399 masculino
     * 
     * Sigue arquitectura de 3 capas y compatibilidad con User.php
     */
    private void processRegistrarCommand(String senderEmail, String comando) {
        System.out.println("👤 Procesando comando REGISTRAR (Tecnoweb):");
        System.out.println("   📧 Email: " + senderEmail);
        System.out.println("   📝 Comando: " + comando);

        try {
            // Parsear comando: "registrar Marco Toledo 67733399 masculino"
            String[] parts = comando.trim().split("\\s+");
            
            if (parts.length != 5) {
                sendErrorEmail(senderEmail, 
                    "Formato incorrecto. Use: registrar [Nombre] [Apellido] [Teléfono] [Género]\n" +
                    "Ejemplo: registrar Marco Toledo 67733399 masculino");
                return;
            }

            String nombre = parts[1];
            String apellido = parts[2]; 
            String telefono = parts[3];
            String genero = parts[4];

            // Validar género
            if (!genero.equalsIgnoreCase("masculino") && !genero.equalsIgnoreCase("femenino")) {
                sendErrorEmail(senderEmail, 
                    "Género inválido. Use: masculino o femenino");
                return;
            }

                         // ✅ ARQUITECTURA 3 CAPAS: USAR CAPA DE NEGOCIO
             try {
                 // Registrar usuario usando NUsuario (capa de negocio)
                 List<String[]> resultado = registrarUsuarioConNegocio(
                     nombre, apellido, telefono, genero, senderEmail);

                if (!resultado.isEmpty()) {
                    String[] user = resultado.get(0);
                    
                    // ✅ RESPUESTA CON HTML MODERNO
                    String htmlContent = HtmlRes.generateSuccess(
                        "✅ Registro Exitoso", 
                        String.format("¡Bienvenido %s %s!\n\n" +
                            "Tu cuenta ha sido creada exitosamente:\n\n" +
                            "📧 Email: %s\n" +
                            "📱 Teléfono: %s\n" +
                            "👤 Género: %s\n" +
                            "🆔 ID Usuario: %s\n\n" +
                            "🌐 Servidor: %s\n" +
                            "🗄️ Base de datos: %s\n\n" +
                            "Ya puedes usar todos los comandos del sistema.",
                            nombre, apellido, senderEmail, telefono, genero, 
                            user[0], DBConnection.TecnoWeb.server, DBConnection.TecnoWeb.database));

                    sendEmailViaTecnoweb(senderEmail, "✅ Registro Exitoso - Sistema Tecnoweb", htmlContent);
                    System.out.println("✅ Usuario registrado exitosamente en Tecnoweb: " + senderEmail);
                } else {
                    sendErrorEmail(senderEmail, "Error: No se pudo completar el registro");
                }

            } catch (SQLException e) {
                System.err.println("❌ Error SQL en registro: " + e.getMessage());
                
                if (e.getMessage().contains("already exists") || e.getMessage().contains("ya está registrado")) {
                    sendErrorEmail(senderEmail, 
                        "Este email ya está registrado en el sistema. " +
                        "Si olvidaste tus datos, contacta al administrador.");
                } else {
                    sendErrorEmail(senderEmail, 
                        "Error de base de datos: " + e.getMessage());
                }
                         } catch (Exception ex) {
                 System.err.println("❌ Error en capa de negocio: " + ex.getMessage());
                 throw ex;
             }

        } catch (Exception e) {
            System.err.println("❌ Error general en registro: " + e.getMessage());
            sendErrorEmail(senderEmail, 
                "Error procesando registro: " + e.getMessage() + 
                "\n\nUse el formato: registrar [Nombre] [Apellido] [Teléfono] [Género]");
        }
    }

    /**
     * ✅ MÉTODO DE REGISTRO USANDO CAPA DE NEGOCIO
     * Usa NUsuario.save() siguiendo arquitectura de 3 capas
     */
    private List<String[]> registrarUsuarioConNegocio(String nombre, String apellido, String telefono, 
                                                      String genero, String email) throws SQLException {
        
        // ✅ USAR CAPA DE NEGOCIO - NUsuario
        NUsuario nUsuario = new NUsuario();
        
        // Verificar si el email ya existe
        try {
            nUsuario.getByEmail(email);
            throw new SQLException("El email ya está registrado: " + email);
        } catch (SQLException e) {
            // Si no encuentra el usuario, continuar con el registro
            if (!e.getMessage().contains("no encontrado")) {
                throw e; // Re-lanzar si es otro error
            }
        }
        
        // Preparar parámetros para NUsuario.save()
        // save(nombre, apellido, telefono, genero, email, password, rol_id)
        List<String> parametros = new ArrayList<>();
        parametros.add(nombre);          // 0: nombre
        parametros.add(apellido);        // 1: apellido  
        parametros.add(telefono);        // 2: telefono
        parametros.add(genero);          // 3: genero
        parametros.add(email);           // 4: email
        parametros.add("password");      // 5: password (temporal)
        parametros.add("1");             // 6: rol_id (cliente por defecto)
        
        System.out.println("📝 Registrando usuario via NUsuario.save():");
        System.out.println("   📧 Email: " + email);
        System.out.println("   👤 Nombre: " + nombre + " " + apellido);
        System.out.println("   📱 Teléfono: " + telefono);
        System.out.println("   👫 Género: " + genero);
        
        // ✅ LLAMAR A LA CAPA DE NEGOCIO
        List<String[]> resultado = nUsuario.save(parametros);
        
        System.out.println("✅ Usuario registrado exitosamente usando capa de negocio");
        return resultado;
    }

    private void sendWelcomeEmail(String email) {
        try {
            // ✅ USAR HtmlRes para generar HTML moderno con estilos CSS
            String htmlContent = HtmlRes.generateWelcome(email);
            sendEmailViaTecnoweb(email, "Bienvenido al Sistema Tecnoweb", htmlContent);
            System.out.println("🎨 Email de bienvenida enviado con estilos HTML modernos (Tecnoweb)");
        } catch (Exception e) {
            // Fallback a HTML básico si falla HtmlRes
            String basicMessage = "<h2>¡Bienvenido al Sistema Tecnoweb!</h2>" +
                    "<p>Para usar el sistema, primero debes registrarte enviando un email con el asunto:</p>" +
                    "<p><strong>registrar [Nombre] [Apellido] [Teléfono] [Género]</strong></p>" +
                    "<p>Ejemplo: <em>registrar Juan Pérez 123456789 masculino</em></p>" +
                    "<p><strong>📧 Enviado desde:</strong> " + DBConnection.TecnoWeb.server + "</p>";
            sendEmailViaTecnoweb(email, "Registro requerido - EmailApp Tecnoweb", basicMessage);
            System.err.println("⚠️ Fallback a HTML básico: " + e.getMessage());
        }
    }

    private void sendErrorEmail(String email, String error) {
        try {
            // ✅ USAR HtmlRes para generar error con estilos CSS
            String htmlContent = HtmlRes.generateError("Error en Sistema Tecnoweb", error);
            sendEmailViaTecnoweb(email, "Error - Sistema Tecnoweb", htmlContent);
            System.out.println("🎨 Email de error enviado con estilos HTML modernos (Tecnoweb)");
        } catch (Exception e) {
            // Fallback a HTML básico si falla HtmlRes
            String basicMessage = "<h2>❌ Error en EmailApp Tecnoweb</h2>" +
                    "<p>" + error + "</p>" +
                    "<p>📧 Servidor: " + DBConnection.TecnoWeb.server + "</p>";
            sendEmailViaTecnoweb(email, "Error - EmailApp Tecnoweb", basicMessage);
            System.err.println("⚠️ Fallback a HTML básico para error: " + e.getMessage());
        }
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
        try {
            // ✅ GENERAR HTML MODERNO PARA AYUDA
            String helpHtml = HtmlRes.generateSuccess("Sistema de Comandos Tecnoweb", 
                "Comandos disponibles:\n\n" +
                "🔐 REGISTRO:\n" +
                "• registrar [Nombre] [Apellido] [Teléfono] [Género]\n" +
                "  Ejemplo: registrar Marco Toledo 67733399 masculino\n\n" +
                "📊 CONSULTAS:\n" +
                "• get usuario - Ver información de usuarios\n" +
                "• get producto [id] - Ver productos\n" +
                "• get categoria [id] - Ver categorías\n" +
                "• get cliente [id] - Ver clientes\n" +
                "• get tipo_pago [id] - Ver tipos de pago\n\n" +
                "📧 Sistema vía: mail.tecnoweb.org.bo\n" +
                "🗄️ Base de datos: " + DBConnection.TecnoWeb.database);
                
            sendEmailViaTecnoweb(event.getSender(), "Ayuda - Comandos Disponibles", helpHtml);
            System.out.println("🎨 Email de ayuda enviado con estilos HTML modernos (Tecnoweb)");
        } catch (Exception e) {
            // Fallback a texto básico
            sendEmailViaTecnoweb(event.getSender(), "Ayuda - Comandos Disponibles", 
                               "Lista de comandos disponibles en el sistema tecnoweb.");
            System.err.println("⚠️ Fallback a ayuda básica: " + e.getMessage());
        }
    }

    // ✅ NUEVOS MÉTODOS IMPLEMENTADOS PARA ANALEX
    @Override
    public void producto(ParamsAction event) {
        System.out.println("🛍️ Procesando comando PRODUCTO via ANALEX (Tecnoweb):");
        System.out.println(event.toString());
        
        try {
            librerias.analex.Token token = new librerias.analex.Token();
            String action = token.getStringToken(event.getAction());
            
            if (event.getAction() == librerias.analex.Token.GET) {
                if (event.countParams() > 0) {
                    // producto get <id>
                    try {
                        int id = Integer.parseInt(event.getParams(0));
                        List<String[]> producto = dProducto.get(id);
                        if (!producto.isEmpty()) {
                            String[] prod = producto.get(0);
                            String htmlContent = HtmlRes.generateSuccess("🛍️ Información de Producto", 
                                String.format("Producto encontrado:\n\n" +
                                    "ID: %s\n" +
                                    "Nombre: %s\n" +
                                    "Descripción: %s\n" +
                                    "Precio: $%s\n\n" +
                                    "📧 Consultado vía Tecnoweb", 
                                    prod[0], prod[1], prod.length > 2 ? prod[2] : "N/A", prod.length > 3 ? prod[3] : "N/A"));
                            sendEmailViaTecnoweb(event.getSender(), "🛍️ Información de Producto", htmlContent);
                        } else {
                            sendErrorEmail(event.getSender(), "Producto ID " + id + " no encontrado");
                        }
                    } catch (NumberFormatException e) {
                        sendErrorEmail(event.getSender(), "ID de producto inválido: " + event.getParams(0));
                    }
                } else {
                    // producto get (listar todos)
                    List<String[]> productos = dProducto.list();
                    String listContent = String.format("Se encontraron %d productos en el sistema.\n\n" +
                        "Para ver detalles de un producto específico, use:\n" +
                        "producto get [ID]\n\n" +
                        "📧 Sistema Tecnoweb", productos.size());
                    String htmlContent = HtmlRes.generateSuccess("🛍️ Lista de Productos", listContent);
                    sendEmailViaTecnoweb(event.getSender(), "🛍️ Lista de Productos", htmlContent);
                }
            } else {
                sendErrorEmail(event.getSender(), "La acción '" + action + "' no está disponible para productos. Use 'producto get'");
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando comando producto: " + e.getMessage());
            sendErrorEmail(event.getSender(), "Error procesando comando: " + e.getMessage());
        }
    }

    @Override
    public void categoria(ParamsAction event) {
        System.out.println("📂 Procesando comando CATEGORIA via ANALEX (Tecnoweb):");
        System.out.println(event.toString());
        
        try {
            librerias.analex.Token token = new librerias.analex.Token();
            String action = token.getStringToken(event.getAction());
            
            if (event.getAction() == librerias.analex.Token.GET) {
                if (event.countParams() > 0) {
                    // categoria get <id>
                    try {
                        int id = Integer.parseInt(event.getParams(0));
                        List<String[]> categoria = nCategoria.get(id);
                        String content = "Categoría ID " + id + " encontrada: " + (categoria.isEmpty() ? "No encontrada" : categoria.get(0)[1]);
                        sendEmailViaTecnoweb(event.getSender(), "📂 Información de Categoría", content);
                    } catch (NumberFormatException e) {
                        sendErrorEmail(event.getSender(), "ID de categoría inválido: " + event.getParams(0));
                    }
                } else {
                    // categoria get (listar todas)
                    List<String[]> categorias = nCategoria.list();
                    String content = "Se encontraron " + categorias.size() + " categorías en el sistema.";
                    sendEmailViaTecnoweb(event.getSender(), "📂 Lista de Categorías", content);
                }
            } else {
                sendErrorEmail(event.getSender(), "La acción '" + action + "' no está disponible para categorías. Use 'categoria get'");
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando comando categoria: " + e.getMessage());
            sendErrorEmail(event.getSender(), "Error procesando comando: " + e.getMessage());
        }
    }

    @Override
    public void cliente(ParamsAction event) {
        System.out.println("👤 Procesando comando CLIENTE via ANALEX (Tecnoweb):");
        System.out.println(event.toString());
        
        try {
            librerias.analex.Token token = new librerias.analex.Token();
            String action = token.getStringToken(event.getAction());
            
            if (event.getAction() == librerias.analex.Token.GET) {
                if (event.countParams() > 0) {
                    // cliente get <id>
                    try {
                        int id = Integer.parseInt(event.getParams(0));
                        List<String[]> cliente = dCliente.get(id);
                        String content = "Cliente ID " + id + " encontrado: " + (cliente.isEmpty() ? "No encontrado" : cliente.get(0)[1]);
                        sendEmailViaTecnoweb(event.getSender(), "👤 Información de Cliente", content);
                    } catch (NumberFormatException e) {
                        sendErrorEmail(event.getSender(), "ID de cliente inválido: " + event.getParams(0));
                    }
                } else {
                    // cliente get (listar todos)
                    List<String[]> clientes = dCliente.list();
                    String content = "Se encontraron " + clientes.size() + " clientes en el sistema.";
                    sendEmailViaTecnoweb(event.getSender(), "👤 Lista de Clientes", content);
                }
            } else {
                sendErrorEmail(event.getSender(), "La acción '" + action + "' no está disponible para clientes. Use 'cliente get'");
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando comando cliente: " + e.getMessage());
            sendErrorEmail(event.getSender(), "Error procesando comando: " + e.getMessage());
        }
    }

    @Override
    public void tipo_pago(ParamsAction event) {
        System.out.println("💳 Procesando comando TIPO_PAGO via ANALEX (Tecnoweb):");
        System.out.println(event.toString());
        
        try {
            librerias.analex.Token token = new librerias.analex.Token();
            String action = token.getStringToken(event.getAction());
            
            if (event.getAction() == librerias.analex.Token.GET) {
                if (event.countParams() > 0) {
                    // tipo_pago get <id>
                    try {
                        int id = Integer.parseInt(event.getParams(0));
                        List<String[]> tipoPago = dTipoPago.get(id);
                        String content = "Tipo de Pago ID " + id + " encontrado: " + (tipoPago.isEmpty() ? "No encontrado" : tipoPago.get(0)[1]);
                        sendEmailViaTecnoweb(event.getSender(), "💳 Información de Tipo de Pago", content);
                    } catch (NumberFormatException e) {
                        sendErrorEmail(event.getSender(), "ID de tipo de pago inválido: " + event.getParams(0));
                    }
                } else {
                    // tipo_pago get (listar todos)
                    List<String[]> tiposPago = dTipoPago.list();
                    String content = "Se encontraron " + tiposPago.size() + " tipos de pago en el sistema.";
                    sendEmailViaTecnoweb(event.getSender(), "💳 Lista de Tipos de Pago", content);
                }
            } else {
                sendErrorEmail(event.getSender(), "La acción '" + action + "' no está disponible para tipos de pago. Use 'tipo_pago get'");
            }
        } catch (Exception e) {
            System.err.println("❌ Error procesando comando tipo_pago: " + e.getMessage());
            sendErrorEmail(event.getSender(), "Error procesando comando: " + e.getMessage());
        }
    }

    /**
     * Procesa comandos relacionados con el carrito de compras
     */
    private void processCarritoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            // Obtener ID del cliente basado en el email CON VALIDACIÓN DE ROL
            int clienteId = obtenerClienteIdPorEmailSeguro(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Cliente No Configurado",
                        String.format("🔍 PROBLEMA DETECTADO:\n" +
                                "Tu usuario (%s) está registrado en el sistema, pero no tienes un perfil de CLIENTE asociado.\n\n" +
                                "📋 PARA RESOLVER ESTE PROBLEMA:\n" +
                                "1. Contacta al administrador del sistema\n" +
                                "2. Solicita que te creen un perfil de cliente\n" +
                                "3. O envía un email con asunto: 'crear cliente para %s'\n\n" +
                                "💡 El sistema requiere que tengas un perfil de cliente para poder realizar compras.\n\n" +
                                "🔧 DETALLES TÉCNICOS:\n" +
                                "- Email detectado: %s\n" +
                                "- Usuario registrado: ✅ SÍ\n" +
                                "- Cliente asociado: ❌ NO\n" +
                                "- Comando solicitado: %s",
                                senderEmail, senderEmail, senderEmail, comando),
                        originalSubject, messageId);
                return;
            }

            // Validación especial: Detectar si falta la acción "add"
            // Ejemplo: "carrito 147 2" debería ser "carrito add 147 2"
            try {
                Integer.parseInt(action); // Si action es un número, falta la acción "add"

                sendSimpleResponse(senderEmail, "❌ Comando Incompleto",
                        String.format("Formato incorrecto: '%s'\n\n" +
                                "✅ FORMATO CORRECTO:\n" +
                                "carrito add %s %s\n\n" +
                                "📋 COMANDOS DISPONIBLES:\n" +
                                "• carrito add [producto_id] [cantidad] - Agregar producto\n" +
                                "• carrito get - Ver carrito\n" +
                                "• carrito remove [producto_id] - Remover producto\n" +
                                "• carrito clear - Vaciar carrito\n\n" +
                                "💡 Te faltó especificar la acción 'add'",
                                comando, action, param != null ? param : "[cantidad]"),
                        originalSubject, messageId);
                return;
            } catch (NumberFormatException e) {
                // action no es un número, continúa con el flujo normal
            }

            switch (action) {
                case "add":
                    if (param != null) {
                        String[] params = comando.split("\\s+");
                        if (params.length >= 4) {
                            try {
                                int productoId = Integer.parseInt(params[2]);
                                int cantidad = Integer.parseInt(params[3]);

                                if (nCarrito.agregarProducto(clienteId, productoId, cantidad)) {
                                    sendSimpleResponse(senderEmail, "✅ Producto Agregado",
                                            String.format(
                                                    "Producto #%d agregado al carrito exitosamente (cantidad: %d).\n\n" +
                                                            "📋 PRÓXIMOS PASOS:\n" +
                                                            "• carrito get - Ver tu carrito completo\n" +
                                                            "• checkout - Crear orden de compra\n" +
                                                            "• tipos_pago get - Ver métodos de pago",
                                                    productoId, cantidad),
                                            originalSubject, messageId);
                                } else {
                                    sendSimpleResponse(senderEmail, "❌ Error Agregando Producto",
                                            String.format("No se pudo agregar el producto #%d al carrito.\n\n" +
                                                    "🔍 POSIBLES CAUSAS:\n" +
                                                    "• Producto no existe en inventario\n" +
                                                    "• Stock insuficiente (cantidad solicitada: %d)\n" +
                                                    "• Error temporal de base de datos\n\n" +
                                                    "💡 Usa 'producto get %d' para verificar disponibilidad",
                                                    productoId, cantidad, productoId),
                                            originalSubject, messageId);
                                }
                            } catch (NumberFormatException e) {
                                sendSimpleResponse(senderEmail, "❌ Error de Formato",
                                        String.format("Los parámetros deben ser números enteros.\n\n" +
                                                "❌ Recibido: '%s'\n" +
                                                "✅ Formato correcto: carrito add [numero_producto] [numero_cantidad]\n" +
                                                "✅ Ejemplo: carrito add 161 3\n\n" +
                                                "💡 Asegúrate de usar números sin corchetes ni caracteres especiales",
                                                comando),
                                        originalSubject, messageId);
                            }
                        } else {
                            sendSimpleResponse(senderEmail, "❌ Parámetros Insuficientes",
                                    String.format("Comando incompleto: '%s'\n\n" +
                                            "✅ FORMATO COMPLETO:\n" +
                                            "carrito add [producto_id] [cantidad]\n\n" +
                                            "✅ EJEMPLO:\n" +
                                            "carrito add 161 3\n\n" +
                                            "💡 Necesitas especificar tanto el ID del producto como la cantidad",
                                            comando),
                                    originalSubject, messageId);
                        }
                    } else {
                        sendSimpleResponse(senderEmail, "❌ Parámetros Faltantes",
                                "Formato: carrito add [producto_id] [cantidad]\n\n" +
                                        "✅ EJEMPLO: carrito add 161 3\n\n" +
                                        "📋 Para ver productos disponibles usa: producto get",
                                originalSubject, messageId);
                    }
                    break;

                case "get":
                    List<String[]> carrito = nCarrito.obtenerCarrito(clienteId);
                    if (carrito.isEmpty()) {
                        sendSimpleResponse(senderEmail, "🛒 Carrito Vacío",
                                "Tu carrito está vacío. Usa 'carrito add [producto_id] [cantidad]' para agregar productos.",
                                originalSubject, messageId);
                    } else {
                        double total = nCarrito.obtenerTotalCarrito(clienteId);
                        String titulo = String.format("🛒 Tu Carrito - Total: $%.2f", total);
                        sendTableResponse(senderEmail, titulo, DCarrito.DETALLE_HEADERS,
                                (ArrayList<String[]>) carrito, comando, originalSubject, messageId);
                    }
                    break;

                case "remove":
                    if (param != null) {
                        try {
                            int productoId = Integer.parseInt(param);
                            if (nCarrito.removerProducto(clienteId, productoId)) {
                                sendSimpleResponse(senderEmail, "✅ Producto Removido",
                                        "Producto removido del carrito exitosamente.", originalSubject, messageId);
                            } else {
                                sendSimpleResponse(senderEmail, "❌ Error",
                                        "No se pudo remover el producto del carrito.", originalSubject, messageId);
                            }
                        } catch (NumberFormatException e) {
                            sendSimpleResponse(senderEmail, "❌ Error",
                                    "ID de producto inválido.", originalSubject, messageId);
                        }
                    } else {
                        sendSimpleResponse(senderEmail, "❌ Parámetro faltante",
                                "Formato: carrito remove [producto_id]", originalSubject, messageId);
                    }
                    break;

                case "clear":
                    if (nCarrito.vaciarCarrito(clienteId)) {
                        sendSimpleResponse(senderEmail, "✅ Carrito Vaciado",
                                "Tu carrito ha sido vaciado exitosamente.", originalSubject, messageId);
                    } else {
                        sendSimpleResponse(senderEmail, "❌ Error",
                                "No se pudo vaciar el carrito.", originalSubject, messageId);
                    }
                    break;

                default:
                    // Detectar aquí también si es un número
                    try {
                        Integer.parseInt(action);
                        sendSimpleResponse(senderEmail, "❌ Comando Incompleto",
                                String.format("Formato incorrecto: '%s'\n\n" +
                                        "✅ FORMATO CORRECTO:\n" +
                                        "carrito add %s %s\n\n" +
                                        "📋 COMANDOS DISPONIBLES:\n" +
                                        "• carrito add [producto_id] [cantidad] - Agregar producto\n" +
                                        "• carrito get - Ver carrito\n" +
                                        "• carrito remove [producto_id] - Remover producto\n" +
                                        "• carrito clear - Vaciar carrito\n\n" +
                                        "💡 Te faltó especificar la acción 'add'",
                                        comando, action, param != null ? param : "[cantidad]"),
                                originalSubject, messageId);
                    } catch (NumberFormatException e) {
                        // No es un número, es una acción inválida
                        sendSimpleResponse(senderEmail, "❌ Acción no válida",
                                "Acciones disponibles: add, get, remove, clear", originalSubject, messageId);
                    }
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en carrito: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage());
        }
    }

    /**
     * Procesa comandos relacionados con pedidos
     */
    private void processPedidoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            int clienteId = obtenerClienteIdPorEmailSeguro(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Error",
                        "No se encontró perfil de cliente o no tienes permisos. Contacta al administrador.", 
                        originalSubject, messageId);
                return;
            }
            
            // Crear instancia de DPedido
            DPedido dPedido = new DPedido();
            
            switch (action) {
                case "get":
                    if (param != null) {
                        try {
                            // Obtener pedido específico
                            int pedidoId = Integer.parseInt(param);
                            List<String[]> pedido = nPedido.get(pedidoId);
                            
                            if (pedido != null && !pedido.isEmpty()) {
                                // Obtener los detalles de productos del pedido
                                List<String[]> detallesPedido = nPedido.obtenerDetallePedido(pedidoId);
                                
                                // Generar HTML para la tabla principal del pedido
                                String htmlPedido = HtmlRes.generateTable(
                                        "Detalles del Pedido #" + pedidoId,
                                        DPedido.HEADERS,
                                        pedido);
                                
                                // Generar HTML para los productos del pedido
                                String htmlProductos = "";
                                if (!detallesPedido.isEmpty()) {
                                    htmlProductos = HtmlRes.generateTable(
                                            "Productos en el Pedido #" + pedidoId,
                                            DPedido.DETALLE_HEADERS,
                                            detallesPedido);
                                } else {
                                    htmlProductos = "<p>No se encontraron productos asociados a este pedido.</p>";
                                }
                                
                                // Combinar ambas tablas en un solo HTML
                                String htmlContent = htmlPedido + "<br/><br/>" + htmlProductos;
                                
                                // Enviar respuesta con ambas tablas
                                sendSimpleResponse(senderEmail, "📦 Detalles del Pedido #" + pedidoId, 
                                        htmlContent, originalSubject, messageId);
                            } else {
                                sendSimpleResponse(senderEmail, "❌ Pedido no encontrado", 
                                        "No se encontró el pedido con ID: " + pedidoId, 
                                        originalSubject, messageId);
                            }
                        } catch (NumberFormatException e) {
                            sendSimpleResponse(senderEmail, "❌ ID inválido", 
                                    "El ID del pedido debe ser un número. Formato: pedido get [id]", 
                                    originalSubject, messageId);
                        } catch (SQLException e) {
                            sendSimpleResponse(senderEmail, "❌ Error de base de datos", 
                                    "Error consultando el pedido: " + e.getMessage(), 
                                    originalSubject, messageId);
                        }
                    } else {
                        try {
                            // Listar pedidos del cliente específico usando capa de negocio
                            List<String[]> pedidos = nPedido.obtenerPedidosPorCliente(clienteId);
                            
                            if (pedidos != null && !pedidos.isEmpty()) {
                                String htmlContent = HtmlRes.generateTable(
                                        "Mis Pedidos - Cliente ID: " + clienteId,
                                        DPedido.HEADERS,
                                        pedidos);
                                
                                // Generar mensaje informativo
                                StringBuilder mensaje = new StringBuilder();
                                mensaje.append("Se encontraron ").append(pedidos.size()).append(" pedidos para tu cuenta.\n\n");
                                
                                // Revisar estado de pagos usando lógica de negocio
                                boolean tienePendientes = nPedido.tienePedidosPendientes(clienteId);
                                
                                if (tienePendientes) {
                                    mensaje.append("⚠️ ATENCIÓN: Tienes pedidos pendientes de pago.\n")
                                          .append("Para completar el pago, usa el comando:\n")
                                          .append("• pago [pedido_id] [tipo_pago_id]\n\n")
                                          .append("Ver tipos de pago disponibles con: tipo_pago get\n\n");
                                }
                                
                                mensaje.append("📋 Detalles de tus pedidos:");
                                
                                sendSimpleResponse(senderEmail, "📦 Mis Pedidos", 
                                        mensaje.toString() + "\n\n" + htmlContent, 
                                        originalSubject, messageId);
                            } else {
                                sendSimpleResponse(senderEmail, "📦 Sin pedidos", 
                                        "No tienes pedidos registrados en tu cuenta.\n\n" +
                                        "Para crear un pedido:\n" +
                                        "1. Agrega productos al carrito: carrito add [producto_id] [cantidad]\n" +
                                        "2. Crea el pedido: pedido add [direccion_id]\n" +
                                        "3. Completa el pago: pago [pedido_id] [tipo_pago_id]", 
                                        originalSubject, messageId);
                            }
                        } catch (SQLException e) {
                            System.err.println("❌ Error de base de datos listando pedidos: " + e.getMessage());
                            sendSimpleResponse(senderEmail, "❌ Error de base de datos", 
                                    "Error consultando tus pedidos: " + e.getMessage(), 
                                    originalSubject, messageId);
                        }
                    }
                    break;
                
                case "add":
                    if (param != null) {
                        try {
                            // Verificar si es un ID numérico o una URL de Google Maps
                            boolean esNumero = true;
                            int direccionId = 0;
                            
                            try {
                                // Intentar parsear como número
                                direccionId = Integer.parseInt(param);
                            } catch (NumberFormatException e) {
                                esNumero = false;
                            }
                            
                            // Si no es un número, intentar procesar como URL de Google Maps
                            if (!esNumero) {
                                try {
                                    // El comando completo tiene el formato: "pedido add URL referencia"
                                    // param ya contiene la URL (primera palabra después de "add")
                                    String urlGoogleMaps = param;
                                    
                                    // La referencia es todo lo que viene después de la URL
                                    String referencia = "Sin referencia";
                                    String[] partesDespuesDeAdd = comando.split("\\s+", 3);
                                    if (partesDespuesDeAdd.length >= 3) {
                                        // Obtener todo después de "pedido add URL"
                                        String[] partesURL = partesDespuesDeAdd[2].split("\\s+", 2);
                                        if (partesURL.length >= 2) {
                                            referencia = partesURL[1];
                                        }
                                    }
                                    
                                    System.out.println("   🗺️ URL de Google Maps: " + urlGoogleMaps);
                                    System.out.println("   📝 Referencia: " + referencia);
                                    
                                    // Crear dirección con la URL
                                    DDireccion dDireccion = new DDireccion();
                                    direccionId = dDireccion.add("Dirección de " + senderEmail, urlGoogleMaps, referencia);
                                    
                                    if (direccionId <= 0) {
                                        sendSimpleResponse(senderEmail, "❌ Error en la dirección", 
                                                "No se pudo crear la dirección con la URL proporcionada. Verifica que sea un enlace válido de Google Maps.",
                                                originalSubject, messageId);
                                        return;
                                    }
                                    
                                    System.out.println("   ✅ Dirección creada con ID: " + direccionId);
                                } catch (Exception e) {
                                    System.err.println("❌ Error procesando URL de Google Maps: " + e.getMessage());
                                    sendSimpleResponse(senderEmail, "❌ Error en la dirección", 
                                            "No se pudo procesar la URL de Google Maps: " + e.getMessage() + "\n\n" +
                                            "El formato correcto es: pedido add URL_GOOGLE_MAPS referencia\n" +
                                            "Ejemplo: pedido add https://www.google.com/maps/@-17.78,63.18,15z Mi Casa",
                                            originalSubject, messageId);
                                    return;
                                }
                            }
                            
                            // Verificar carrito
                            DCarrito dCarrito = new DCarrito();
                            List<String[]> carrito = dCarrito.obtenerCarrito(clienteId);
                            
                            if (carrito == null || carrito.isEmpty()) {
                                sendSimpleResponse(senderEmail, "❌ Carrito vacío", 
                                        "Tu carrito está vacío. Agrega productos antes de crear un pedido.",
                                        originalSubject, messageId);
                                return;
                            }
                            
                            // Crear pedido desde carrito usando capa de negocio
                            int pedidoId = nPedido.crearPedidoDesdeCarrito(clienteId, direccionId);
                            
                            if (pedidoId > 0) {
                                sendSimpleResponse(senderEmail, "✅ Pedido Creado", 
                                        String.format("¡Tu pedido #%d ha sido creado exitosamente!\n\n" +
                                                     "Para completar el proceso de compra, usa el comando:\n" +
                                                     "• pago [venta_id] [tipo_pago_id]\n\n" +
                                                     "Puedes ver los tipos de pago disponibles con:\n" +
                                                     "• tipo_pago get", pedidoId),
                                        originalSubject, messageId);
                            } else {
                                sendSimpleResponse(senderEmail, "❌ Error", 
                                        "No se pudo crear el pedido. Verifica que tu carrito tenga productos válidos.",
                                        originalSubject, messageId);
                            }
                        } catch (SQLException e) {
                            System.err.println("❌ Error de base de datos en pedido add: " + e.getMessage());
                            sendSimpleResponse(senderEmail, "❌ Error de base de datos", 
                                    "Error procesando el pedido: " + e.getMessage(), 
                                    originalSubject, messageId);
                        } catch (Exception e) {
                            System.err.println("❌ Error en pedido add: " + e.getMessage());
                            e.printStackTrace();
                            sendSimpleResponse(senderEmail, "❌ Error", 
                                    "Error procesando el comando: " + e.getMessage() + "\n\n" +
                                    "Formatos válidos:\n" +
                                    "• pedido add [direccion_id] - Usar ID de dirección existente\n" +
                                    "• pedido add [URL_GOOGLE_MAPS] [referencia] - Crear nueva dirección",
                                    originalSubject, messageId);
                        }
                    } else {
                        sendSimpleResponse(senderEmail, "❌ Parámetro requerido", 
                                "Se requiere el ID de la dirección o una URL de Google Maps.\n\n" +
                                "Formatos válidos:\n" +
                                "• pedido add [direccion_id] - Usar ID de dirección existente\n" +
                                "• pedido add [URL_GOOGLE_MAPS] [referencia] - Crear nueva dirección",
                                originalSubject, messageId);
                    }
                    break;
                
                default:
                    sendSimpleResponse(senderEmail, "❌ Acción no implementada", 
                            String.format("La acción '%s' no está implementada para pedidos.\n\n" +
                                         "ACCIONES DISPONIBLES:\n" +
                                         "• pedido get - Ver todos los pedidos\n" +
                                         "• pedido get [id] - Ver pedido específico\n" +
                                         "• pedido add [direccion_id] - Crear pedido desde carrito", action),
                                originalSubject, messageId);
                    break;
            }
        } catch (Exception e) {
            System.err.println("❌ Error en processPedidoCommand: " + e.getMessage());
            e.printStackTrace();
            sendErrorEmail(senderEmail, "Error procesando comando: " + e.getMessage());
        }
    }

    /**
     * Valida que el usuario tenga rol de cliente antes de procesar comandos
     */
    private boolean validarRolCliente(String email) {
        try {
            boolean tieneRolCliente = dUsuario.tieneRol(email, "cliente");
            return tieneRolCliente;
        } catch (Exception e) {
            System.err.println("❌ Error validando rol de cliente: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el ID del cliente basado en su email - CON VALIDACIÓN DE ROL
     */
    private int obtenerClienteIdPorEmailSeguro(String email) {
        try {
            if (!validarRolCliente(email)) {
                return 0;
            }

            // Obtener el usuario para conseguir su ID
            List<String[]> usuarios = nUsuario.getByEmail(email);
            if (usuarios.isEmpty()) {
                return 0;
            }

            String userId = usuarios.get(0)[0];
            
            // Buscar cliente asociado
            List<String[]> clientes = dCliente.list();
            for (String[] cliente : clientes) {
                if (cliente[1].equals(userId)) {
                    return Integer.parseInt(cliente[0]);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error en búsqueda segura de cliente: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el ID del cliente basado en su email (método base)
     */
    private int obtenerClienteIdPorEmail(String email) {
        try {
            System.out.println("🔍 Buscando cliente para email: " + email);

            // Primero obtener el usuario por email
            DUsuario dUser = new DUsuario();
            if (!dUser.existsByEmail(email)) {
                System.out.println("❌ Usuario no existe: " + email);
                return 0;
            }

            // Obtener el usuario para conseguir su ID
            List<String[]> usuarios = nUsuario.getByEmail(email);
            if (usuarios.isEmpty()) {
                System.out.println("❌ No se pudo obtener datos del usuario: " + email);
                return 0;
            }

            String userId = usuarios.get(0)[0]; // ID del usuario
            System.out.println("✅ Usuario encontrado - ID: " + userId + ", Email: " + email);

            // Buscar cliente asociado usando consulta SQL directa
            try {
                DCliente dClienteTemp = new DCliente();
                List<String[]> clientes = dClienteTemp.list();
                System.out.println("📋 Total clientes en sistema: " + clientes.size());

                for (String[] cliente : clientes) {
                    System.out.println("🔍 Revisando cliente ID: " + cliente[0] + ", user_id: " + cliente[1]);
                    if (cliente[1].equals(userId)) { // user_id está en la posición 1
                        int clienteId = Integer.parseInt(cliente[0]);
                        System.out.println("✅ Cliente encontrado - ID: " + clienteId + " para usuario: " + userId);
                        return clienteId;
                    }
                }

                System.out.println("❌ No se encontró cliente asociado al usuario ID: " + userId);

            } catch (Exception e) {
                System.err.println("❌ Error buscando cliente: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("❌ Error obteniendo cliente por email: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Normaliza comandos para aceptar singular y plural
     * usuario/usuarios -> usuario
     * producto/productos -> producto
     * categoria/categorias -> categoria
     * cliente/clientes -> cliente
     */
    private String normalizeCommand(String entity) {
        if (entity == null)
            return null;

        entity = entity.toLowerCase().trim();

        // Mapear plurales a singulares
        switch (entity) {
            case "usuarios":
                return "usuario";
            case "productos":
                return "producto";
            case "categorias":
                return "categoria";
            case "clientes":
                return "cliente";
            case "tipos_pago":
            case "tipo_pagos":
                return "tipo_pago";
            default:
                return entity;
        }
    }

    /**
     * Extrae comando del contenido del email (para respuestas)
     * Busca líneas que no empiecen con ">" (texto citado) y que contengan comandos válidos
     */
    private String extractCommandFromContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        String[] lines = content.split("\n");
        for (String line : lines) {
            line = line.trim();

            // Ignorar líneas vacías y texto citado (que empieza con >)
            if (line.isEmpty() || line.startsWith(">")) {
                continue;
            }

            // Limpiar corchetes de los comandos
            line = line.replaceAll("\\[|\\]", "");

            // Buscar primera línea que contenga un comando válido
            if (isCommandEmail(line)) {
                System.out.println("   🔍 Línea de comando detectada: " + line);
                return line.toLowerCase().trim();
            }
        }

        return null;
    }

    /**
     * Envía respuesta simple via email
     */
    private void sendSimpleResponse(String email, String title, String message, String originalSubject,
            String messageId) {
        String content = HtmlRes.generateText(new String[] { title, message });
        sendEmailViaTecnoweb(email, "[Servidor Tecnoweb] " + title, content);
    }

    /**
     * Envía respuesta con tabla via email
     */
    private void sendTableResponse(String email, String title, String[] headers, ArrayList<String[]> data,
            String command, String originalSubject, String messageId) {
        String content = HtmlRes.generateTable(title, headers, data);
        String subject = command != null ? "[Comando: " + command + "] " + title : "[Servidor Tecnoweb] " + title;
        sendEmailViaTecnoweb(email, subject, content);
    }

    /**
     * Envía email de error como reply
     */
    private void sendErrorEmailAsReply(String email, String error, String originalSubject, String messageId) {
        sendSimpleResponse(email, "Error de Procesamiento", error, originalSubject, messageId);
    }

    /**
     * Maneja errores y envía respuesta
     */
    private void handleError(int type, String email, List<String> args, String originalSubject, String messageId) {
        String errorMsg = args != null && !args.isEmpty() ? args.get(0) : "Error desconocido";
        System.err.println("🚨 Error procesando comando: " + errorMsg);
        sendSimpleResponse(email, "Error de Sistema", "Se ha producido un error al procesar su solicitud: " + errorMsg,
                originalSubject, messageId);
    }

    /**
     * Procesa comandos de cliente
     */
    private void processClienteCommand(String senderEmail, String comando) {
        String message = "<h2>👤 Clientes (Tecnoweb)</h2>" +
                "<p><strong>🌐 Servidor:</strong> " + DBConnection.TecnoWeb.server + "</p>" +
                "<p><strong>🗄️ Base de datos:</strong> " + DBConnection.TecnoWeb.database + "</p>" +
                "<p>⚠️ Función implementada para tecnoweb</p>";
        sendEmailViaTecnoweb(senderEmail, "Clientes - EmailApp Tecnoweb", message);
    }

    /**
     * Procesa comandos de tipo de pago
     */
    private void processTipoPagoCommand(String senderEmail, String comando) {
        try {
            List<String[]> tiposPago = dTipoPago.list();
            String htmlContent = HtmlRes.generateTable("💳 Tipos de Pago Disponibles", DTipoPago.HEADERS, tiposPago);
            sendEmailViaTecnoweb(senderEmail, "Tipos de Pago - Tecnoweb", htmlContent);
        } catch (Exception e) {
            sendErrorEmail(senderEmail, "Error consultando tipos de pago: " + e.getMessage());
        }
    }

    /**
     * Procesa comando de checkout
     */
    private void processCheckoutCommand(String senderEmail, String comando) {
        try {
            int clienteId = obtenerClienteIdPorEmailSeguro(senderEmail);
            if (clienteId == 0) {
                sendErrorEmail(senderEmail, "No se encontró perfil de cliente. Contacta al administrador.");
                return;
            }

            int ventaId = dVenta.procesarCheckout(clienteId);
            String message = String.format("Tu orden #%d ha sido creada. Usa 'pago %d [tipo_pago_id]' para completar la compra.", ventaId, ventaId);
            sendEmailViaTecnoweb(senderEmail, "✅ Checkout Exitoso", message);

        } catch (SQLException ex) {
            sendErrorEmail(senderEmail, "Error en checkout: " + ex.getMessage());
        }
    }

    /**
     * Procesa comandos de pago
     */
    private void processPagoCommand(String senderEmail, String action, String param, String comando) {
        try {
            int clienteId = obtenerClienteIdPorEmailSeguro(senderEmail);
            if (clienteId == 0) {
                sendErrorEmail(senderEmail, "No se encontró perfil de cliente. Contacta al administrador.");
                return;
            }

            if (param != null) {
                String[] params = comando.split("\\s+");
                if (params.length >= 3) {
                    try {
                        int ventaId = Integer.parseInt(params[1]);
                        int tipoPagoId = Integer.parseInt(params[2]);

                        if (dVenta.completarVenta(ventaId, tipoPagoId, clienteId)) {
                            String message = String.format("¡Compra exitosa! Tu orden #%d ha sido pagada y procesada.", ventaId);
                            sendEmailViaTecnoweb(senderEmail, "✅ Pago Completado", message);
                        } else {
                            sendErrorEmail(senderEmail, "No se pudo procesar el pago.");
                        }
                    } catch (NumberFormatException e) {
                        sendErrorEmail(senderEmail, "IDs inválidos. Formato: pago [venta_id] [tipo_pago_id]");
                    }
                } else {
                    sendErrorEmail(senderEmail, "Formato: pago [venta_id] [tipo_pago_id]");
                }
            } else {
                sendErrorEmail(senderEmail, "Formato: pago [venta_id] [tipo_pago_id]. Usa 'tipos_pago get' para ver opciones.");
            }

        } catch (SQLException ex) {
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage());
        }
    }

    /**
     * Procesa comandos de ventas/compras
     */
    private void processVentasCommand(String senderEmail, String action, String param, String comando) {
        try {
            int clienteId = obtenerClienteIdPorEmailSeguro(senderEmail);
            if (clienteId == 0) {
                sendErrorEmail(senderEmail, "No se encontró perfil de cliente. Contacta al administrador.");
                return;
            }

            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int ventaId = Integer.parseInt(param);
                        List<String[]> detalle = dVenta.obtenerDetalleVenta(ventaId, clienteId);

                        if (detalle.isEmpty()) {
                            sendErrorEmail(senderEmail, "No se encontró la venta especificada.");
                        } else {
                            String htmlContent = HtmlRes.generateTable("📋 Detalle de Venta #" + ventaId, DCarrito.DETALLE_HEADERS, detalle);
                            sendEmailViaTecnoweb(senderEmail, "Detalle de Venta", htmlContent);
                        }
                    } catch (NumberFormatException e) {
                        sendErrorEmail(senderEmail, "ID de venta inválido.");
                    }
                } else {
                    List<String[]> historial = dVenta.obtenerHistorialVentas(clienteId);

                    if (historial.isEmpty()) {
                        sendEmailViaTecnoweb(senderEmail, "📋 Sin Compras", "No tienes compras registradas aún.");
                    } else {
                        String[] headers = { "ID", "Fecha", "Total", "Estado", "Método Pago" };
                        String htmlContent = HtmlRes.generateTable("📋 Tu Historial de Compras", headers, historial);
                        sendEmailViaTecnoweb(senderEmail, "Historial de Compras", htmlContent);
                    }
                }
            } else {
                sendErrorEmail(senderEmail, "Usa 'ventas get' para ver tu historial.");
            }

        } catch (SQLException ex) {
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage());
        }
    }

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