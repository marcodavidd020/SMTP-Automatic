package negocio;

import data.DServicio;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NServicio {

    private DServicio dServicio;

    public NServicio() {
        this.dServicio = new DServicio();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dServicio.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dServicio.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dServicio.save(
//                parametros.get(0), // nombre
//                parametros.get(1)  // descripcion
//        );
//        dServicio.disconnect();
        return dServicio.save(
                parametros.get(0), // nombre
                parametros.get(1)  // descripcion
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dServicio.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // nombre
//        );
//        dServicio.disconnect();
        return dServicio.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1)  // nombre
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dServicio.delete(Integer.parseInt(parametros.get(0)));
//        dServicio.disconnect();
        return dServicio.delete(Integer.parseInt(parametros.get(0)));
    }
}
