package com.mycompany.parcial1.tecnoweb;

import java.util.Scanner;

import postgresConecction.DBConnection;
import servidor.GmailMonitorComandos;
import servidor.HTTPEmailServer;

/**
 * Lanzador principal que permite elegir entre diferentes modos de funcionamiento
 * @author MARCO
 */
public class LanzadorPrincipal {

    public static void main(String[] args) {
        System.out.println("🚀 SISTEMA DE EMAIL PERSONALIZADO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👨‍💻 Desarrollador: Marco David Toledo");
        System.out.println("📧 Email: marcodavidtoledo@gmail.com");
        System.out.println();
        
        mostrarOpciones();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Selecciona una opción (1-6): ");
        
        try {
            int opcion = scanner.nextInt();
            ejecutarOpcion(opcion);
        } catch (Exception e) {
            System.err.println("❌ Opción inválida");
            main(args); // Reiniciar
        } finally {
            scanner.close();
        }
    }
    
    private static void mostrarOpciones() {
        System.out.println("📋 MODOS DISPONIBLES:");
        System.out.println();
        
        System.out.println("🔧 VERSIÓN TECNOWEB (Original):");
        System.out.println("1. 📧 EmailApp Tecnoweb - Procesa emails via POP3 desde mail.tecnoweb.org.bo");
        
        System.out.println();
        System.out.println("🆕 VERSIÓN INDEPENDIENTE (Nueva):");
        System.out.println("2. 🤖 Monitor Gmail con Comandos - Procesa comandos CRUD via email");
        System.out.println("3. 🌐 Servidor HTTP Email - Interfaz web + API REST");
        System.out.println("4. 🔄 Sistema Completo - Monitor Gmail + Servidor HTTP");
        
        System.out.println();
        System.out.println("🛠️ HERRAMIENTAS:");
        System.out.println("5. 📊 Test EmailApp Independiente - Prueba procesamiento de comandos");
        System.out.println("6. ❓ Mostrar información del sistema");
        System.out.println();
    }
    
    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                ejecutarEmailAppTecnoweb();
                break;
            case 2:
                ejecutarMonitorGmailComandos();
                break;
            case 3:
                ejecutarServidorHTTP();
                break;
            case 4:
                ejecutarSistemaCompleto();
                break;
            case 5:
                ejecutarTestEmailApp();
                break;
            case 6:
                mostrarInformacionSistema();
                break;
            default:
                System.err.println("❌ Opción no válida");
        }
    }
    
    private static void ejecutarEmailAppTecnoweb() {
        System.out.println("\n🔧 Iniciando EmailApp TECNOWEB...");
        System.out.println("📧 Servidor: mail.tecnoweb.org.bo");
        System.out.println("🗄️ Base de datos: " + DBConnection.TecnoWeb.database);
        System.out.println("⚠️ NOTA: Este modo puede no funcionar si mail.tecnoweb.org.bo está inaccesible");
        
        // Cambiar temporalmente la configuración a tecnoweb
        String originalDb = DBConnection.database;
        String originalServer = DBConnection.server;
        String originalUser = DBConnection.user;
        String originalPassword = DBConnection.password;
        
        DBConnection.database = DBConnection.TecnoWeb.database;
        DBConnection.server = DBConnection.TecnoWeb.server;
        DBConnection.user = DBConnection.TecnoWeb.user;
        DBConnection.password = DBConnection.TecnoWeb.password;
        
        try {
            EmailApp app = new EmailApp();
            app.start();
        } finally {
            // Restaurar configuración original
            DBConnection.database = originalDb;
            DBConnection.server = originalServer;
            DBConnection.user = originalUser;
            DBConnection.password = originalPassword;
        }
    }
    
    private static void ejecutarMonitorGmailComandos() {
        System.out.println("\n🤖 Iniciando Monitor Gmail con Comandos...");
        System.out.println("📧 Monitoreando: marcodavidtoledo@gmail.com");
        System.out.println("🗄️ Base de datos: " + DBConnection.database + " en " + DBConnection.server);
        System.out.println("🎯 Solo responde a comandos específicos en el asunto");
        System.out.println();
        System.out.println("📝 COMANDOS DISPONIBLES:");
        System.out.println("   • 'usuario get' - Lista todos los usuarios");
        System.out.println("   • 'usuario get 1' - Obtiene usuario por ID");
        System.out.println("   • 'help' - Muestra ayuda");
        System.out.println();
        
        GmailMonitorComandos monitor = new GmailMonitorComandos();
        monitor.startMonitoring();
    }
    
    private static void ejecutarServidorHTTP() {
        System.out.println("\n🌐 Iniciando Servidor HTTP Email...");
        System.out.println("🔗 URL: http://localhost:8080");
        System.out.println("📧 Endpoint API: POST /send-email");
        System.out.println("🖥️ Interfaz web disponible en http://localhost:8080");
        System.out.println();
        
        try {
            HTTPEmailServer server = new HTTPEmailServer();
            server.start();
        } catch (Exception e) {
            System.err.println("❌ Error iniciando servidor HTTP: " + e.getMessage());
        }
    }
    
    private static void ejecutarSistemaCompleto() {
        System.out.println("\n🔄 Iniciando Sistema COMPLETO...");
        System.out.println("🤖 Monitor Gmail + 🌐 Servidor HTTP");
        System.out.println("📧 Funcionalidad DUAL: Comandos via email + API REST");
        System.out.println();
        
        // Iniciar servidor HTTP en un hilo separado
        Thread httpThread = new Thread(() -> {
            try {
                HTTPEmailServer server = new HTTPEmailServer();
                server.start();
            } catch (Exception e) {
                System.err.println("❌ Error en servidor HTTP: " + e.getMessage());
            }
        });
        httpThread.setName("HTTP Server");
        httpThread.start();
        
        // Esperar un poco antes de iniciar el monitor
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Iniciar monitor Gmail
        GmailMonitorComandos monitor = new GmailMonitorComandos();
        monitor.startMonitoring();
    }
    
    private static void ejecutarTestEmailApp() {
        System.out.println("\n📊 Ejecutando Test EmailApp Independiente...");
        System.out.println("🗄️ Base de datos: " + DBConnection.database);
        
        EmailAppIndependiente app = new EmailAppIndependiente();
        app.start();
        
        // Ejecutar algunos comandos de prueba
        System.out.println("\n🧪 Ejecutando comandos de prueba:");
        
        System.out.println("\n1. Procesando comando 'help':");
        app.processEmailCommand("test@test.com", "help", "Solicito ayuda");
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n2. Procesando comando 'usuario get':");
        app.processEmailCommand("test@test.com", "usuario get", "Lista de usuarios");
        
        System.out.println("\n✅ Test completado. Los emails de respuesta deberían haber sido enviados.");
    }
    
    private static void mostrarInformacionSistema() {
        System.out.println("\n📋 INFORMACIÓN DEL SISTEMA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.println("\n🔧 CONFIGURACIÓN ACTUAL:");
        System.out.println("📊 Base de datos: " + DBConnection.database);
        System.out.println("🖥️ Servidor DB: " + DBConnection.server + ":" + DBConnection.port);
        System.out.println("👤 Usuario DB: " + DBConnection.user);
        System.out.println("📧 Gmail: marcodavidtoledo@gmail.com");
        
        System.out.println("\n🆚 DIFERENCIAS ENTRE VERSIONES:");
        System.out.println("📧 TECNOWEB:");
        System.out.println("   • Usa POP3 para recibir emails");
        System.out.println("   • Base de datos: " + DBConnection.TecnoWeb.database);
        System.out.println("   • Servidor: " + DBConnection.TecnoWeb.server);
        System.out.println("   • ⚠️ Puede fallar si tecnoweb está caído");
        
        System.out.println("\n🆕 INDEPENDIENTE:");
        System.out.println("   • Usa Gmail IMAP para recibir emails");
        System.out.println("   • Base de datos: " + DBConnection.database + " (local)");
        System.out.println("   • Servidor: " + DBConnection.server);
        System.out.println("   • ✅ Completamente independiente");
        System.out.println("   • 🤖 Procesa comandos CRUD via email");
        System.out.println("   • 🌐 Servidor HTTP con interfaz web");
        
        System.out.println("\n📝 COMANDOS DISPONIBLES VIA EMAIL:");
        System.out.println("   • usuario get - Lista usuarios");
        System.out.println("   • usuario get <id> - Usuario por ID");
        System.out.println("   • help - Muestra ayuda");
        System.out.println("   • (Otros comandos en desarrollo)");
        
        System.out.println("\n🔗 ENDPOINTS HTTP:");
        System.out.println("   • GET  / - Interfaz web");
        System.out.println("   • POST /send-email - Enviar email");
        
        System.out.println("\n📦 ARCHIVOS PRINCIPALES:");
        System.out.println("   • EmailApp.java - Versión tecnoweb");
        System.out.println("   • EmailAppIndependiente.java - Versión independiente");
        System.out.println("   • GmailMonitorComandos.java - Monitor híbrido");
        System.out.println("   • HTTPEmailServer.java - Servidor web");
        System.out.println("   • sql/init_database.sql - Inicialización DB");
        
        System.out.println("\n💡 Para inicializar la base de datos local:");
        System.out.println("   psql -U postgres -f sql/init_database.sql");
    }
} 