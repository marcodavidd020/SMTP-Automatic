package negocio;

import data.DPago;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NPago {

    private DPago dPago;

    public NPago() {
        this.dPago = new DPago();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dPago.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dPago.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException, ParseException {
//        dPago.save(
//                Float.valueOf(parametros.get(0)), // monto
//                java.sql.Date.valueOf(parametros.get(1)), // fecha
//                parametros.get(2), // metodo_pago
//                Integer.parseInt(parametros.get(3))  // reserva_id
//        );
//        dPago.disconnect();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Asegúrate de usar el formato correcto

        java.sql.Date fecha = new java.sql.Date(dateFormat.parse(parametros.get(1)).getTime()); // Convierte la fecha

        return dPago.save(
                Float.valueOf(parametros.get(0)), // monto
                fecha, // fecha
                parametros.get(2), // metodo_pago
                Integer.parseInt(parametros.get(3))  // reserva_id
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dPago.update(
//                Integer.parseInt(parametros.get(0)), // id
//                Float.valueOf(parametros.get(1))  // monto
//        );
//        dPago.disconnect();
        return dPago.update(
                Integer.parseInt(parametros.get(0)), // id
                Float.valueOf(parametros.get(1))  // monto
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dPago.delete(Integer.parseInt(parametros.get(0)));
//        dPago.disconnect();
        return dPago.delete(Integer.parseInt(parametros.get(0)));
    }
}
