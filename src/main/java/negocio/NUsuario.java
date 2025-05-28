package negocio;

import data.DUsuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NUsuario {

    private DUsuario dUsuario;

    public NUsuario() {
        this.dUsuario = new DUsuario();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dUsuario.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dUsuario.get(id);
    }

    public  List<String[]> save(List<String> parametros) throws SQLException {
//        dUsuario.save(
//                parametros.get(0), // nombre
//                parametros.get(1), // apellido
//                parametros.get(2), // telefono
//                parametros.get(3), // genero
//                parametros.get(4), // email
//                parametros.get(5), // password
//                Integer.valueOf(parametros.get(6)) // rol_id
//        );
//        dUsuario.disconnect();
        return dUsuario.save(
                parametros.get(0), // nombre
                parametros.get(1), // apellido
                parametros.get(2), // telefono
                parametros.get(3), // genero
                parametros.get(4), // email
                parametros.get(5), // password
                Integer.valueOf(parametros.get(6)) // rol_id
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dUsuario.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1), // nombre
//                parametros.get(2)  // apellido
//        );
//        dUsuario.disconnect();
        return dUsuario.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // nombre
                parametros.get(2), // apellido
                parametros.get(3), // telefono
                parametros.get(4), // genero
                parametros.get(5) // email
        );
    }

    public  List<String[]> delete(List<String> parametros) throws SQLException {
//        dUsuario.delete(Integer.parseInt(parametros.get(0)));
//        dUsuario.disconnect();
        return dUsuario.delete(Integer.parseInt(parametros.get(0)));
    }
}
