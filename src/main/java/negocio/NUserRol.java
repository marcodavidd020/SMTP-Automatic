package negocio;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import data.DUserRol;

public class NUserRol {

    private DUserRol dUserRol;

    public NUserRol() {
        this.dUserRol = new DUserRol();
    }
    
    public NUserRol(boolean useGlobalConfig) {
        if (useGlobalConfig) {
            this.dUserRol = DUserRol.createWithGlobalConfig();
        } else {
            this.dUserRol = new DUserRol();
        }
    }

    /**
     * Lista todas las asignaciones de roles
     */
    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dUserRol.list();
    }

    /**
     * Obtiene una asignación específica
     */
    public List<String[]> get(int id) throws SQLException {
        return dUserRol.get(id);
    }

    /**
     * Asigna un rol a un usuario
     * Parámetros: [user_id, rol_id]
     */
    public List<String[]> save(List<String> parametros) throws SQLException {
        if (parametros.size() < 2) {
            throw new SQLException("Faltan parámetros: se requiere [user_id, rol_id]");
        }
        
        int userId = Integer.parseInt(parametros.get(0));
        int rolId = Integer.parseInt(parametros.get(1));
        
        return dUserRol.save(userId, rolId);
    }

    /**
     * Elimina una asignación de rol
     * Parámetros: [id]
     */
    public List<String[]> delete(List<String> parametros) throws SQLException {
        if (parametros.isEmpty()) {
            throw new SQLException("Falta parámetro: se requiere [id]");
        }
        
        int id = Integer.parseInt(parametros.get(0));
        return dUserRol.delete(id);
    }

    /**
     * Obtiene todos los roles de un usuario
     * Parámetros: [user_id]
     */
    public List<String[]> getRolesByUser(List<String> parametros) throws SQLException {
        if (parametros.isEmpty()) {
            throw new SQLException("Falta parámetro: se requiere [user_id]");
        }
        
        int userId = Integer.parseInt(parametros.get(0));
        return dUserRol.getRolesByUserId(userId);
    }

    /**
     * Obtiene todos los usuarios con un rol específico
     * Parámetros: [rol_id]
     */
    public List<String[]> getUsersByRole(List<String> parametros) throws SQLException {
        if (parametros.isEmpty()) {
            throw new SQLException("Falta parámetro: se requiere [rol_id]");
        }
        
        int rolId = Integer.parseInt(parametros.get(0));
        return dUserRol.getUsersByRolId(rolId);
    }

    /**
     * Verifica si un usuario tiene un rol específico
     * Parámetros: [user_id, nombre_rol]
     */
    public boolean verificarRol(List<String> parametros) throws SQLException {
        if (parametros.size() < 2) {
            throw new SQLException("Faltan parámetros: se requiere [user_id, nombre_rol]");
        }
        
        int userId = Integer.parseInt(parametros.get(0));
        String nombreRol = parametros.get(1);
        
        return dUserRol.usuarioTieneRol(userId, nombreRol);
    }

    /**
     * Comando especial register@/migrations: Asigna rol cliente automáticamente
     * Parámetros: [user_id]
     */
    public List<String[]> registerCliente(List<String> parametros) throws SQLException {
        if (parametros.isEmpty()) {
            throw new SQLException("Falta parámetro: se requiere [user_id]");
        }
        
        int userId = Integer.parseInt(parametros.get(0));
        return dUserRol.registerClienteAutomatic(userId);
    }

    /**
     * Sobrecarga del método registerCliente con int directo
     */
    public List<String[]> registerCliente(int userId) throws SQLException {
        return dUserRol.registerClienteAutomatic(userId);
    }

    public void disconnect() {
        if (dUserRol != null) {
            dUserRol.disconnect();
        }
    }
} 