package interfaces;

import librerias.ParamsAction;

/**
 * Interfaz ICasoUsoListener define los métodos para los casos de uso soportados
 * por la aplicación en base a las tablas del modelo de dominio.
 */
public interface ICasoUsoListener {
    void usuario(ParamsAction event);
    void evento(ParamsAction event);
    void reserva(ParamsAction event);
    void pago(ParamsAction event);
    void proveedor(ParamsAction event);
    void promocion(ParamsAction event);
    void patrocinador(ParamsAction event);
    void patrocinio(ParamsAction event);
    void rol(ParamsAction event);
    void servicio(ParamsAction event);
    void detalleEvento(ParamsAction event);
    void error(ParamsAction event); // Método general para manejar errores
    void help(ParamsAction event); // Método para mostrar ayuda
}
