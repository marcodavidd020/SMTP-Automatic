package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;
import librerias.PasswordHelper;

/**
 * Versión optimizada de DUsuario para tecnoweb con manejo eficiente de conexiones
 */
public class DUsuarioOptimizado {

    public static final String[] HEADERS = {"id", "nombre", "email", "created_at"};

    // ✅ POOL DE CONEXIÓN ÚNICA PARA EVITAR LÍMITES
    private static SqlConnection sharedConnection = null;
    
    private static SqlConnection getSharedConnection() {
        if (sharedConnection == null) {
            sharedConnection = new SqlConnection(
                DBConnection.database, 
                DBConnection.server, 
                DBConnection.port, 
                DBConnection.user, 
                DBConnection.password
            );
        }
        return sharedConnection;
    }

    /**
     * ✅ MÉTODO OPTIMIZADO: Verificar email sin mantener conexión abierta
     */
    public static boolean existsByEmail(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM \"user\" WHERE \"email\" = ?";
        SqlConnection connection = getSharedConnection();
        
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("📧 Email " + email + ": " + (exists ? "REGISTRADO" : "NO REGISTRADO"));
                return exists;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error verificando email: " + e.getMessage());
            // Intentar con sintaxis alternativa
            return existsByEmailAlternative(email);
        }
    }
    
    /**
     * ✅ MÉTODO ALTERNATIVO: Sintaxis diferente para problemas de columnas
     */
    private static boolean existsByEmailAlternative(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM public.user WHERE email = ?";
        SqlConnection connection = getSharedConnection();
        
        try (Connection conn = connection.connect();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("📧 Email " + email + " (alt): " + (exists ? "REGISTRADO" : "NO REGISTRADO"));
                return exists;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("❌ Error con sintaxis alternativa: " + e.getMessage());
            throw e;
        }
    }

    /**
     * ✅ REGISTRO OPTIMIZADO PARA TECNOWEB CON ROL_ID = 2
     */
    public static List<String[]> registerWithRoleId2(String nombre, String apellido, String telefono, String genero, String email) throws SQLException {
        // Verificar si el email ya existe
        if (existsByEmail(email)) {
            throw new SQLException("El email ya está registrado: " + email);
        }

        String fullName = nombre + " " + apellido;
        String defaultPassword = PasswordHelper.generateTemporaryPassword(); // Contraseña segura
        SqlConnection connection = getSharedConnection();

        Connection conn = null;
        try {
            conn = connection.connect();
            conn.setAutoCommit(false); // Iniciar transacción

            // ✅ 1. CREAR USUARIO (tabla 'user' con sintaxis segura)
            String userQuery = "INSERT INTO public.user (nombre, celular, email, genero, password, estado, created_at, updated_at) " +
                             "VALUES (?, ?, ?, ?, ?, 'activo', NOW(), NOW()) RETURNING id";
            int userId;
            try (PreparedStatement ps = conn.prepareStatement(userQuery)) {
                ps.setString(1, fullName);
                ps.setString(2, telefono);
                ps.setString(3, email);
                ps.setString(4, genero);
                ps.setString(5, PasswordHelper.hashPassword(defaultPassword));  // 🔒 ENCRIPTAR CONTRASEÑA

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    userId = rs.getInt(1);
                    System.out.println("✅ Usuario registrado exitosamente: " + email + " (ID: " + userId + ")");
                } else {
                    throw new SQLException("Error al registrar usuario. No se pudo recuperar el ID.");
                }
            }

            // ✅ 2. CREAR CLIENTE ASOCIADO (tabla 'cliente')
            String clientQuery = "INSERT INTO public.cliente (user_id, nit, created_at, updated_at) " +
                               "VALUES (?, ?, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(clientQuery)) {
                ps.setInt(1, userId);
                ps.setString(2, "NIT-" + userId); // NIT automático

                int clienteResult = ps.executeUpdate();
                if (clienteResult > 0) {
                    System.out.println("✅ Cliente creado automáticamente para usuario ID: " + userId);
                } else {
                    System.out.println("⚠️ Error al crear cliente asociado");
                }
            } catch (SQLException clientEx) {
                System.err.println("⚠️ Error creando cliente: " + clientEx.getMessage());
                // No fallar el registro por esto
            }

            // ✅ 3. ASIGNAR ROL_ID = 2 (CLIENTE)
            String userRolQuery = "INSERT INTO public.user_rol (user_id, rol_id, created_at, updated_at) " +
                                "VALUES (?, 2, NOW(), NOW())";
            try (PreparedStatement ps = conn.prepareStatement(userRolQuery)) {
                ps.setInt(1, userId);
                
                int userRolResult = ps.executeUpdate();
                if (userRolResult > 0) {
                    System.out.println("✅ Rol cliente (ID: 2) asignado al usuario ID: " + userId);
                } else {
                    System.out.println("⚠️ No se pudo asignar rol cliente");
                }
            } catch (SQLException roleEx) {
                System.err.println("⚠️ Error asignando rol: " + roleEx.getMessage());
                // No fallar el registro por esto
            }

            conn.commit(); // Confirmar transacción
            
            // ✅ 4. RETORNAR DATOS DEL USUARIO REGISTRADO (incluyendo contraseña temporal)
            List<String[]> resultado = new ArrayList<>();
            resultado.add(new String[]{
                String.valueOf(userId),
                fullName,
                email,
                "CONTRASEÑA_TEMPORAL: " + defaultPassword // Mostrar contraseña temporal al usuario
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
     * ✅ OBTENER USUARIO POR EMAIL CON SINTAXIS SEGURA
     */
    public static List<String[]> getByEmail(String email) throws SQLException {
        List<String[]> usuario = new ArrayList<>();
        String query = "SELECT id, nombre, email, created_at FROM public.user WHERE email = ?";
        SqlConnection connection = getSharedConnection();
        
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
     * ✅ LIMPIAR RECURSOS
     */
    public static void cleanup() {
        if (sharedConnection != null) {
            sharedConnection.closeConnection();
            sharedConnection = null;
        }
    }
} 