package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DDireccion {

    public static final String[] HEADERS = {"ID", "Nombre", "Latitud", "Longitud", "Referencia"};

    private final SqlConnection connection;

    public DDireccion() {
        this.connection = new SqlConnection(DBConnectionManager.getDatabase(), DBConnectionManager.getServer(), DBConnectionManager.getPort(), DBConnectionManager.getUser(), DBConnectionManager.getPassword());
    }
    
    private DDireccion(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DDireccion createWithGlobalConfig() {
        return new DDireccion(DBConnectionManager.createConnection());
    }

    /**
     * Extrae coordenadas de un enlace de Google Maps
     * Formato esperado: https://www.google.com/maps/@-17.8513859,-63.1647372,14z...
     */
    public static double[] extraerCoordenadasDeEnlace(String enlaceGoogleMaps) {
        if (enlaceGoogleMaps == null || enlaceGoogleMaps.trim().isEmpty()) {
            return null;
        }

        // Patrón para extraer coordenadas del formato @lat,lng,zoom
        Pattern pattern = Pattern.compile("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+),\\d+");
        Matcher matcher = pattern.matcher(enlaceGoogleMaps);

        if (matcher.find()) {
            try {
                double latitud = Double.parseDouble(matcher.group(1));
                double longitud = Double.parseDouble(matcher.group(2));
                System.out.println("✅ Coordenadas extraídas: " + latitud + ", " + longitud);
                return new double[]{latitud, longitud};
            } catch (NumberFormatException e) {
                System.err.println("❌ Error parsing coordinates: " + e.getMessage());
                return null;
            }
        }

        System.err.println("❌ No se pudieron extraer coordenadas del enlace: " + enlaceGoogleMaps);
        return null;
    }

    public int add(String nombre, String enlaceGoogleMaps, String referencia) throws SQLException {
        // Extraer coordenadas del enlace
        double[] coordenadas = extraerCoordenadasDeEnlace(enlaceGoogleMaps);
        if (coordenadas == null) {
            throw new SQLException("No se pudieron extraer coordenadas válidas del enlace");
        }

        return add(nombre, coordenadas[0], coordenadas[1], referencia);
    }

    public int add(String nombre, double latitud, double longitud, String referencia) throws SQLException {
        String sql = "INSERT INTO \"direccion\" (\"nombre\", \"latitud\", \"longitud\", \"referencia\") VALUES (?, ?, ?, ?) RETURNING \"id\"";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, nombre);
            stmt.setDouble(2, latitud);
            stmt.setDouble(3, longitud);
            stmt.setString(4, referencia);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ Dirección creada con ID: " + id);
                System.out.println("   📍 " + nombre + " (" + latitud + ", " + longitud + ")");
                return id;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error insertando dirección: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT \"id\", \"nombre\", \"latitud\", \"longitud\", \"referencia\" FROM \"direccion\" WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String[] row = {
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("latitud"),
                    rs.getString("longitud"),
                    rs.getString("referencia")
                };
                result.add(row);
            } else {
                throw new SQLException("Dirección no encontrada.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Error obteniendo dirección: " + e.getMessage());
            throw e;
        }
        return result;
    }

    public List<String[]> list() throws SQLException {
        List<String[]> result = new ArrayList<>();
        String sql = "SELECT \"id\", \"nombre\", \"latitud\", \"longitud\", \"referencia\" FROM \"direccion\" ORDER BY \"id\"";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String[] row = {
                    rs.getString("id"),
                    rs.getString("nombre"),
                    rs.getString("latitud"),
                    rs.getString("longitud"),
                    rs.getString("referencia")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error listando direcciones: " + e.getMessage());
            throw e;
        }
        
        System.out.println("📍 Total direcciones encontradas: " + result.size());
        return result;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM \"direccion\" WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                System.out.println("✅ Dirección eliminada: ID " + id);
                return true;
            } else {
                System.err.println("❌ No se encontró dirección con ID: " + id);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error eliminando dirección: " + e.getMessage());
            return false;
        }
    }

    public boolean exists(int id) throws SQLException {
        String sql = "SELECT 1 FROM \"direccion\" WHERE \"id\" = ?";
        
        try (Connection conn = connection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("❌ Error verificando dirección: " + e.getMessage());
            throw e;
        }
    }
} 