package interfaces;

import librerias.ParamsAction;

/**
 * Interfaz ICasoUsoListener define los métodos para los casos de uso soportados
 * por la aplicación en base a las tablas del modelo de dominio.
 */
public interface ICasoUsoListener {
    void usuario(ParamsAction event);
    void pago(ParamsAction event);
    void proveedor(ParamsAction event);
    void patrocinador(ParamsAction event);
    void patrocinio(ParamsAction event);
    void rol(ParamsAction event);
    void servicio(ParamsAction event);
    void error(ParamsAction event); // Método general para manejar errores
    void help(ParamsAction event); // Método para mostrar ayuda
    
    // ✅ NUEVOS MÉTODOS PARA INTEGRACIÓN CON ANALEX
    void producto(ParamsAction event);
    void categoria(ParamsAction event);
    void cliente(ParamsAction event);
    void tipo_pago(ParamsAction event);
}
