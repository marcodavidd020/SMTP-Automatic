package negocio;

import data.DEvento;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NEvento {

    private DEvento dEvento;

    public NEvento() {
        this.dEvento = new DEvento();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dEvento.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dEvento.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Asegúrate de usar el formato correcto
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm"); // Asegúrate de usar el formato correcto

        Date fechaEvento = new Date(dateFormat.parse(parametros.get(4)).getTime()); // Convierte la fecha
        Time horaEvento = new Time(timeFormat.parse(parametros.get(5)).getTime()); // Convierte la hora

        return dEvento.save(
                parametros.get(0), // nombre
                parametros.get(1), // descripcion
                Integer.parseInt(parametros.get(2)), // capacidad
                Float.parseFloat(parametros.get(3)), // precio entrada
                fechaEvento, // fecha ya parseada
                horaEvento, // hora ya parseada
                parametros.get(6), // ubicacion
                parametros.get(7), // estado
                parametros.get(8), // imagen
                Integer.parseInt(parametros.get(9)) // servicio_id
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dEvento.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // estado
//        );
//        dEvento.disconnect();
        return dEvento.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1),  // nombre
                parametros.get(2), // descripcion
                Integer.parseInt(parametros.get(3)), // capacidad
                Float.parseFloat(parametros.get(4)) // precio entrada
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dEvento.delete(Integer.parseInt(parametros.get(0)));
//        dEvento.disconnect();
        return dEvento.delete(Integer.parseInt(parametros.get(0)));
    }
}
