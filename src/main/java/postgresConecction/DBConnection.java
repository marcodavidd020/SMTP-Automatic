package postgresConecction;

public class DBConnection {
    // 🔧 CONFIGURACIÓN DE BASE DE DATOS LOCAL
    public static String database = "EcommerceTool";
    public static String server = "127.0.0.1";
    public static String port = "5432";
    public static String user = "postgres";
    public static String password = "postgres";
    public static String url = "jdbc:postgresql://" + server + ":" + port + "/" + database;

    // 🔧 CONFIGURACIÓN TECNOWEB (para compatibilidad)
    public static class TecnoWeb {
        public static String database = "db_grupo21sc";
        public static String server = "mail.tecnoweb.org.bo";
        public static String port = "5432";
        public static String user = "grupo21sc";
        public static String password = "grup021grup021*";
        public static String url = "jdbc:postgresql://" + server + ":" + port + "/" + database;
    }
}
