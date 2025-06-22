package negocio;

import data.DProveedor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NProveedor {

    private DProveedor dProveedor;

    public NProveedor() {
        this.dProveedor = new DProveedor();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dProveedor.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dProveedor.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dProveedor.save(
//                parametros.get(0), // nombre
//                parametros.get(1), // telefono
//                parametros.get(2), // email
//                parametros.get(3)  // direccion
//        );
//        dProveedor.disconnect();
        return dProveedor.save(
                parametros.get(0), // nombre
                parametros.get(1), // telefono
                parametros.get(2), // email
                parametros.get(3)  // direccion
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dProveedor.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // nombre
//        );
//        dProveedor.disconnect();
        return dProveedor.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // nombre
                parametros.get(2), // telefono
                parametros.get(3), // email
                parametros.get(4)  // direccion
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dProveedor.delete(Integer.parseInt(parametros.get(0)));
//        dProveedor.disconnect();
        return dProveedor.delete(Integer.parseInt(parametros.get(0)));
    }
}
