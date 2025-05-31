package com.mycompany.parcial1.tecnoweb;

import java.util.Scanner;

import postgresConecction.DBConnection;
import servidor.GmailMonitorComandos;
import servidor.HTTPEmailServer;

/**
 * Lanzador principal que permite elegir entre diferentes modos de
 * funcionamiento
 * 
 * @author MARCO
 */
public class LanzadorPrincipal {

    public static void main(String[] args) {
        System.out.println("🚀 SISTEMA DE EMAIL PERSONALIZADO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👨‍💻 Desarrollador: Marco David Toledo");
        System.out.println("📧 Email: marcodavidtoledo@gmail.com");
        System.out.println();

        mostrarMenu();

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

        private static void mostrarMenu() {
        System.out.println("🎯 SELECCIONA UNA OPCIÓN:");
        System.out.println();
        System.out.println("📧 TECNOWEB ORIGINAL:");
        System.out.println("1. 📧 EmailApp Tecnoweb - Versión original POP3");
        System.out.println();
        System.out.println("🛒 SISTEMA DE E-COMMERCE:");
        System.out.println("2. 🤖 Monitor Gmail con E-commerce - Sistema completo de carrito y ventas");
        System.out.println("3. 🌐 Servidor HTTP Email - Interfaz web + API REST");
        System.out.println("4. 🔄 Sistema Completo - Monitor Gmail + Servidor HTTP");
        
        System.out.println();
        System.out.println("🛠️ HERRAMIENTAS:");
        System.out.println("5. 📊 Test EmailApp E-commerce - Prueba sistema de carrito");
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
        System.out.println("\n📧 Iniciando EmailApp Tecnoweb - Versión Original POP3...");
        System.out.println("🔗 Conectando a servidor tecnoweb");
        System.out.println("📧 Protocolo: POP3");
        System.out.println("🗄️ Base de datos: " + DBConnection.TecnoWeb.database + " en " + DBConnection.TecnoWeb.server);
        System.out.println();
        System.out.println("📝 COMANDOS DISPONIBLES:");
        System.out.println("   📋 CONSULTAS: 'usuario get', 'producto get', 'categoria get'");
        System.out.println("   🔐 REGISTRO: 'registrar Juan Pérez 123456789 masculino'");
        System.out.println("   ❓ AYUDA: 'help'");
        System.out.println();
        System.out.println("⚠️ NOTA: Requiere que el servidor tecnoweb esté funcionando");
        System.out.println();
        
        try {
            // Usar la nueva clase EmailAppTecnoweb que se conecta realmente a tecnoweb
            EmailAppTecnoweb app = new EmailAppTecnoweb();
            System.out.println("✅ EmailApp Tecnoweb iniciado correctamente");
            app.start();
        } catch (Exception e) {
            System.err.println("❌ Error iniciando EmailApp Tecnoweb: " + e.getMessage());
            System.err.println("💡 Sugerencia: Verifica que el servidor tecnoweb esté funcionando");
        }
    }
    
    private static void ejecutarMonitorGmailComandos() {
        System.out.println("\n🤖 Iniciando Monitor Gmail con Sistema E-commerce...");
        System.out.println("📧 Monitoreando: marcodavidtoledo@gmail.com");
        System.out.println("🗄️ Base de datos: " + DBConnection.database + " en " + DBConnection.server);
        System.out.println("🛒 Sistema completo de carrito, checkout y pagos");
        System.out.println();
        System.out.println("📝 COMANDOS DE E-COMMERCE DISPONIBLES:");
        System.out.println("   🔐 REGISTRO: 'registrar Juan Pérez 123456789 masculino'");
        System.out.println("   📋 CONSULTAS: 'productos get', 'categorias get', 'tipos_pago get'");
        System.out.println("   🛒 CARRITO: 'carrito add 161 3', 'carrito get', 'carrito clear'");
        System.out.println("   💳 COMPRAS: 'checkout', 'pago 123 1', 'ventas get'");
        System.out.println("   ❓ AYUDA: 'help'");
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
        System.out.println("\n📊 Ejecutando Test EmailApp E-commerce...");
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