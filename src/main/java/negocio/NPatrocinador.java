package negocio;

import data.DPatrocinador;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NPatrocinador {

    private DPatrocinador dPatrocinador;

    public NPatrocinador() {
        this.dPatrocinador = new DPatrocinador();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dPatrocinador.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dPatrocinador.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dPatrocinador.save(
//                parametros.get(0), // nombre
//                parametros.get(1), // descripcion
//                parametros.get(2), // email
//                parametros.get(3)  // telefono
//        );
//        dPatrocinador.disconnect();
        return dPatrocinador.save(
                parametros.get(0), // nombre
                parametros.get(1), // descripcion
                parametros.get(2), // email
                parametros.get(3)  // telefono
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dPatrocinador.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // nombre
//        );
//        dPatrocinador.disconnect();
        return dPatrocinador.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // nombre
                parametros.get(2), // descripcion
                parametros.get(3), // email
                parametros.get(4)  // telefono
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dPatrocinador.delete(Integer.parseInt(parametros.get(0)));
//        dPatrocinador.disconnect();
        return dPatrocinador.delete(Integer.parseInt(parametros.get(0)));
    }
}
