package com.mycompany.parcial1.tecnoweb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.DCategoria;
import data.DCliente;
import data.DProducto;
import data.DTipoPago;
import data.DUsuario;
import interfaces.ICasoUsoListener;
import librerias.HtmlRes;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.NUsuario;
import postgresConecction.DBConnection;
import postgresConecction.TestConnection;
import servidor.GmailRelay;

/**
 * Versión independiente del EmailApp que usa el servidor independiente
 * y responde automáticamente a emails con comandos
 * 
 * @author MARCO
 */
public class EmailAppIndependiente implements ICasoUsoListener {

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

    public EmailAppIndependiente() {
        this.emailRelay = new GmailRelay();
        this.nUsuario = new NUsuario();
        this.dProducto = new DProducto();
        this.dCategoria = new DCategoria();
        this.dCliente = new DCliente();
        this.dTipoPago = new DTipoPago();

        System.out.println("🚀 EmailApp Independiente inicializado");
        System.out.println("📧 Modo: Servidor Independiente con Gmail");
        System.out.println("🗄️ Base de datos: " + DBConnection.database + " en " + DBConnection.server);

        // Verificar conexión a base de datos
        if (!TestConnection.testConnection()) {
            System.err.println("⚠️ ADVERTENCIA: No se pudo conectar a la base de datos");
            System.err.println("💡 Verifica que PostgreSQL esté ejecutándose");
            System.err.println("💡 Verifica que la base de datos 'EcommerceTool' exista");
        } else {
            System.out.println("✅ Conexión a base de datos verificada");
        }
    }

    /**
     * Inicia el monitoreo de emails usando el monitor existente
     */
    public void start() {
        System.out.println("📬 Iniciando EmailApp Independiente...");
        System.out.println("💡 Para procesar comandos, use GmailMonitorRecientes por separado");
        System.out.println("🔄 Este EmailApp procesará comandos cuando se llamen directamente");
    }

    /**
     * Procesa comandos recibidos via email (para ser llamado externamente)
     */
    public void processEmailCommand(String senderEmail, String subject, String content) {
        processEmailCommand(senderEmail, subject, content, null, null);
    }

    /**
     * 🆕 Procesa comandos recibidos via email con soporte para respuesta como reply
     */
    public void processEmailCommand(String senderEmail, String subject, String content, String originalSubject,
            String messageId) {
        try {
            System.out.println("\n🔄 Procesando comando de email:");
            System.out.println("   📧 From: " + senderEmail);
            System.out.println("   📝 Subject: " + subject);
            if (messageId != null) {
                System.out.println("   🆔 Message-ID: " + messageId);
                System.out.println("   💬 Responderá como REPLY al email original");
            }

            // Verificar conexión antes de procesar
            if (!TestConnection.testConnection()) {
                System.err.println("   ❌ Error de conexión a base de datos");
                sendErrorEmailAsReply(senderEmail,
                        "Error de conexión a la base de datos. Verifica que PostgreSQL esté ejecutándose.",
                        originalSubject, messageId);
                return;
            }

            // Verificar si el comando es para registro
            if (isRegistrationCommand(subject)) {
                processRegistrationCommand(senderEmail, subject, originalSubject, messageId);
                return;
            }

            // Verificar si el usuario está registrado
            if (!isUserRegistered(senderEmail)) {
                System.out.println("   ❌ Usuario no registrado: " + senderEmail);
                sendWelcomeEmailAsReply(senderEmail, originalSubject, messageId);
                return;
            }

            // Verificar si es un comando válido
            if (!isCommandEmail(subject)) {
                System.out.println("   ⏭️ No es un comando válido, omitiendo");
                return;
            }

            // Procesar comando directamente
            String comando = subject.toLowerCase().trim();
            processDirectCommand(senderEmail, comando, originalSubject, messageId);

        } catch (Exception e) {
            System.err.println("❌ Error procesando comando: " + e.getMessage());
            sendErrorEmailAsReply(senderEmail, "Error procesando comando: " + e.getMessage(), originalSubject,
                    messageId);
        }
    }

    /**
     * Verifica si el usuario está registrado en el sistema
     */
    private boolean isUserRegistered(String email) {
        try {
            DUsuario dUser = new DUsuario();
            return dUser.existsByEmail(email);
        } catch (Exception e) {
            System.err.println("❌ Error verificando usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si el comando es para registro
     */
    private boolean isRegistrationCommand(String subject) {
        if (subject == null)
            return false;
        return subject.toLowerCase().trim().startsWith("registrar ");
    }

    /**
     * Procesa comandos de registro
     */
    private void processRegistrationCommand(String senderEmail, String subject, String originalSubject,
            String messageId) {
        try {
            String[] parts = subject.trim().split("\\s+");

            if (parts.length < 5) {
                sendRegistrationHelp(senderEmail, "❌ Faltan parámetros para el registro", originalSubject, messageId);
                return;
            }

            String nombre = parts[1];
            String apellido = parts[2];
            String telefono = parts[3];
            String genero = parts[4];

            // Registrar usuario
            DUsuario dUser = new DUsuario();
            List<String[]> userData = dUser.register(nombre, apellido, telefono, genero, senderEmail);

            String html = HtmlRes.generateSuccess("Registro Exitoso",
                    "¡Bienvenido " + nombre + "! Tu cuenta ha sido creada exitosamente. " +
                            "Ahora puedes usar todos los comandos del sistema. Envía 'help' para ver los comandos disponibles.");

            if (messageId != null && originalSubject != null) {
                emailRelay.replyToEmail(senderEmail, originalSubject, html, messageId);
            } else {
                emailRelay.sendEmail("servidor-independiente@localhost", senderEmail,
                        "[Registro Exitoso] Bienvenido al Sistema", html);
            }

        } catch (Exception e) {
            System.err.println("❌ Error en registro: " + e.getMessage());
            sendRegistrationHelp(senderEmail, "❌ Error en el registro: " + e.getMessage(), originalSubject, messageId);
        }
    }

    /**
     * Envía ayuda para el registro
     */
    private void sendRegistrationHelp(String email, String error, String originalSubject, String messageId) {
        String html = HtmlRes.generateError("Error de Registro",
                error + "<br><br>" +
                        "<strong>Formato correcto:</strong><br>" +
                        "<code>registrar nombre apellido telefono genero</code><br><br>" +
                        "<strong>Ejemplo:</strong><br>" +
                        "<code>registrar Juan Pérez 123456789 masculino</code>");

        if (messageId != null && originalSubject != null) {
            emailRelay.replyToEmail(email, originalSubject, html, messageId);
        } else {
            emailRelay.sendEmail("servidor-independiente@localhost", email,
                    "[Error] Registro Incorrecto", html);
        }
    }

    /**
     * Envía email de bienvenida para usuarios no registrados como reply
     */
    private void sendWelcomeEmailAsReply(String email, String originalSubject, String messageId) {
        String html = HtmlRes.generateWelcome(email);
        if (messageId != null && originalSubject != null) {
            emailRelay.replyToEmail(email, originalSubject, html, messageId);
        } else {
            emailRelay.sendEmail("servidor-independiente@localhost", email,
                    "[Bienvenido] Registro Requerido", html);
        }
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

    private void processDirectCommand(String senderEmail, String comando, String originalSubject, String messageId) {
        try {
            String[] parts = comando.split("\\s+");

            if (parts.length == 0) {
                sendErrorEmail(senderEmail, "Comando vacío", originalSubject, messageId);
                return;
            }

            String entity = normalizeCommand(parts[0]); // 🆕 NORMALIZAR COMANDO
            String action = parts.length > 1 ? parts[1] : "get";
            String param = parts.length > 2 ? parts[2] : null;

            System.out.println("   🎯 Entidad: " + entity + " (original: " + parts[0] + "), Acción: " + action
                    + ", Parámetro: " + param);

            switch (entity) {
                case "usuario":
                    processUsuarioCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "producto":
                    processProductoCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "categoria":
                    processCategoriaCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "cliente":
                    processClienteCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "tipo_pago":
                    processTipoPagoCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "help":
                    processHelpCommand(senderEmail, comando, originalSubject, messageId);
                    break;
                default:
                    sendErrorEmail(senderEmail,
                            "Comando no reconocido: " + parts[0]
                                    + " (intenta: usuario, producto, categoria, cliente, tipo_pago, help)",
                            originalSubject, messageId);
            }

        } catch (Exception e) {
            System.err.println("❌ Error en processDirectCommand: " + e.getMessage());
            sendErrorEmail(senderEmail, "Error procesando comando: " + e.getMessage(), originalSubject, messageId);
        }
    }

    private void processUsuarioCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int id = Integer.parseInt(param);
                        List<String[]> userData = nUsuario.get(id);
                        sendTableResponse(senderEmail, "Usuario encontrado",
                                DUsuario.HEADERS, (ArrayList<String[]>) userData, comando, originalSubject, messageId);
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "Error", "ID inválido: " + param, originalSubject, messageId);
                    }
                } else {
                    List<String[]> usuarios = nUsuario.list();
                    sendTableResponse(senderEmail, "Lista de Usuarios",
                            DUsuario.HEADERS, (ArrayList<String[]>) usuarios, comando, originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "Comando no disponible",
                        "Solo 'usuario get' está disponible por ahora.", originalSubject, messageId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en usuario: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    private void processProductoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int id = Integer.parseInt(param);
                        List<String[]> productoData = dProducto.get(id);
                        sendTableResponse(senderEmail, "Producto encontrado",
                                DProducto.HEADERS, (ArrayList<String[]>) productoData, comando, originalSubject,
                                messageId);
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "Error", "ID inválido: " + param, originalSubject, messageId);
                    }
                } else {
                    List<String[]> productos = dProducto.list();
                    sendTableResponse(senderEmail, "Lista de Productos",
                            DProducto.HEADERS, (ArrayList<String[]>) productos, comando, originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "Comando no disponible",
                        "Solo 'producto get' está disponible por ahora.", originalSubject, messageId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en producto: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    private void processCategoriaCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int id = Integer.parseInt(param);
                        List<String[]> categoriaData = dCategoria.get(id);
                        sendTableResponse(senderEmail, "Categoría encontrada",
                                DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, comando, originalSubject,
                                messageId);
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "Error", "ID inválido: " + param, originalSubject, messageId);
                    }
                } else {
                    List<String[]> categorias = dCategoria.list();
                    sendTableResponse(senderEmail, "Lista de Categorías",
                            DCategoria.HEADERS, (ArrayList<String[]>) categorias, comando, originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "Comando no disponible",
                        "Solo 'categoria get' está disponible por ahora.", originalSubject, messageId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en categoria: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    private void processHelpCommand(String senderEmail, String comando, String originalSubject, String messageId) {
        try {
            String[] headers = { "Comando", "Disponible", "Descripción" };
            ArrayList<String[]> data = new ArrayList<>();

            // Comandos de registro y autenticación
            data.add(new String[] { "registrar nombre apellido telefono genero", "✅ SÍ", "Registrarse en el sistema" });

            // Comandos de consulta (GET) - Ahora con soporte singular/plural
            data.add(new String[] { "usuario get / usuarios get", "✅ SÍ", "Lista todos los usuarios" });
            data.add(new String[] { "usuario get <id> / usuarios get <id>", "✅ SÍ", "Usuario específico por ID" });
            data.add(new String[] { "producto get / productos get", "✅ SÍ", "Lista todos los productos" });
            data.add(new String[] { "producto get <id> / productos get <id>", "✅ SÍ", "Producto específico por ID" });
            data.add(new String[] { "categoria get / categorias get", "✅ SÍ", "Lista todas las categorías" });
            data.add(
                    new String[] { "categoria get <id> / categorias get <id>", "✅ SÍ", "Categoría específica por ID" });
            data.add(new String[] { "cliente get / clientes get", "✅ SÍ", "Lista todos los clientes" });
            data.add(new String[] { "cliente get <id> / clientes get <id>", "✅ SÍ", "Cliente específico por ID" });
            data.add(new String[] { "tipo_pago get / tipos_pago get", "✅ SÍ", "Lista todos los tipos de pago" });
            data.add(new String[] { "tipo_pago get <id> / tipos_pago get <id>", "✅ SÍ",
                    "Tipo de pago específico por ID" });

            // Comandos de administración (próximamente)
            data.add(new String[] { "producto add nombre precio_compra precio_venta descripcion categoria_id",
                    "⏳ DESARROLLO", "Agregar nuevo producto" });
            data.add(new String[] { "categoria add nombre descripcion", "⏳ DESARROLLO", "Agregar nueva categoría" });
            data.add(new String[] { "cliente add nit user_id", "⏳ DESARROLLO", "Agregar nuevo cliente" });
            data.add(new String[] { "tipo_pago add tipo_pago", "⏳ DESARROLLO", "Agregar tipo de pago" });

            // Información adicional
            data.add(new String[] { "", "", "" });
            data.add(new String[] { "💡 NOVEDADES v2.0:", "", "" });
            data.add(new String[] { "✅ Comandos en SINGULAR y PLURAL", "NUEVO",
                    "usuario = usuarios, producto = productos" });
            data.add(new String[] { "✅ Respuestas como REPLY", "NUEVO", "El sistema responde a tu email original" });
            data.add(new String[] { "✅ Nombres de categoría en productos", "NUEVO", "Muestra nombre en lugar de ID" });
            data.add(new String[] { "✅ Comandos en CONTENIDO de email", "NUEVO",
                    "Puedes escribir comandos en el texto" });

            // Comandos no implementados
            data.add(new String[] { "", "", "" });
            data.add(new String[] { "evento *", "❌ NO", "Gestión de eventos - No implementado" });
            data.add(new String[] { "reserva *", "❌ NO", "Gestión de reservas - No implementado" });
            data.add(new String[] { "pago *", "❌ NO", "Gestión de pagos - No implementado" });
            data.add(new String[] { "promocion *", "❌ NO", "Gestión de promociones - No implementado" });

            sendTableResponse(senderEmail, "📚 Comandos Disponibles - Sistema CRUD v2.0", headers, data, comando,
                    originalSubject, messageId);
        } catch (Exception ex) {
            System.err.println("❌ Error en help: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    private void processClienteCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int id = Integer.parseInt(param);
                        List<String[]> clienteData = dCliente.get(id);
                        sendTableResponse(senderEmail, "Cliente encontrado",
                                DCliente.HEADERS, (ArrayList<String[]>) clienteData, comando, originalSubject,
                                messageId);
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "Error", "ID inválido: " + param, originalSubject, messageId);
                    }
                } else {
                    List<String[]> clientes = dCliente.list();
                    sendTableResponse(senderEmail, "Lista de Clientes",
                            DCliente.HEADERS, (ArrayList<String[]>) clientes, comando, originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "Comando no disponible",
                        "Solo 'cliente get' está disponible por ahora.", originalSubject, messageId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en cliente: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    private void processTipoPagoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            if ("get".equals(action)) {
                if (param != null) {
                    try {
                        int id = Integer.parseInt(param);
                        List<String[]> tipoPagoData = dTipoPago.get(id);
                        sendTableResponse(senderEmail, "Tipo de pago encontrado",
                                DTipoPago.HEADERS, (ArrayList<String[]>) tipoPagoData, comando, originalSubject,
                                messageId);
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "Error", "ID inválido: " + param, originalSubject, messageId);
                    }
                } else {
                    List<String[]> tiposPago = dTipoPago.list();
                    sendTableResponse(senderEmail, "Lista de Tipos de Pago",
                            DTipoPago.HEADERS, (ArrayList<String[]>) tiposPago, comando, originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "Comando no disponible",
                        "Solo 'tipo_pago get' está disponible por ahora.", originalSubject, messageId);
            }
        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en tipo_pago: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    /**
     * Verifica si el email contiene un comando
     */
    private boolean isCommandEmail(String subject) {
        if (subject == null)
            return false;
        String subjectLower = subject.toLowerCase().trim();
        return subjectLower.startsWith("usuario ") ||
                subjectLower.startsWith("usuarios ") ||
                subjectLower.equals("help") ||
                subjectLower.equals("usuario get") ||
                subjectLower.equals("usuarios get") ||
                subjectLower.startsWith("producto ") ||
                subjectLower.startsWith("productos ") ||
                subjectLower.equals("producto get") ||
                subjectLower.equals("productos get") ||
                subjectLower.startsWith("categoria ") ||
                subjectLower.startsWith("categorias ") ||
                subjectLower.equals("categoria get") ||
                subjectLower.equals("categorias get") ||
                subjectLower.startsWith("cliente ") ||
                subjectLower.startsWith("clientes ") ||
                subjectLower.equals("cliente get") ||
                subjectLower.equals("clientes get") ||
                subjectLower.startsWith("tipo_pago ") ||
                subjectLower.startsWith("tipos_pago ") ||
                subjectLower.equals("tipo_pago get") ||
                subjectLower.equals("tipos_pago get");
    }

    // Implementación de la interfaz ICasoUsoListener (métodos requeridos)
    @Override
    public void usuario(ParamsAction event) {
        // Redirigir al procesamiento directo (sin reply para estos métodos legacy)
        processUsuarioCommand(event.getSender(), "get", null, event.getCommand(), null, null);
    }

    // Implementación para eventos (mantener original)
    @Override
    public void evento(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'evento' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void reserva(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'reserva' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void pago(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'pago' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void proveedor(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'proveedor' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void promocion(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'promocion' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void patrocinador(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'patrocinador' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void patrocinio(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'patrocinio' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void rol(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'rol' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void servicio(ParamsAction event) {
        // Reutilizar para categorías
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        String idParam = event.getParams().get(0);
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> categoriaData = dCategoria.get(id);
                            sendTableResponse(event.getSender(), "Categoría encontrada",
                                    DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand(), null,
                                    null);
                        } catch (NumberFormatException e) {
                            sendSimpleResponse(event.getSender(), "Error", "ID de categoría inválido.", null, null);
                        }
                    } else {
                        List<String[]> categorias = dCategoria.list();
                        sendTableResponse(event.getSender(), "Lista de Categorías",
                                DCategoria.HEADERS, (ArrayList<String[]>) categorias, event.getCommand(), null, null);
                    }
                    break;
                default:
                    sendSimpleResponse(event.getSender(), "Comando no disponible",
                            "Solo 'categoria get' está disponible por ahora.", null, null);
            }
        } catch (SQLException ex) {
            handleError(DATABASE_ERROR, event.getSender(),
                    Collections.singletonList("Error de base de datos: " + ex.getMessage()), null, null);
        }
    }

    @Override
    public void detalleEvento(ParamsAction event) {
        sendSimpleResponse(event.getSender(), "Comando no disponible",
                "El comando 'detalleEvento' aún no está implementado en el servidor independiente.", null, null);
    }

    @Override
    public void error(ParamsAction event) {
        handleError(event.getAction(), event.getSender(), event.getParams(), null, null);
    }

    @Override
    public void help(ParamsAction event) {
        try {
            String[] headers = { "Comando", "Disponible", "Descripción" };
            ArrayList<String[]> data = new ArrayList<>();

            // Comandos disponibles
            data.add(new String[] { "usuario get", "✅ SÍ", "Lista todos los usuarios" });
            data.add(new String[] { "usuario get <id>", "✅ SÍ", "Usuario por ID" });
            data.add(new String[] { "producto get", "✅ SÍ", "Lista todos los productos" });
            data.add(new String[] { "producto get <id>", "✅ SÍ", "Producto por ID" });
            data.add(new String[] { "categoria get", "✅ SÍ", "Lista todas las categorías" });
            data.add(new String[] { "categoria get <id>", "✅ SÍ", "Categoría por ID" });
            data.add(new String[] { "cliente get", "✅ SÍ", "Lista todos los clientes" });
            data.add(new String[] { "cliente get <id>", "✅ SÍ", "Cliente por ID" });
            data.add(new String[] { "tipo_pago get", "✅ SÍ", "Lista todos los tipos de pago" });
            data.add(new String[] { "tipo_pago get <id>", "✅ SÍ", "Tipo de pago por ID" });

            // Comandos no disponibles
            data.add(new String[] { "usuario add/modify/delete", "⏳ DESARROLLO", "CRUD de usuarios" });
            data.add(new String[] { "evento", "❌ NO", "Aún no implementado" });
            data.add(new String[] { "reserva", "❌ NO", "Aún no implementado" });
            data.add(new String[] { "pago", "❌ NO", "Aún no implementado" });

            sendTableResponse(event.getSender(), "Comandos disponibles - Servidor Independiente", headers, data,
                    event.getCommand(), null, null);
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error: " + ex.getMessage()),
                    null, null);
        }
    }

    /**
     * Envía respuesta simple via email
     */
    private void sendSimpleResponse(String email, String title, String message, String originalSubject,
            String messageId) {
        String content = HtmlRes.generateText(new String[] { title, message });
        if (messageId != null && originalSubject != null) {
            emailRelay.replyToEmail(email, originalSubject, content, messageId);
        } else {
            emailRelay.sendEmail("servidor-independiente@localhost", email, "[Servidor Independiente] " + title,
                    content);
        }
    }

    /**
     * Envía respuesta con tabla via email
     */
    private void sendTableResponse(String email, String title, String[] headers, ArrayList<String[]> data,
            String command, String originalSubject, String messageId) {
        String content = HtmlRes.generateTable(title, headers, data);
        String subject = command != null ? "[Comando: " + command + "] " + title : "[Servidor Independiente] " + title;
        if (messageId != null && originalSubject != null) {
            emailRelay.replyToEmail(email, originalSubject, content, messageId);
        } else {
            emailRelay.sendEmail("servidor-independiente@localhost", email, subject, content);
        }
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
     * Envía email de error
     */
    private void sendErrorEmail(String email, String error, String originalSubject, String messageId) {
        sendSimpleResponse(email, "Error de Procesamiento", error, originalSubject, messageId);
    }

    /**
     * Envía email de error como reply
     */
    private void sendErrorEmailAsReply(String email, String error, String originalSubject, String messageId) {
        sendSimpleResponse(email, "Error de Procesamiento", error, originalSubject, messageId);
    }

    public static void main(String[] args) {
        EmailAppIndependiente app = new EmailAppIndependiente();
        app.start();

        System.out.println("\n🎯 COMANDOS DISPONIBLES POR EMAIL:");
        System.out.println("📧 Envía un email con el comando en el asunto:");
        System.out.println("   • 'usuario get' - Lista todos los usuarios");
        System.out.println("   • 'usuario get 1' - Usuario ID 1");
        System.out.println("   • 'producto get' - Lista todos los productos");
        System.out.println("   • 'categoria get' - Lista todas las categorías");
        System.out.println("   • 'help' - Muestra ayuda");

        // Ejemplo de uso
        System.out.println("\n🔧 Para usar este EmailApp:");
        System.out.println("   EmailAppIndependiente app = new EmailAppIndependiente();");
        System.out.println("   app.processEmailCommand(\"test@test.com\", \"usuario get\", \"contenido\");");
    }
}