package com.mycompany.parcial1.tecnoweb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.DCarrito;
import data.DCategoria;
import data.DCliente;
import data.DProducto;
import data.DTipoPago;
import data.DUsuario;
import data.DVenta;
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
    private DCarrito dCarrito;
    private DVenta dVenta;

    public EmailAppIndependiente() {
        this.emailRelay = new GmailRelay();
        this.nUsuario = new NUsuario();
        this.dProducto = new DProducto();
        this.dCategoria = new DCategoria();
        this.dCliente = new DCliente();
        this.dTipoPago = new DTipoPago();
        this.dCarrito = new DCarrito();
        this.dVenta = new DVenta();

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
            if (content != null && !content.trim().isEmpty()) {
                System.out.println(
                        "   💬 Content preview: " + content.substring(0, Math.min(content.length(), 50)) + "...");
            }
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

            // Verificar si el comando es para registro (solo en subject)
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

            // 🆕 BUSCAR COMANDOS EN SUBJECT Y CONTENT
            String comando = null;

            // Primero buscar en el subject
            if (isCommandEmail(subject)) {
                comando = subject.toLowerCase().trim();
                System.out.println("   ✅ Comando encontrado en SUBJECT: " + comando);
            }
            // Si no hay comando en subject, buscar en content (para respuestas)
            else if (content != null && !content.trim().isEmpty()) {
                String comandoEnContent = extractCommandFromContent(content);
                if (comandoEnContent != null && isCommandEmail(comandoEnContent)) {
                    comando = comandoEnContent;
                    System.out.println("   ✅ Comando encontrado en CONTENT: " + comando);
                }
            }

            // Si no se encontró comando válido, omitir
            if (comando == null) {
                System.out.println("   ⏭️ No se encontraron comandos válidos, omitiendo");
                return;
            }

            // Procesar comando encontrado
            processDirectCommand(senderEmail, comando, originalSubject, messageId);

        } catch (Exception e) {
            System.err.println("❌ Error procesando comando: " + e.getMessage());
            sendErrorEmailAsReply(senderEmail, "Error procesando comando: " + e.getMessage(), originalSubject,
                    messageId);
        }
    }

    /**
     * 🆕 Extrae comando del contenido del email (para respuestas)
     * Busca líneas que no empiecen con ">" (texto citado) y que contengan comandos
     * válidos
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

            // 🆕 LIMPIAR CORCHETES de los comandos
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
                case "carrito":
                    processCarritoCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "checkout":
                    processCheckoutCommand(senderEmail, comando, originalSubject, messageId);
                    break;
                case "pago":
                    processPagoCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "ventas":
                case "compras":
                    processVentasCommand(senderEmail, action, param, comando, originalSubject, messageId);
                    break;
                case "help":
                    processHelpCommand(senderEmail, comando, originalSubject, messageId);
                    break;
                default:
                    sendErrorEmail(senderEmail,
                            "Comando no reconocido: " + parts[0]
                                    + " (intenta: usuario, producto, categoria, cliente, tipo_pago, carrito, checkout, pago, ventas, help)",
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

            // Comandos disponibles del sistema de e-commerce
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

            // Comandos del sistema de carrito y e-commerce
            data.add(new String[] { "carrito add [id] [cantidad]", "✅ SÍ", "Agregar producto al carrito" });
            data.add(new String[] { "carrito get", "✅ SÍ", "Ver contenido del carrito" });
            data.add(new String[] { "carrito remove [id]", "✅ SÍ", "Remover producto del carrito" });
            data.add(new String[] { "carrito clear", "✅ SÍ", "Vaciar carrito completo" });
            data.add(new String[] { "checkout", "✅ SÍ", "Crear orden de compra" });
            data.add(new String[] { "pago [venta_id] [tipo_pago_id]", "✅ SÍ", "Completar pago" });
            data.add(new String[] { "ventas get", "✅ SÍ", "Ver historial de compras" });

            sendTableResponse(senderEmail, "Comandos disponibles - Sistema E-commerce", headers, data, comando,
                    originalSubject, messageId);
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, senderEmail, Collections.singletonList("Error: " + ex.getMessage()),
                    null, null);
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
                subjectLower.equals("tipos_pago get") ||
                // 🆕 Comandos del sistema de carrito y e-commerce
                subjectLower.startsWith("carrito ") ||
                subjectLower.equals("carrito get") ||
                subjectLower.equals("checkout") ||
                subjectLower.startsWith("pago ") ||
                subjectLower.startsWith("ventas ") ||
                subjectLower.startsWith("compras ") ||
                subjectLower.equals("ventas get") ||
                subjectLower.equals("compras get");
    }

    // Implementación de la interfaz ICasoUsoListener (métodos requeridos)
    @Override
    public void usuario(ParamsAction event) {
        // Redirigir al procesamiento directo (sin reply para estos métodos legacy)
        processUsuarioCommand(event.getSender(), "get", null, event.getCommand(), null, null);
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
    public void error(ParamsAction event) {
        handleError(event.getAction(), event.getSender(), event.getParams(), null, null);
    }

    @Override
    public void help(ParamsAction event) {
        try {
            String[] headers = { "Comando", "Disponible", "Descripción" };
            ArrayList<String[]> data = new ArrayList<>();

            // Comandos disponibles del sistema de e-commerce
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

            // Comandos del sistema de carrito y e-commerce
            data.add(new String[] { "carrito add [id] [cantidad]", "✅ SÍ", "Agregar producto al carrito" });
            data.add(new String[] { "carrito get", "✅ SÍ", "Ver contenido del carrito" });
            data.add(new String[] { "carrito remove [id]", "✅ SÍ", "Remover producto del carrito" });
            data.add(new String[] { "carrito clear", "✅ SÍ", "Vaciar carrito completo" });
            data.add(new String[] { "checkout", "✅ SÍ", "Crear orden de compra" });
            data.add(new String[] { "pago [venta_id] [tipo_pago_id]", "✅ SÍ", "Completar pago" });
            data.add(new String[] { "ventas get", "✅ SÍ", "Ver historial de compras" });

            sendTableResponse(event.getSender(), "Comandos disponibles - Sistema E-commerce", headers, data,
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

    /**
     * Procesa comandos relacionados con el carrito de compras
     */
    private void processCarritoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            // Obtener ID del cliente basado en el email
            int clienteId = obtenerClienteIdPorEmail(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Cliente No Configurado",
                        String.format("🔍 PROBLEMA DETECTADO:\n" +
                                "Tu usuario (%s) está registrado en el sistema, pero no tienes un perfil de CLIENTE asociado.\n\n"
                                +
                                "📋 PARA RESOLVER ESTE PROBLEMA:\n" +
                                "1. Contacta al administrador del sistema\n" +
                                "2. Solicita que te creen un perfil de cliente\n" +
                                "3. O envía un email con asunto: 'crear cliente para %s'\n\n" +
                                "💡 El sistema requiere que tengas un perfil de cliente para poder realizar compras.\n\n"
                                +
                                "🔧 DETALLES TÉCNICOS:\n" +
                                "- Email detectado: %s\n" +
                                "- Usuario registrado: ✅ SÍ\n" +
                                "- Cliente asociado: ❌ NO\n" +
                                "- Comando solicitado: %s",
                                senderEmail, senderEmail, senderEmail, comando),
                        originalSubject, messageId);
                return;
            }

            // 🆕 VALIDACIÓN ESPECIAL: Detectar si falta la acción "add"
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
                        // Formato: carrito add producto_id cantidad
                        String[] params = comando.split("\\s+");
                        if (params.length >= 4) {
                            try {
                                int productoId = Integer.parseInt(params[2]);
                                int cantidad = Integer.parseInt(params[3]);

                                if (dCarrito.agregarProducto(clienteId, productoId, cantidad)) {
                                    sendSimpleResponse(senderEmail, "✅ Producto Agregado",
                                            String.format(
                                                    "Producto #%d agregado al carrito exitosamente (cantidad: %d).\n\n"
                                                            +
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
                                                "✅ Formato correcto: carrito add [numero_producto] [numero_cantidad]\n"
                                                +
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
                    // Mostrar contenido del carrito
                    List<String[]> carrito = dCarrito.obtenerCarrito(clienteId);
                    if (carrito.isEmpty()) {
                        sendSimpleResponse(senderEmail, "🛒 Carrito Vacío",
                                "Tu carrito está vacío. Usa 'carrito add [producto_id] [cantidad]' para agregar productos.",
                                originalSubject, messageId);
                    } else {
                        double total = dCarrito.obtenerTotalCarrito(clienteId);
                        String titulo = String.format("🛒 Tu Carrito - Total: $%.2f", total);
                        sendTableResponse(senderEmail, titulo, DCarrito.DETALLE_HEADERS,
                                (ArrayList<String[]>) carrito, comando, originalSubject, messageId);
                    }
                    break;

                case "remove":
                    if (param != null) {
                        try {
                            int productoId = Integer.parseInt(param);
                            if (dCarrito.removerProducto(clienteId, productoId)) {
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
                    if (dCarrito.vaciarCarrito(clienteId)) {
                        sendSimpleResponse(senderEmail, "✅ Carrito Vaciado",
                                "Tu carrito ha sido vaciado exitosamente.", originalSubject, messageId);
                    } else {
                        sendSimpleResponse(senderEmail, "❌ Error",
                                "No se pudo vaciar el carrito.", originalSubject, messageId);
                    }
                    break;

                default:
                    // 🆕 DETECTAR AQUÍ TAMBIÉN si es un número
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
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        }
    }

    /**
     * Obtiene el ID del cliente basado en su email
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
     * Procesa comando de checkout
     */
    private void processCheckoutCommand(String senderEmail, String comando, String originalSubject, String messageId) {
        try {
            int clienteId = obtenerClienteIdPorEmail(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Error",
                        "No se encontró perfil de cliente. Contacta al administrador.", originalSubject, messageId);
                return;
            }

            int ventaId = dVenta.procesarCheckout(clienteId);
            sendSimpleResponse(senderEmail, "✅ Checkout Exitoso",
                    String.format("Tu orden #%d ha sido creada. Usa 'pago %d [tipo_pago_id]' para completar la compra.",
                            ventaId, ventaId),
                    originalSubject, messageId);

        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en checkout: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error en checkout: " + ex.getMessage(), originalSubject, messageId);
        } catch (Exception e) {
            System.err.println("❌ Error en checkout: " + e.getMessage());
            sendErrorEmail(senderEmail, "Error procesando checkout: " + e.getMessage(), originalSubject, messageId);
        }
    }

    /**
     * Procesa comandos de pago
     */
    private void processPagoCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            int clienteId = obtenerClienteIdPorEmail(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Error",
                        "No se encontró perfil de cliente. Contacta al administrador.", originalSubject, messageId);
                return;
            }

            if (param != null) {
                String[] params = comando.split("\\s+");
                if (params.length >= 3) {
                    try {
                        int ventaId = Integer.parseInt(params[1]);
                        int tipoPagoId = Integer.parseInt(params[2]);

                        if (dVenta.completarVenta(ventaId, tipoPagoId, clienteId)) {
                            sendSimpleResponse(senderEmail, "✅ Pago Completado",
                                    String.format("¡Compra exitosa! Tu orden #%d ha sido pagada y procesada. " +
                                            "El stock ha sido actualizado automáticamente.", ventaId),
                                    originalSubject, messageId);
                        } else {
                            sendSimpleResponse(senderEmail, "❌ Error en Pago",
                                    "No se pudo procesar el pago.", originalSubject, messageId);
                        }
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "❌ Error",
                                "IDs inválidos. Formato: pago [venta_id] [tipo_pago_id]", originalSubject, messageId);
                    }
                } else {
                    sendSimpleResponse(senderEmail, "❌ Parámetros insuficientes",
                            "Formato: pago [venta_id] [tipo_pago_id]", originalSubject, messageId);
                }
            } else {
                sendSimpleResponse(senderEmail, "❌ Parámetros faltantes",
                        "Formato: pago [venta_id] [tipo_pago_id]. Usa 'tipos_pago get' para ver opciones.",
                        originalSubject, messageId);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en pago: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        } catch (Exception e) {
            System.err.println("❌ Error en pago: " + e.getMessage());
            sendErrorEmail(senderEmail, "Error procesando pago: " + e.getMessage(), originalSubject, messageId);
        }
    }

    /**
     * Procesa comandos de ventas/compras
     */
    private void processVentasCommand(String senderEmail, String action, String param, String comando,
            String originalSubject, String messageId) {
        try {
            int clienteId = obtenerClienteIdPorEmail(senderEmail);
            if (clienteId == 0) {
                sendSimpleResponse(senderEmail, "❌ Error",
                        "No se encontró perfil de cliente. Contacta al administrador.", originalSubject, messageId);
                return;
            }

            if ("get".equals(action)) {
                if (param != null) {
                    // Ver detalle de una venta específica
                    try {
                        int ventaId = Integer.parseInt(param);
                        List<String[]> detalle = dVenta.obtenerDetalleVenta(ventaId, clienteId);

                        if (detalle.isEmpty()) {
                            sendSimpleResponse(senderEmail, "❌ Venta no encontrada",
                                    "No se encontró la venta especificada o no tienes permisos para verla.",
                                    originalSubject, messageId);
                        } else {
                            sendTableResponse(senderEmail, "📋 Detalle de Venta #" + ventaId,
                                    DCarrito.DETALLE_HEADERS, (ArrayList<String[]>) detalle, comando,
                                    originalSubject, messageId);
                        }
                    } catch (NumberFormatException e) {
                        sendSimpleResponse(senderEmail, "❌ Error",
                                "ID de venta inválido.", originalSubject, messageId);
                    }
                } else {
                    // Ver historial de ventas
                    List<String[]> historial = dVenta.obtenerHistorialVentas(clienteId);

                    if (historial.isEmpty()) {
                        sendSimpleResponse(senderEmail, "📋 Sin Compras",
                                "No tienes compras registradas aún.", originalSubject, messageId);
                    } else {
                        String[] headers = { "ID", "Fecha", "Total", "Estado", "Método Pago" };
                        sendTableResponse(senderEmail, "📋 Tu Historial de Compras", headers,
                                (ArrayList<String[]>) historial, comando, originalSubject, messageId);
                    }
                }
            } else {
                sendSimpleResponse(senderEmail, "❌ Acción no válida",
                        "Usa 'ventas get' para ver tu historial o 'ventas get [id]' para ver detalle.",
                        originalSubject, messageId);
            }

        } catch (SQLException ex) {
            System.err.println("❌ Error SQL en ventas: " + ex.getMessage());
            sendErrorEmail(senderEmail, "Error de base de datos: " + ex.getMessage(), originalSubject, messageId);
        } catch (Exception e) {
            System.err.println("❌ Error en ventas: " + e.getMessage());
            sendErrorEmail(senderEmail, "Error procesando consulta de ventas: " + e.getMessage(), originalSubject,
                    messageId);
        }
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