import java.util.Scanner;
import servidor.GmailMonitorComandos;
import com.mycompany.parcial1.tecnoweb.EmailAppIndependiente;

/**
 * 🚀 LANZADOR PRINCIPAL - SISTEMA DE EMAIL CON COMANDOS
 * 
 * @author Jairo Jairo Jairo
 * @email JairoJairoJairo@gmail.com
 * @version 2.0 - Diciembre 2024
 */
public class LanzadorPrincipal {

    public static void main(String[] args) {
        mostrarBienvenida();
        mostrarOpciones();
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("\n👉 Selecciona una opción (1-4, 0 para salir): ");
            
            try {
                int opcion = scanner.nextInt();
                
                if (opcion == 0) {
                    System.out.println("\n👋 ¡Hasta luego!");
                    break;
                }
                
                ejecutarOpcion(opcion);
                
            } catch (Exception e) {
                System.err.println("❌ Opción inválida. Intenta de nuevo.");
                scanner.nextLine(); // Limpiar buffer
            }
        }
        
        scanner.close();
    }
    
    private static void mostrarBienvenida() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🚀    SISTEMA DE EMAIL CON COMANDOS - v2.0");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("👨‍💻 Desarrollador: Jairo Jairo Jairo");
        System.out.println("📧 Email: JairoJairoJairo@gmail.com");
        System.out.println("🗄️ Base de datos: PostgreSQL local");
        System.out.println("📬 Monitoreo: Gmail IMAP en tiempo real");
        System.out.println();
    }
    
    private static void mostrarOpciones() {
        System.out.println("📋 OPCIONES DISPONIBLES:");
        System.out.println();
        
        System.out.println("1. 🤖 Monitor Gmail - Procesamiento automático de emails");
        System.out.println("   ✅ Detecta comandos en asunto Y contenido");
        System.out.println("   ✅ Procesa respuestas a emails del sistema");
        System.out.println("   ✅ Validación completa con sugerencias");
        System.out.println();
        
        System.out.println("2. 🧪 Test EmailApp - Pruebas locales sin emails reales");
        System.out.println("   ✅ Simula procesamiento de comandos");
        System.out.println("   ✅ Útil para desarrollo y debugging");
        System.out.println();
        
        System.out.println("3. 📊 Información del Sistema - Configuración y comandos");
        System.out.println("   ✅ Configuración actual");
        System.out.println("   ✅ Comandos disponibles");
        System.out.println("   ✅ Instrucciones de uso");
        System.out.println();
        
        System.out.println("4. 🔧 Configuración avanzada");
        System.out.println("   ✅ Cambiar configuración de base de datos");
        System.out.println("   ✅ Verificar conexiones");
        System.out.println();
        
        System.out.println("0. 🚪 Salir");
    }
    
    private static void ejecutarOpcion(int opcion) {
        switch (opcion) {
            case 1:
                ejecutarMonitorGmail();
                break;
            case 2:
                ejecutarTestEmailApp();
                break;
            case 3:
                mostrarInformacionSistema();
                break;
            case 4:
                configuracionAvanzada();
                break;
            default:
                System.err.println("❌ Opción no válida");
        }
    }
    
    private static void ejecutarMonitorGmail() {
        System.out.println("\n🤖 INICIANDO MONITOR GMAIL...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📧 Monitoreando: JairoJairoJairo@gmail.com");
        System.out.println("🎯 Detecta comandos en ASUNTO y CONTENIDO");
        System.out.println("📝 Procesa respuestas a emails del sistema");
        System.out.println();
        
        System.out.println("🔧 COMANDOS DISPONIBLES:");
        System.out.println("   • registrar Juan Pérez 123456789 masculino");
        System.out.println("   • usuario get");
        System.out.println("   • producto get");
        System.out.println("   • categoria get");
        System.out.println("   • cliente get");
        System.out.println("   • tipo_pago get");
        System.out.println("   • help");
        System.out.println();
        
        System.out.println("💡 NOVEDADES v2.0:");
        System.out.println("   ✅ Responde a emails: cuando respondes a un email del sistema");
        System.out.println("      escribiendo un comando, el sistema lo detecta");
        System.out.println("   ✅ Detección inteligente en contenido además del asunto");
        System.out.println("   ✅ Limpieza automática de HTML y texto citado");
        System.out.println();
        
        System.out.println("⚠️ NOTA: El monitor se ejecutará indefinidamente.");
        System.out.println("   Presiona Ctrl+C para detenerlo");
        System.out.println();
        
        GmailMonitorComandos monitor = new GmailMonitorComandos();
        monitor.startMonitoring();
    }
    
    private static void ejecutarTestEmailApp() {
        System.out.println("\n🧪 EJECUTANDO TEST EMAILAPP...");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        EmailAppIndependiente app = new EmailAppIndependiente();
        
        // Simular comandos
        System.out.println("📝 Simulando comando: help");
        app.processEmailCommand("test@ejemplo.com", "help", "Solicito ayuda");
        
        System.out.println("\n📝 Simulando comando: usuario get");
        app.processEmailCommand("test@ejemplo.com", "usuario get", "Lista de usuarios");
        
        System.out.println("\n📝 Simulando comando de registro:");
        app.processEmailCommand("test@ejemplo.com", "registrar Juan Pérez 123456789 masculino", "Quiero registrarme");
        
        System.out.println("\n✅ Test completado. Los emails de respuesta fueron enviados.");
    }
    
    private static void mostrarInformacionSistema() {
        System.out.println("\n📊 INFORMACIÓN DEL SISTEMA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        System.out.println("\n🔧 CONFIGURACIÓN ACTUAL:");
        try {
            Class<?> dbClass = Class.forName("postgresConecction.DBConnection");
            String database = (String) dbClass.getField("database").get(null);
            String server = (String) dbClass.getField("server").get(null);
            String port = (String) dbClass.getField("port").get(null);
            String user = (String) dbClass.getField("user").get(null);
            
            System.out.println("📊 Base de datos: " + database);
            System.out.println("🖥️ Servidor: " + server + ":" + port);
            System.out.println("👤 Usuario: " + user);
        } catch (Exception e) {
            System.out.println("❌ No se pudo obtener configuración de BD");
        }
        
        System.out.println("📧 Gmail: JairoJairoJairo@gmail.com");
        System.out.println();
        
        System.out.println("📝 COMANDOS IMPLEMENTADOS:");
        System.out.println("   ✅ registrar [nombre] [apellido] [teléfono] [género]");
        System.out.println("   ✅ usuario get");
        System.out.println("   ✅ usuario get [id]");
        System.out.println("   ✅ producto get");
        System.out.println("   ✅ producto get [id]");
        System.out.println("   ✅ categoria get");
        System.out.println("   ✅ categoria get [id]");
        System.out.println("   ✅ cliente get");
        System.out.println("   ✅ cliente get [id]");
        System.out.println("   ✅ tipo_pago get");
        System.out.println("   ✅ tipo_pago get [id]");
        System.out.println("   ✅ help");
        System.out.println();
        
        System.out.println("🚫 COMANDOS NO IMPLEMENTADOS:");
        System.out.println("   ❌ evento, reserva, pago, proveedor, promocion");
        System.out.println("   ❌ patrocinador, patrocinio, rol, servicio");
        System.out.println("   💡 Responden con mensaje de 'no disponible'");
        System.out.println();
        
        System.out.println("💡 CARACTERÍSTICAS PRINCIPALES:");
        System.out.println("   🤖 Monitor IMAP en tiempo real");
        System.out.println("   📧 Respuestas HTML modernas");
        System.out.println("   ✅ Validación completa con sugerencias");
        System.out.println("   🔍 Detección de comandos en asunto Y contenido");
        System.out.println("   🗄️ Integración con PostgreSQL");
        System.out.println("   📝 Registro automático de email del remitente");
        System.out.println();
        
        System.out.println("📖 CÓMO USAR:");
        System.out.println("   1. Ejecuta el Monitor Gmail (opción 1)");
        System.out.println("   2. Envía email a: JairoJairoJairo@gmail.com");
        System.out.println("   3. Pon el comando en el asunto O en el contenido");
        System.out.println("   4. El sistema responde automáticamente");
        System.out.println("   5. Puedes responder al email con nuevos comandos");
    }
    
    private static void configuracionAvanzada() {
        System.out.println("\n🔧 CONFIGURACIÓN AVANZADA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println();
        System.out.println("🔍 Esta sección está en desarrollo.");
        System.out.println("📝 Funcionalidades futuras:");
        System.out.println("   • Cambiar configuración de base de datos");
        System.out.println("   • Verificar conexiones IMAP/SMTP");
        System.out.println("   • Configurar credenciales de Gmail");
        System.out.println("   • Cambiar configuración de logging");
        System.out.println();
        System.out.println("💡 Por ahora, modifica manualmente:");
        System.out.println("   📁 postgresConecction/DBConnection.java");
        System.out.println("   📁 servidor/GmailMonitorComandos.java");
    }
} 