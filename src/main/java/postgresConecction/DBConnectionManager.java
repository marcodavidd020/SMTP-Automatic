package postgresConecction;

/**
 * Gestor de configuración global de base de datos
 * Permite cambiar fácilmente entre configuración local y remota (Tecnoweb)
 * 
 * @author MARCO
 */
public class DBConnectionManager {
    
    /**
     * Enum para los tipos de configuración disponibles
     */
    public enum ConfigType {
        LOCAL,      // Base de datos local (127.0.0.1)
        TECNOWEB    // Base de datos remota de Tecnoweb
    }
    
    // Configuración activa (por defecto LOCAL para compatibilidad)
    private static ConfigType activeConfig = ConfigType.LOCAL;
    
    /**
     * Cambia la configuración global de base de datos
     * @param configType Tipo de configuración a usar
     */
    public static void setActiveConfig(ConfigType configType) {
        activeConfig = configType;
        System.out.println("🔧 Configuración de BD cambiada a: " + configType);
        printCurrentConfig();
    }
    
    /**
     * Obtiene la configuración activa
     */
    public static ConfigType getActiveConfig() {
        return activeConfig;
    }
    
    /**
     * Obtiene la base de datos según la configuración activa
     */
    public static String getDatabase() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.database : DBConnection.database;
    }
    
    /**
     * Obtiene el servidor según la configuración activa
     */
    public static String getServer() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.server : DBConnection.server;
    }
    
    /**
     * Obtiene el puerto según la configuración activa
     */
    public static String getPort() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.port : DBConnection.port;
    }
    
    /**
     * Obtiene el usuario según la configuración activa
     */
    public static String getUser() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.user : DBConnection.user;
    }
    
    /**
     * Obtiene la contraseña según la configuración activa
     */
    public static String getPassword() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.password : DBConnection.password;
    }
    
    /**
     * Obtiene la URL completa según la configuración activa
     */
    public static String getUrl() {
        return activeConfig == ConfigType.TECNOWEB ? 
            DBConnection.TecnoWeb.url : DBConnection.url;
    }
    
    /**
     * Crea una nueva instancia de SqlConnection con la configuración activa
     */
    public static SqlConnection createConnection() {
        return new SqlConnection(getDatabase(), getServer(), getPort(), getUser(), getPassword());
    }
    
    /**
     * Muestra la configuración actual
     */
    public static void printCurrentConfig() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🗄️ CONFIGURACIÓN DE BASE DE DATOS ACTIVA");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📊 Tipo: " + activeConfig);
        System.out.println("🗄️ Base de datos: " + getDatabase());
        System.out.println("🖥️ Servidor: " + getServer() + ":" + getPort());
        System.out.println("👤 Usuario: " + getUser());
        System.out.println("🔗 URL: " + getUrl());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    /**
     * Fuerza el uso de configuración de Tecnoweb para Gmail Server
     */
    public static void forceGmailToTecnoweb() {
        System.out.println("🔄 Forzando Gmail Server a usar Tecnoweb...");
        setActiveConfig(ConfigType.TECNOWEB);
    }
    
    /**
     * Fuerza el uso de configuración local 
     */
    public static void forceLocal() {
        System.out.println("🔄 Forzando uso de base de datos local...");
        setActiveConfig(ConfigType.LOCAL);
    }
    
    /**
     * Verifica si la configuración actual es Tecnoweb
     */
    public static boolean isTecnoweb() {
        return activeConfig == ConfigType.TECNOWEB;
    }
    
    /**
     * Verifica si la configuración actual es local
     */
    public static boolean isLocal() {
        return activeConfig == ConfigType.LOCAL;
    }
} 