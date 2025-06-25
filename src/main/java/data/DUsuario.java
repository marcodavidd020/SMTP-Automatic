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
import librerias.PasswordHelper;

public class DUsuario {

    public static final String[] HEADERS = {"id", "nombre", "email", "created_at"};

    private final SqlConnection connection;

    // Constructor con configuración global del manager
    public DUsuario() {
        this.connection = new SqlConnection(
            DBConnectionManager.getDatabase(), 
            DBConnectionManager.getServer(), 
            DBConnectionManager.getPort(), 
            DBConnectionManager.getUser(), 
            DBConnectionManager.getPassword()
        );
    }
    
    // Constructor con configuración personalizada (NUEVO para Tecnoweb)
    public DUsuario(String database, String server, String port, String user, String password) {
        this.connection = new SqlConnection(database, server, port, user, password);
    }
    
    // Constructor que usa configuración global del manager (NUEVO)
    public static DUsuario createWithGlobalConfig() {
        return new DUsuario(
            DBConnectionManager.getDatabase(),
            DBConnectionManager.getServer(), 
            DBConnectionManager.getPort(),
            DBConnectionManager.getUser(),
            DBConnectionManager.getPassword()
        );
    }

    public List<String[]> get(int id) throws SQLException {
        List<String[]> usuario = new ArrayList<>();
        String query = "SELECT * FROM user WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                usuario.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("email"),
                        set.getString("created_at")
                });
            } else {
                throw new SQLException("Usuario no encontrado.");
            }
        } catch (SQLException e) {
            System.err.println("Error de conexión o consulta SQL: " + e.getMessage());
            throw e;
        }
        return usuario;
    }

    public List<String[]> save(String nombre, String apellido, String telefono, String genero, String email, String password, int rol_id) throws SQLException {
        // ✅ INTEGRACIÓN CON LARAVEL: Usar tabla 'user' (singular) siguiendo migraciones Laravel
        String query = "INSERT INTO user (nombre, celular, email, genero, password, estado, created_at, updated_at) VALUES (?, ?, ?, ?, ?, 'activo', NOW(), NOW()) RETURNING id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre + " " + apellido);  // Laravel usa campo 'nombre' completo
            ps.setString(2, telefono);                // Laravel usa 'celular' 
            ps.setString(3, email);
            ps.setString(4, genero);                  // Laravel: enum('masculino', 'femenino', 'otro')
            ps.setString(5, PasswordHelper.hashPassword(password));  // 🔒 ENCRIPTAR CONTRASEÑA

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return get(id);
            } else {
                throw new SQLException("Error al insertar usuario. No se pudo recuperar el ID del usuario.");
            }
        }
    }

    public List<String[]> update(int id, String nombre, String apellido, String telefono, String genero, String email) throws SQLException {
        String query = "UPDATE user SET nombre = ?, celular = ?, email = ?, genero = ?, updated_at = NOW() WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, nombre + " " + apellido);
            ps.setString(2, telefono);
            ps.setString(3, email);
            ps.setString(4, genero);
            ps.setInt(5, id);

            if(ps.executeUpdate() == 0) {
                System.err.println("Error al modificar el usuario");
                throw new SQLException("Usuario no encontrado para actualizar");
            }
            return get(id);
        }
    }

    public List<String[]> delete(int id) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, id);
            if(ps.executeUpdate() == 0) {
                System.err.println("Error al eliminar usuario");
                throw new SQLException("Usuario no encontrado para eliminar");
            }
            return list();
        }
    }

    public List<String[]> list() throws SQLException {
        List<String[]> usuarios = new ArrayList<>();
        String query = "SELECT * FROM user ORDER BY id";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ResultSet set = ps.executeQuery();
            while (set.next()) {
                usuarios.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("email"),
                        set.getString("created_at")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error listando usuarios: " + e.getMessage());
            throw e;
        }
        
        System.out.println("👥 Total usuarios encontrados: " + usuarios.size());
        return usuarios;
    }

    public void disconnect() {
        connection.closeConnection();
    }

    /**
     * Verifica si un usuario existe por email
     */
    public boolean existsByEmail(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM \"user\" WHERE \"email\" = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error verificando email: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Obtiene un usuario por email
     */
    public List<String[]> getByEmail(String email) throws SQLException {
        List<String[]> usuario = new ArrayList<>();
        String query = "SELECT * FROM \"user\" WHERE \"email\" = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                usuario.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("email"),
                        set.getString("created_at")
                });
            } else {
                throw new SQLException("Usuario no encontrado con email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario por email: " + e.getMessage());
            throw e;
        }
        return usuario;
    }

    /**
     * ✅ REGISTRO COMPLETO PARA LARAVEL: usuario + cliente + rol
     * Registra un nuevo usuario y crea automáticamente un cliente asociado
     * siguiendo la estructura de migraciones de Laravel
     */
    public List<String[]> register(String nombre, String apellido, String telefono, String genero, String email) throws SQLException {
        // Verificar si el email ya existe
        if (existsByEmail(email)) {
            throw new SQLException("El email ya está registrado: " + email);
        }

        String fullName = nombre + " " + apellido;
        String defaultPassword = PasswordHelper.generateTemporaryPassword(); // Contraseña temporal segura

        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false); // Iniciar transacción

            // ✅ 1. CREAR USUARIO (tabla 'user' de Laravel)
            String userQuery = "INSERT INTO \"user\" (\"nombre\", \"celular\", \"email\", \"genero\", \"password\", \"estado\", \"created_at\", \"updated_at\") VALUES (?, ?, ?, ?, ?, 'activo', NOW(), NOW()) RETURNING \"id\"";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userQuery)) {
                ps.setString(1, fullName);
                ps.setString(2, telefono);    // Laravel usa 'celular'
                ps.setString(3, email);
                ps.setString(4, genero);      // Laravel: enum('masculino', 'femenino', 'otro')
                ps.setString(5, PasswordHelper.hashPassword(defaultPassword));  // 🔒 ENCRIPTAR CONTRASEÑA

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    System.out.println("✅ Usuario registrado exitosamente: " + email + " (ID: " + userId + ")");
                } else {
                    throw new SQLException("Error al registrar usuario. No se pudo recuperar el ID.");
                }
            }

            // ✅ 2. CREAR CLIENTE ASOCIADO (tabla 'cliente' de Laravel - solo NIT)
            String clientQuery = "INSERT INTO \"cliente\" (\"user_id\", \"nit\", \"created_at\", \"updated_at\") VALUES (?, ?, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(clientQuery)) {
                ps.setInt(1, userId);
                ps.setString(2, "AUTO-" + userId); // NIT automático

                int clienteResult = ps.executeUpdate();
                if (clienteResult > 0) {
                    System.out.println("✅ Cliente creado automáticamente para usuario ID: " + userId);
                } else {
                    throw new SQLException("Error al crear cliente asociado.");
                }
            }

            // ✅ 3. ASIGNAR ROL DE CLIENTE (user_rol) - USAR ROL_ID = 2 DIRECTAMENTE
            String userRolQuery = "INSERT INTO \"user_rol\" (\"user_id\", \"rol_id\", \"created_at\", \"updated_at\") VALUES (?, ?, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(userRolQuery)) {
                ps.setInt(1, userId);
                ps.setInt(2, 2); // ✅ ROL_ID = 2 para cliente
                
                int userRolResult = ps.executeUpdate();
                if (userRolResult > 0) {
                    System.out.println("✅ Rol cliente (ID: 2) asignado al usuario ID: " + userId);
                } else {
                    System.out.println("⚠️ No se pudo asignar rol cliente");
                }
            } catch (SQLException roleEx) {
                System.err.println("⚠️ Error asignando rol cliente: " + roleEx.getMessage());
                // No fallar el registro por esto
            }

            conn.commit(); // Confirmar transacción
            
            // Retornar información del usuario con contraseña temporal
            List<String[]> resultado = new ArrayList<>();
            resultado.add(new String[]{
                String.valueOf(userId),
                fullName,
                email,
                "CONTRASEÑA_TEMPORAL: " + defaultPassword // Mostrar contraseña temporal
            });
            return resultado;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir transacción en caso de error
                } catch (SQLException rollbackEx) {
                    System.err.println("Error en rollback: " + rollbackEx.getMessage());
                }
            }
            System.err.println("❌ Error registrando usuario: " + e.getMessage());
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeEx) {
                    System.err.println("Error cerrando conexión: " + closeEx.getMessage());
                }
            }
        }
    }

    /**
     * Obtiene usuario por email incluyendo información del rol
     * para validaciones de permisos
     */
    public List<String[]> getByEmailWithRole(String email) throws SQLException {
        List<String[]> usuario = new ArrayList<>();
        String query = "SELECT u.\"id\", u.\"nombre\", u.\"email\", r.\"id\" as rol_id, r.\"nombre\" as rol_nombre " +
                      "FROM \"user\" u " +
                      "LEFT JOIN \"user_rol\" ur ON u.\"id\" = ur.\"user_id\" " +
                      "LEFT JOIN \"rol\" r ON ur.\"rol_id\" = r.\"id\" " +
                      "WHERE u.\"email\" = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet set = ps.executeQuery();
            if (set.next()) {
                usuario.add(new String[]{
                        String.valueOf(set.getInt("id")),
                        set.getString("nombre"),
                        set.getString("email"),
                        String.valueOf(set.getInt("rol_id")),
                        set.getString("rol_nombre")
                });
            } else {
                throw new SQLException("Usuario no encontrado con email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("Error obteniendo usuario con rol por email: " + e.getMessage());
            throw e;
        }
        return usuario;
    }

    /**
     * Verifica si un usuario tiene un rol específico
     */
    public boolean tieneRol(String email, String nombreRol) throws SQLException {
        String query = "SELECT COUNT(*) " +
                      "FROM \"user\" u " +
                      "JOIN \"user_rol\" ur ON u.\"id\" = ur.\"user_id\" " +
                      "JOIN \"rol\" r ON ur.\"rol_id\" = r.\"id\" " +
                      "WHERE u.\"email\" = ? AND r.\"nombre\" = ?";
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, nombreRol);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error verificando rol: " + e.getMessage());
            throw e;
        }
        return false;
    }
}
