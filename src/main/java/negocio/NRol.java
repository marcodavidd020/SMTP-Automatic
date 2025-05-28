package negocio;

import data.DRol;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NRol {

    private DRol dRol;

    public NRol() {
        this.dRol = new DRol();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dRol.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dRol.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dRol.save(parametros.get(0)); // nombre
//        dRol.disconnect();
        return dRol.save(parametros.get(0));
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dRol.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // nombre
//        );
//        dRol.disconnect();
        return dRol.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1)  // nombre
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dRol.delete(Integer.parseInt(parametros.get(0)));
//        dRol.disconnect();
        return dRol.delete(Integer.parseInt(parametros.get(0)));
    }
}
