package postgresConecction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqlConnection {
    private final String database;
    private final String server;
    private final String port;
    private final String user;
    private final String password;

    private Connection connection;

    public SqlConnection(String database, String server, String port, String user, String password) {
        this.database = database;
        this.server = server;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                String url = "jdbc:postgresql://" + server + ":" + port + "/" + database;
                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;  // Considerar lanzar una excepción propia o manejar de otra forma
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
