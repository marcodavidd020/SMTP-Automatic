package com.mycompany.parcial1.tecnoweb;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.DDetalleEvento;
import data.DEvento;
import data.DPago;
import data.DPatrocinador;
import data.DPatrocinio;
import data.DPromocion;
import data.DProveedor;
import data.DReserva;
import data.DRol;
import data.DServicio;
import data.DUsuario;
import interfaces.ICasoUsoListener;
import interfaces.IEmailListener;
import librerias.Email;
import librerias.HtmlRes;
import librerias.Interpreter;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.NDetalleEvento;
import negocio.NEvento;
import negocio.NPago;
import negocio.NPatrocinador;
import negocio.NPatrocinio;
import negocio.NPromocion;
import negocio.NProveedor;
import negocio.NReserva;
import negocio.NRol;
import negocio.NServicio;
import negocio.NUsuario;
import postgresConecction.EmailReceipt;
import postgresConecction.EmailSend;

public class EmailApp implements ICasoUsoListener, IEmailListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;

    private EmailReceipt emailReceipt;
    private NUsuario nUsuario;
    private NEvento nEvento;
    private NReserva nReserva;
    private NPago nPago;
    private NProveedor nProveedor;
    private NPromocion nPromocion;
    private NPatrocinador nPatrocinador;
    private NPatrocinio nPatrocinio;
    private NRol nRol;
    private NServicio nServicio;
    private NDetalleEvento nDetalleEvento;

    public EmailApp() {
        this.emailReceipt = new EmailReceipt();
        this.emailReceipt.setEmailListener(this);
        this.nUsuario = new NUsuario();
        this.nEvento = new NEvento();
        this.nReserva = new NReserva();
        this.nPago = new NPago();
        this.nProveedor = new NProveedor();
        this.nPromocion = new NPromocion();
        this.nPatrocinador = new NPatrocinador();
        this.nPatrocinio = new NPatrocinio();
        this.nRol = new NRol();
        this.nServicio = new NServicio();
        this.nDetalleEvento = new NDetalleEvento();
    }

    public void start() {
        Thread thread = new Thread(emailReceipt);
        thread.setName("Mail Receipt");
        thread.start();
    }

    // Implementacion para cada caso de uso específico
    @Override
    public void usuario(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nUsuario.save(event.getParams());
                    List<String[]> userDataSaved = nUsuario.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario guardado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de usuario por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> userData = nUsuario.get(id);
                            if (userData != null) {
                                //simpleNotifySuccess(event.getSender(), "Usuario encontrado: " + Arrays.toString(userData));
                                tableNotifySuccess(event.getSender(), "Usuario encontrado", DUsuario.HEADERS, (ArrayList<String[]>) userData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Usuario no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los usuarios
                        tableNotifySuccess(event.getSender(), "Lista de Usuarios", DUsuario.HEADERS, nUsuario.list());
                    }
                    break;
                case Token.MODIFY:
                    // nUsuario.update(event.getParams());
                    List<String[]> userDataUpdated = nUsuario.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario actualizado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nUsuario.delete(event.getParams());
                    List<String[]> userDataDeleted = nUsuario.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario eliminado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }


    @Override
    public void evento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nEvento.save(event.getParams());
                    List<String[]> eventoDataSaved = nEvento.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento guardado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de evento por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> eventoData = nEvento.get(id);
                        if (!eventoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Evento", DEvento.HEADERS, (ArrayList<String[]>) eventoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Evento no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los eventos
                        tableNotifySuccess(event.getSender(), "Lista de Eventos", DEvento.HEADERS, nEvento.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> eventoDataUpdated = nEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento actualizado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nEvento.delete(event.getParams());
                    List<String[]> eventoDataDeleted = nEvento.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Evento eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Evento eliminado correctamente", DEvento.HEADERS, (ArrayList<String[]>) eventoDataDeleted, event.getCommand());
                    break;
            }
        } catch (NumberFormatException | SQLException | IndexOutOfBoundsException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    // Continuación de los métodos dentro de la clase EmailApp
    @Override
    public void reserva(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nReserva.save(event.getParams());
                    List<String[]> reservaDataSaved = nReserva.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva creada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva creada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    // tableNotifySuccess(event.getSender(), "Lista de Reservas", DReserva.HEADERS, nReserva.list());
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de reserva por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> reservaData = nReserva.get(id);
                        if (!reservaData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles de la Reserva", DReserva.HEADERS, (ArrayList<String[]>) reservaData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Reserva no encontrada.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todas las reservas
                        tableNotifySuccess(event.getSender(), "Lista de Reservas", DReserva.HEADERS, nReserva.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> reservaDataUpdated = nReserva.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva actualizada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva actualizada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nReserva.delete(event.getParams());
                    List<String[]> reservaDataDeleted = nReserva.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Reserva eliminada correctamente");
                    tableNotifySuccess(event.getSender(), "Reserva eliminada correctamente", DReserva.HEADERS, (ArrayList<String[]>) reservaDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void pago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nPago.save(event.getParams());
                    List<String[]> pagoDataSaved = nPago.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago procesado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago procesado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de pago por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> pagoData = nPago.get(id);
                        if (!pagoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Pago", DPago.HEADERS, (ArrayList<String[]>) pagoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Pago no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los pagos
                        tableNotifySuccess(event.getSender(), "Lista de Pagos", DPago.HEADERS, nPago.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> pagoDataUpdated = nPago.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago actualizado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nPago.delete(event.getParams());
                    List<String[]> pagoDataDeleted = nPago.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Pago eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Pago eliminado correctamente", DPago.HEADERS, (ArrayList<String[]>) pagoDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void proveedor(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nProveedor.save(event.getParams());
                    List<String[]> proveedorDataSaved = nProveedor.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor agregado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor agregado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de proveedor por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> proveedorData = nProveedor.get(id);
                        if (!proveedorData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Proveedor", DProveedor.HEADERS, (ArrayList<String[]>) proveedorData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Proveedor no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los proveedores
                        tableNotifySuccess(event.getSender(), "Lista de Proveedores", DProveedor.HEADERS, nProveedor.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> proveedorDataUpdated = nProveedor.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor actualizado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nProveedor.delete(event.getParams());
                    List<String[]> proveedorDataDeleted = nProveedor.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Proveedor eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Proveedor eliminado correctamente", DProveedor.HEADERS, (ArrayList<String[]>) proveedorDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void promocion(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nPromocion.save(event.getParams());
                    List<String[]> promocionDataSaved = nPromocion.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Promoción creada correctamente");
                    tableNotifySuccess(event.getSender(), "Promoción creada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de promoción por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> promocionData = nPromocion.get(id);
                        if (!promocionData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles de la Promoción", DPromocion.HEADERS, (ArrayList<String[]>) promocionData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Promoción no encontrada.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todas las promociones
                        tableNotifySuccess(event.getSender(), "Lista de Promociones", DPromocion.HEADERS, nPromocion.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> promocionDataUpdated = nPromocion.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Promoción actualizada correctamente");
                    tableNotifySuccess(event.getSender(), "Promoción actualizada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nPromocion.delete(event.getParams());
                    List<String[]> promocionDataDeleted = nPromocion.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Promoción eliminada correctamente");
                    tableNotifySuccess(event.getSender(), "Promoción eliminada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void patrocinador(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nPatrocinador.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador agregado correctamente");
                    List<String[]> patrocinadorDataSaved = nPatrocinador.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador agregado correctamente: " + Arrays.toString(patrocinadorDataSaved));
                    tableNotifySuccess(event.getSender(), "Patrocinador agregado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataSaved);
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de patrocinador por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> patrocinadorData = nPatrocinador.get(id);
                        if (!patrocinadorData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Patrocinador", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorData);
                        } else {
                            simpleNotify(event.getSender(), "Error", "Patrocinador no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los patrocinadores
                        tableNotifySuccess(event.getSender(), "Lista de Patrocinadores", DPatrocinador.HEADERS, nPatrocinador.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> patrocinadorDataUpdated = nPatrocinador.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador actualizado correctamente: " + Arrays.toString(patrocinadorDataUpdated));
                    tableNotifySuccess(event.getSender(), "Patrocinador actualizado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataUpdated);
                    break;
                case Token.DELETE:
//                    nPatrocinador.delete(event.getParams());
//                    simpleNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente");
                    List<String[]> patrocinadorDataDeleted = nPatrocinador.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente: " + Arrays.toString(patrocinadorDataDeleted));
                    tableNotifySuccess(event.getSender(), "Patrocinador eliminado correctamente", DPatrocinador.HEADERS, (ArrayList<String[]>) patrocinadorDataDeleted);
                    break;
            }
        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            handleError(CONSTRAINTS_ERROR, event.getSender(), ex.getMessage() != null ? Collections.singletonList("Error: " + ex.getMessage()) : null);
        }
    }

    @Override
    public void patrocinio(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
//                    nPatrocinio.save(event.getParams());
                    List<String[]> patrocinioDataSaved = nPatrocinio.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio registrado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio registrado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de patrocinio por ID
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> patrocinioData = nPatrocinio.get(id);
                        if (!patrocinioData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Detalles del Patrocinio", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioData);
                        } else {
                            simpleNotify(event.getSender(), "Error", "Patrocinio no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los patrocinios
                        tableNotifySuccess(event.getSender(), "Lista de Patrocinios", DPatrocinio.HEADERS, nPatrocinio.list());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> patrocinioDataUpdated = nPatrocinio.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio actualizado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nPatrocinio.delete(event.getParams());
                    List<String[]> patrocinioDataDeleted = nPatrocinio.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Patrocinio eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Patrocinio eliminado correctamente", DPatrocinio.HEADERS, (ArrayList<String[]>) patrocinioDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void rol(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
//                    nRol.save(event.getParams());
                    List<String[]> rolDataAdd = nRol.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol guardado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de rol por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> rolData = nRol.get(id);
                            if (rolData != null) {
                                // simpleNotifySuccess(event.getSender(), "Rol encontrado: " + Arrays.toString(rolData));
                                tableNotifySuccess(event.getSender(), "Rol encontrado", DRol.HEADERS, (ArrayList<String[]>) rolData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Rol no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los roles
                        tableNotifySuccess(event.getSender(), "Lista de Roles", DRol.HEADERS, nRol.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    List<String[]> rolDataUpdate = nRol.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol actualizado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataUpdate, event.getCommand());
                    break;
                case Token.DELETE:
                    // nRol.delete(event.getParams());
                    List<String[]> rolDataDelete = nRol.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Rol eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Rol eliminado correctamente", DRol.HEADERS, (ArrayList<String[]>) rolDataDelete, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void servicio(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nServicio.save(event.getParams());
                    List<String[]> servicioDataAdd = nServicio.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio guardado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de servicio por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> servicioData = nServicio.get(id);
                            if (servicioData != null) {
                                // simpleNotifySuccess(event.getSender(), "Servicio encontrado: " + Arrays.toString(servicioData));
                                tableNotifySuccess(event.getSender(), "Servicio encontrado", DServicio.HEADERS, (ArrayList<String[]>) servicioData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Servicio no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los servicios
                        tableNotifySuccess(event.getSender(), "Lista de Servicios", DServicio.HEADERS, nServicio.list());
                    }
                    break;
                    case Token.MODIFY:
                    List<String[]> servicioDataUpdate = nServicio.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio actualizado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataUpdate, event.getCommand());
                    break;
                    case Token.DELETE:
                    // nServicio.delete(event.getParams());
                    List<String[]> servicioDataDelete = nServicio.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Servicio eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Servicio eliminado correctamente", DServicio.HEADERS, (ArrayList<String[]>) servicioDataDelete, event.getCommand());
                    break;
            }
        } catch (NumberFormatException | SQLException | IndexOutOfBoundsException ex) {
            handleError(NUMBER_FORMAT_ERROR, event.getSender(), null);
        }
    }

    @Override
    public void detalleEvento(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nDetalleEvento.save(event.getParams());
                    List<String[]> detalleEventoDataAdd = nDetalleEvento.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Detalle de Evento guardado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataAdd, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de detalle de evento por ID
                        int evento_id = Integer.parseInt(event.getParams().get(0));
                        int servicio_id = Integer.parseInt(event.getParams().get(1));
                        List<String[]> detalleEventoData = nDetalleEvento.get(evento_id, servicio_id);
                        if (detalleEventoData != null) {
                            tableNotifySuccess(event.getSender(), "Detalles del Detalle de Evento", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error", "Detalle de Evento no encontrado.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los detalles de eventos
                        tableNotifySuccess(event.getSender(), "Lista de Detalles de Eventos", DDetalleEvento.HEADERS, nDetalleEvento.list(), event.getCommand());
                    }
                    break;
                case Token.MODIFY:
                    nDetalleEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente");
                    List<String[]> detalleEventoDataUpdate = nDetalleEvento.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente: " + Arrays.toString(detalleEventoDataUpdate));
                    tableNotifySuccess(event.getSender(), "Detalle de Evento actualizado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataUpdate, event.getCommand());
                    break;
                case Token.DELETE:
                    // nDetalleEvento.delete(event.getParams());
                    List<String[]> detalleEventoDataDelete = nDetalleEvento.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Detalle de Evento eliminado
                    tableNotifySuccess(event.getSender(), "Detalle de Evento eliminado correctamente", DDetalleEvento.HEADERS, (ArrayList<String[]>) detalleEventoDataDelete, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }

    @Override
    public void error(ParamsAction event) {
        handleError(event.getAction(), event.getSender(), event.getParams());
    }

    @Override
    public void help(ParamsAction event) {
        try {
            if (event.getAction() == Token.GET) {
                String[] headers = {"Categoría", "Comando", "Descripción"};
                ArrayList<String[]> data = new ArrayList<>();

                // Usuarios
                data.add(new String[]{"Usuarios", "usuario get", "Obtiene todos los usuarios"});
                data.add(new String[]{"Usuarios", "usuario get &lt;id&gt;", "Obtiene usuario por ID"});
                data.add(new String[]{"Usuarios", "usuario add &lt;nombre, apellido, telefono, genero, email, password, rol_id&gt;", "Crea un usuario"});
                data.add(new String[]{"Usuarios", "usuario modify &lt;id, nombre, apellido, telefono, genero, email&gt;", "Modifica un usuario por ID"});
                data.add(new String[]{"Usuarios", "usuario delete &lt;id&gt;", "Elimina un usuario por ID"});

                // Eventos
                data.add(new String[]{"Eventos", "evento get", "Obtiene todos los eventos"});
                data.add(new String[]{"Eventos", "evento get &lt;id&gt;", "Obtiene evento por ID"});
                data.add(new String[]{"Eventos", "evento add &lt;nombre, descripcion, capacidad, precio_entrada, fecha, hora, ubicacion, estado, imagen, servicio_id&gt;", "Crea un evento"});
                data.add(new String[]{"Eventos", "evento modify &lt;id, nombre, descripcion, capacidad, precio_entrada&gt;", "Modifica un evento por ID"});
                data.add(new String[]{"Eventos", "evento delete &lt;id&gt;", "Elimina un evento por ID"});

                // Patrocinadores y Patrocinios
                data.add(new String[]{"Patrocinadores", "patrocinador get", "Obtener todos los patrocinadores"});
                data.add(new String[]{"Patrocinadores", "patrocinador get &lt;id&gt;", "Obtener patrocinador por ID"});
                data.add(new String[]{"Patrocinadores", "patrocinador add &lt;nombre, descripcion, email, telefono&gt;", "Crea un patrocinador"});
                data.add(new String[]{"Patrocinadores", "patrocinador modify &lt;id, nombre, descripcion, email, telefono&gt;", "Modifica un patrocinador por ID"});
                data.add(new String[]{"Patrocinadores", "patrocinador delete &lt;id&gt;", "Elimina un patrocinador por ID"});

                data.add(new String[]{"Patrocinios", "patrocinio get", "Obtiene todos los patrocinios"});
                data.add(new String[]{"Patrocinios", "patrocinio get &lt;id&gt;", "Obtiene patrocinio por ID"});
                data.add(new String[]{"Patrocinios", "patrocinio add &lt;aporte, patrocinador_id, evento_id&gt;", "Crea un patrocinio"});
                data.add(new String[]{"Patrocinios", "patrocinio modify &lt;id, aporte&gt;", "Modifica un patrocinio por ID"});
                data.add(new String[]{"Patrocinios", "patrocinio delete &lt;id&gt;", "Elimina un patrocinio por ID"});

                // Roles
                data.add(new String[]{"Roles", "rol get", "Obtiene todos los roles"});
                data.add(new String[]{"Roles", "rol get &lt;id&gt;", "Obtiene rol por ID"});
                data.add(new String[]{"Roles", "rol add &lt;nombre&gt;", "Crear un rol"});
                data.add(new String[]{"Roles", "rol modify &lt;id, nombre&gt;", "Modifica un rol por ID"});
                data.add(new String[]{"Roles", "rol delete &lt;id&gt;", "Elimina un rol por ID"});

                // Servicios
                data.add(new String[]{"Servicios", "servicio get", "Obtiene todos los servicios"});
                data.add(new String[]{"Servicios", "servicio get &lt;id&gt;", "Obtiene servicio por ID"});
                data.add(new String[]{"Servicios", "servicio add &lt;nombre, descripcion&gt;", "Crea un servicio"});
                data.add(new String[]{"Servicios", "servicio modify &lt;id, nombre, descripcion&gt;", "Modifica un servicio por ID"});
                data.add(new String[]{"Servicios", "servicio delete &lt;id&gt;", "Elimina un servicio por ID"});

                // Reservas
                data.add(new String[]{"Reservas", "reserva get", "Obtiene todas las reservas"});
                data.add(new String[]{"Reservas", "reserva get &lt;id&gt;", "Obtiene reserva por ID"});
                data.add(new String[]{"Reservas", "reserva add &lt;codigo, fecha, costo_entrada, cantidad, costo_total, estado, usuario_id, evento_id&gt;", "Crea una reserva"});
                data.add(new String[]{"Reservas", "reserva modify &lt;id, estado&gt;", "Modifica una reserva por ID"});
                data.add(new String[]{"Reservas", "reserva delete &lt;id&gt;", "Elimina una reserva por ID"});

                // Proveedores
                data.add(new String[]{"Proveedores", "proveedor get", "Obtener todos los proveedores"});
                data.add(new String[]{"Proveedores", "proveedor get &lt;id&gt;", "Obtiene proveedor por ID"});
                data.add(new String[]{"Proveedores", "proveedor add &lt;nombre, telefono, email, direccion&gt;", "Crea un proveedor"});
                data.add(new String[]{"Proveedores", "proveedor modify &lt;id, nombre, telefono, email, direccion&gt;", "Modifica un proveedor por ID"});
                data.add(new String[]{"Proveedores", "proveedor delete &lt;id&gt;", "Elimina un proveedor por ID"});

                // Promociones
                data.add(new String[]{"Promociones", "promocion get", "Obtener todas las promociones"});
                data.add(new String[]{"Promociones", "promocion get &lt;id&gt;", "Obtiene la promocion por ID"});
                data.add(new String[]{"Promociones", "promocion add &lt;descripcion, descuento, fecha_inicio, fecha_fin, proveedor_id&gt;", "Crea una promocion"});
                data.add(new String[]{"Promociones", "promocion modify &lt;id, descuento&gt;", "Modifica una promocion"});
                data.add(new String[]{"Promociones", "promocion delete &lt;id&gt;", "Elimina una promocion"});

                // Pagos
                data.add(new String[]{"Pagos", "pago get", "Obtiene todos los pagos"});
                data.add(new String[]{"Pagos", "pago get &lt;id&gt;", "Obtiene el pago por ID"});
                data.add(new String[]{"Pagos", "pago add &lt;monto, fecha, metodo_pago, reserva_id&gt;", "Crea un pago"});
                data.add(new String[]{"Pagos", "pago modify &lt;id, monto&gt;", "Modifica un pago"});
                data.add(new String[]{"Pagos", "pago delete &lt;id&gt;", "Elimina un pago"});

                // Detalles de Evento
                data.add(new String[]{"Detalles de Evento", "detalleEvento get", "Obtiene todas los detalleevento"});
                data.add(new String[]{"Detalles de Evento", "detalleEvento get &lt;id&gt;", "Obtiene el detalleevento por ID"});
                data.add(new String[]{"Detalles de Evento", "detalleEvento add &lt;evento_id, servicio_id, costo_servicio&gt;", "Crea un detalleevento"});
                data.add(new String[]{"Detalles de Evento", "detalleEvento modify &lt;evento_id, servicio_id, costo_servicio&gt;", "Modifica un detalleevento"});
                data.add(new String[]{"Detalles de Evento", "detalleEvento delete &lt;id&gt;", "Elimina un detalleevento"});

                // Mostrar todos los comandos disponibles de manera organizada
                tableNotifySuccess(event.getSender(), "Comandos disponibles detallados", headers, data);
            }
        } catch (Exception ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error: " + ex.getMessage()));
        }
    }


    @Override
    public void onReceiptEmail(List<Email> emails) {
        for (Email email : emails) {
            Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
            interpreter.setCasoUsoListener(this);
            Thread thread = new Thread(interpreter);
            thread.start();
        }
    }

    private void handleError(int type, String email, List<String> args) {
//        Email emailObject = new Email(email, Email.SUBJECT,
//                HtmlRes.generateText(new String[]{
//                        "Error de Sistema",
//                        "Se ha producido un error al procesar su solicitud."
//                }));
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[]{
                        "Error de Sistema",
                        "Se ha producido un error al procesar su solicitud.",
                        args != null ? args.get(0) : ""
                }));
        sendEmail(emailObject);
    }

    private void simpleNotifySuccess(String email, String message) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[]{
                        "Operación exitosa",
                        message
                }));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data) {
        System.out.println("Email: " + email);
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateTable(title, headers, data));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data, String command) {
        System.out.println("Email: " + email);
        Email emailObject = new Email(email, command,
                HtmlRes.generateTable(title, headers, data));
        sendEmail(emailObject);
    }

    private void sendEmail(Email email) {
        // System.out.println("Emailtecnoweb: " + email);
        try {
            EmailSend sendEmail = new EmailSend(email);
            Thread thread = new Thread(sendEmail);
            thread.start();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void simpleNotify(String email, String title, String message) {
        Email emailObject = new Email(email, Email.SUBJECT,
                HtmlRes.generateText(new String[]{
                        title,
                        message
                }));
        sendEmail(emailObject);
    }

}
