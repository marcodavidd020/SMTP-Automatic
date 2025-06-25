package negocio;

import java.sql.SQLException;
import java.util.List;
import data.DCategoria;

public class NCategoria {
    private DCategoria dCategoria;

    public NCategoria() {
        this.dCategoria = new DCategoria();
    }

    public List<String[]> get(int id) throws SQLException {
        return dCategoria.get(id);
    }

    public List<String[]> list() throws SQLException {
        return dCategoria.list();
    }
} 