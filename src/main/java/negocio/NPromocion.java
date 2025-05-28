package negocio;

import data.DPromocion;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NPromocion {

    private DPromocion dPromocion;

    public NPromocion() {
        this.dPromocion = new DPromocion();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dPromocion.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dPromocion.get(id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException, ParseException {
//        dPromocion.save(
//                parametros.get(0), // descripcion
//                Integer.parseInt(parametros.get(1)), // descuento
//                java.sql.Date.valueOf(parametros.get(2)), // fecha_inicio
//                java.sql.Date.valueOf(parametros.get(3)), // fecha_fin
//                Integer.parseInt(parametros.get(4))  // proveedor_id
//        );
//        dPromocion.disconnect();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Asegúrate de usar el formato correcto

        java.sql.Date fechaInicio = new java.sql.Date(dateFormat.parse(parametros.get(2)).getTime()); // Convierte la fecha
        java.sql.Date fechaFin = new java.sql.Date(dateFormat.parse(parametros.get(3)).getTime()); // Convierte la fecha

        return dPromocion.save(
                parametros.get(0), // descripcion
                Integer.parseInt(parametros.get(1)), // descuento
                fechaInicio, // fecha_inicio
                fechaFin, // fecha_fin
                Integer.parseInt(parametros.get(4))  // proveedor_id
        );

    }

    public List<String[]>  update(List<String> parametros) throws SQLException {
//        dPromocion.update(
//                Integer.parseInt(parametros.get(0)), // id
//                Integer.parseInt(parametros.get(1))  // descuento
//        );
//        dPromocion.disconnect();
        return dPromocion.update(
                Integer.parseInt(parametros.get(0)), // id
                Integer.parseInt(parametros.get(1)) // descuento
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dPromocion.delete(Integer.parseInt(parametros.get(0)));
//        dPromocion.disconnect();
        return dPromocion.delete(Integer.parseInt(parametros.get(0)));
    }
}
