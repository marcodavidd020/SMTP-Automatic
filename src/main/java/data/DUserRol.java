package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.DBConnectionManager;
import postgresConecction.SqlConnection;

public class DUserRol {

    public static final String[] HEADERS = {"id", "user_id", "rol_id", "nombre_usuario", "email_usuario", "nombre_rol"};

    private final SqlConnection connection;

    public DUserRol() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    private DUserRol(SqlConnection customConnection) {
        this.connection = customConnection;
    }
    
    public static DUserRol createWithGlobalConfig() {
        return new DUserRol(DBConnectionManager.createConnection());
    }

    /**
     * Obtiene una asignación de rol específica
     */
    public List<String[]> get(int id) throws SQLException {
        List<String[]> userRol = new ArrayList<>();
        String query = "SELECT ur.*, u.nombre as nombre_usuario, u.email as email_usuario, r.nombre as nombre_rol " +
                      "FROM user_rol ur " +
                      "LEFT JOIN user u ON ur.user_id = u.id " +
                      "LEFT JOIN rol r ON ur.rol_id = r.id " +
                      "WHERE ur.id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                userRol.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        String.valueOf(set.getInt("rol_id")),
                        set.getString("nombre_usuario"),
                        set.getString("email_usuario"),
                        set.getString("nombre_rol")
                });
            } else {
                throw new SQLException("Asignación de rol no encontrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo user_rol: " + e.getMessage());
            throw e;
        }
        return userRol;
    }

    /**
     * Asigna un rol a un usuario
     */
    public List<String[]> save(int userId, int rolId) throws SQLException {
        // Verificar que no exista la asignación
        if (existeAsignacion(userId, rolId)) {
            throw new SQLException("El usuario ya tiene asignado este rol.");
        }

        String query = "INSERT INTO user_rol (user_id, rol_id, created_at, updated_at) VALUES (?, ?, NOW(), NOW()) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, rolId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                System.out.println("✅ Rol asignado: Usuario " + userId + " -> Rol " + rolId);
                return get(id);
            } else {
                throw new SQLException("Error al asignar rol. No se pudo recuperar el ID.");
            }
        }
    }

    /**
     * Elimina una asignación de rol
     */
    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM user_rol WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                System.err.println("Error al eliminar asignación de rol");
                throw new SQLException("Asignación de rol no encontrada para eliminar");
            }
            System.out.println("✅ Asignación de rol eliminada: ID " + id);
            return list();
        }
    }

    /**
     * Lista todas las asignaciones de roles
     */
    public List<String[]> list() throws SQLException {
        List<String[]> userRoles = new ArrayList<>();
        String query = "SELECT ur.*, u.nombre as nombre_usuario, u.email as email_usuario, r.nombre as nombre_rol " +
                      "FROM user_rol ur " +
                      "LEFT JOIN user u ON ur.user_id = u.id " +
                      "LEFT JOIN rol r ON ur.rol_id = r.id " +
                      "ORDER BY ur.id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                userRoles.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        String.valueOf(set.getInt("rol_id")),
                        set.getString("nombre_usuario"),
                        set.getString("email_usuario"),
                        set.getString("nombre_rol")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando user_rol: " + e.getMessage());
            throw e;
        }
        
        System.out.println("👥 Total asignaciones de rol: " + userRoles.size());
        return userRoles;
    }

    /**
     * Obtiene todos los roles de un usuario específico
     */
    public List<String[]> getRolesByUserId(int userId) throws SQLException {
        List<String[]> roles = new ArrayList<>();
        String query = "SELECT ur.*, r.nombre as nombre_rol " +
                      "FROM user_rol ur " +
                      "JOIN rol r ON ur.rol_id = r.id " +
                      "WHERE ur.user_id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                roles.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        String.valueOf(set.getInt("rol_id")),
                        "", // nombre_usuario (no necesario aquí)
                        "", // email_usuario (no necesario aquí)
                        set.getString("nombre_rol")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo roles del usuario: " + e.getMessage());
            throw e;
        }
        return roles;
    }

    /**
     * Obtiene todos los usuarios con un rol específico
     */
    public List<String[]> getUsersByRolId(int rolId) throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT ur.*, u.nombre as nombre_usuario, u.email as email_usuario " +
                      "FROM user_rol ur " +
                      "JOIN user u ON ur.user_id = u.id " +
                      "WHERE ur.rol_id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, rolId);
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                usuarios.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        String.valueOf(set.getInt("user_id")),
                        String.valueOf(set.getInt("rol_id")),
                        set.getString("nombre_usuario"),
                        set.getString("email_usuario"),
                        "" // nombre_rol (no necesario aquí)
                });
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuarios por rol: " + e.getMessage());
            throw e;
        }
        return usuarios;
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean usuarioTieneRol(int userId, String nombreRol) throws SQLException {
        String query = "SELECT COUNT(*) " +
                      "FROM user_rol ur " +
                      "JOIN rol r ON ur.rol_id = r.id " +
                      "WHERE ur.user_id = ? AND r.nombre = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setString(2, nombreRol);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando rol de usuario: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Verifica si existe una asignación específica
     */
    private boolean existeAsignacion(int userId, int rolId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_rol WHERE user_id = ? AND rol_id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.setInt(2, rolId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando asignación: " + e.getMessage());
            throw e;
        }
        return false;
    }

    /**
     * Comando especial register@/migrations: Asigna rol cliente automáticamente
     */
    public List<String[]> registerClienteAutomatic(int userId) throws SQLException {
        // Buscar rol cliente
        String getRolQuery = "SELECT id FROM rol WHERE nombre = 'cliente' LIMIT 1";
        int rolClienteId = 0;
        
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(getRolQuery)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                rolClienteId = rs.getInt("id");
            } else {
                throw new SQLException("Rol 'cliente' no encontrado en la base de datos");
            }
        }

        // Asignar rol cliente si no lo tiene
        if (!existeAsignacion(userId, rolClienteId)) {
            return save(userId, rolClienteId);
        } else {
            System.out.println("ℹ️ Usuario " + userId + " ya tiene rol de cliente asignado");
            return getRolesByUserId(userId);
        }
    }

    public void disconnect() {
        connection.closeConnection();
    }
} 