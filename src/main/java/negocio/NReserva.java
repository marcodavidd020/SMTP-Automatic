package negocio;

import data.DReserva;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NReserva {

    private DReserva dReserva;

    public NReserva() {
        this.dReserva = new DReserva();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dReserva.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dReserva.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException, ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Asegúrate de usar el formato correcto

        Date fechaReserva = new Date(dateFormat.parse(parametros.get(1)).getTime()); // Convierte la fecha

        return dReserva.save(
                parametros.get(0), // codigo
                fechaReserva, // fecha ya parseada
                Float.parseFloat(parametros.get(2)), // costo_entrada
                Integer.parseInt(parametros.get(3)), // cantidad
                Float.parseFloat(parametros.get(4)), // costo_total
                parametros.get(5), // estado
                Integer.parseInt(parametros.get(6)), // usuario_id
                Integer.parseInt(parametros.get(7))  // evento_id
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dReserva.update(
//                Integer.parseInt(parametros.get(0)), // id
//                parametros.get(1)  // estado
//        );
//        dReserva.disconnect();
        return dReserva.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1)  // estado
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dReserva.delete(Integer.parseInt(parametros.get(0)));
//        dReserva.disconnect();
        return dReserva.delete(Integer.parseInt(parametros.get(0)));
    }
}
