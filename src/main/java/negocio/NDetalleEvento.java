package negocio;

import data.DDetalleEvento;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NDetalleEvento {

    private DDetalleEvento dDetalleEvento;

    public NDetalleEvento() {
        this.dDetalleEvento = new DDetalleEvento();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dDetalleEvento.list();
    }

    public List<String[]> get(int evento_id, int servicio_id) throws SQLException {
        return dDetalleEvento.get(evento_id, servicio_id);
    }

    public List<String[]> save(List<String> parametros) throws SQLException {
//        dDetalleEvento.save(
//                Integer.parseInt(parametros.get(0)), // evento_id
//                Integer.parseInt(parametros.get(1)), // servicio_id
//                Float.parseFloat(parametros.get(2))  // costo_servicio
//        );
//        dDetalleEvento.disconnect();
        return dDetalleEvento.save(
                Integer.parseInt(parametros.get(0)), // evento_id
                Integer.parseInt(parametros.get(1)), // servicio_id
                Float.parseFloat(parametros.get(2))  // costo_servicio
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
//        dDetalleEvento.update(
//                Integer.parseInt(parametros.get(0)), // evento_id
//                Integer.parseInt(parametros.get(1)), // servicio_id
//                Float.parseFloat(parametros.get(2))  // costo_servicio
//        );
//        dDetalleEvento.disconnect();
        return dDetalleEvento.update(
                Integer.parseInt(parametros.get(0)), // evento_id
                Integer.parseInt(parametros.get(1)), // servicio_id
                Float.parseFloat(parametros.get(2))  // costo_servicio
        );
    }

    public List<String[]> delete(List<String> parametros) throws SQLException {
//        dDetalleEvento.delete(
//                Integer.parseInt(parametros.get(0)), // evento_id
//                Integer.parseInt(parametros.get(1))  // servicio_id
//        );
//        dDetalleEvento.disconnect();
        return dDetalleEvento.delete(
                Integer.parseInt(parametros.get(0)), // evento_id
                Integer.parseInt(parametros.get(1))  // servicio_id
        );
    }
}
