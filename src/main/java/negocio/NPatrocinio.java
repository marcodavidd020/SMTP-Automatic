package negocio;

import data.DPatrocinio;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NPatrocinio {

    private DPatrocinio dPatrocinio;

    public NPatrocinio() {
        this.dPatrocinio = new DPatrocinio();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dPatrocinio.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dPatrocinio.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dPatrocinio.save(
//                Float.valueOf(parametros.get(0)), // aporte
//                Integer.parseInt(parametros.get(1)), // patrocinador_id
//                Integer.parseInt(parametros.get(2))  // evento_id
//        );
//        dPatrocinio.disconnect();
        return dPatrocinio.save(
                Float.parseFloat(parametros.get(0)), // aporte
                Integer.parseInt(parametros.get(1)), // patrocinador_id
                Integer.parseInt(parametros.get(2))  // evento_id
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dPatrocinio.update(
//                Integer.parseInt(parametros.get(0)), // id
//                Float.valueOf(parametros.get(1))  // aporte
//        );
//        dPatrocinio.disconnect();
        return dPatrocinio.update(
                Integer.parseInt(parametros.get(0)), // id
                Float.parseFloat(parametros.get(1))  // aporte
        );
    }

    public  List<String[]> delete(List<String> parametros) throws SQLException {
//        dPatrocinio.delete(Integer.parseInt(parametros.get(0)));
//        dPatrocinio.disconnect();
        return dPatrocinio.delete(Integer.parseInt(parametros.get(0)));
    }
}
