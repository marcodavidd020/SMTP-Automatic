package com.mycompany.parcial1.tecnoweb;

import interfaces.ICasoUsoListener;
import interfaces.IEmailListener;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import librerias.Email;
import librerias.Interpreter;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.*;
import postgresConecction.EmailReceipt;
import postgresConecction.EmailSend;

/**
 * Clase de prueba para demostrar el manejo de eventos y acciones para diversas entidades en el sistema.
 */
public class pruebas {

    /**
     * Método principal para ejecutar pruebas.
     */
    public static void main(String[] args) {
        //_crudDB();
        //_estructuraInstruccion();
        //_instruccionToken();
        //_sendEmail();
        _socketEmailReceipt();
    }

    /**
     * Método para recibir emails y procesarlos usando un Listener.
     */
    private static void _socketEmailReceipt() {
        EmailReceipt mail = new EmailReceipt();
        mail.setEmailListener(new IEmailListener() {
            @Override
            public void onReceiptEmail(List<Email> emails) {
                for (Email email : emails) {
                    System.out.println(email);
                    interprete(email);
                }
            }
        });
        Thread thread = new Thread(mail);
        thread.setName("Recibiendo mail");
        thread.start();
    }

    /**
     * Método para enviar emails.
     */
    private static void _sendEmail() {
        Email emailObject = new Email("example@example.com", Email.SUBJECT, "Mensaje de prueba para funcionalidad de email.");
        EmailSend sendEmail = new EmailSend(emailObject);
        Thread thread = new Thread(sendEmail);
        thread.setName("Send email Thread");
        thread.start();
    }

    /**
     * Método para interpretar comandos recibidos via email.
     */
    private static void _instruccionToken() {
        String instruccion = "usuario get";
        String correo = "user@example.com";

        NUsuario nUsuario = new NUsuario();
        Interpreter interpreter = new Interpreter(instruccion, correo);
        interpreter.setCasoUsoListener(new ICasoUsoListener() {
            @Override
            public void usuario(ParamsAction paramsAction) {
                System.out.println("CU: USUARIO");
                try {
                    if (paramsAction.getAction() == Token.GET) {
                        ArrayList<String[]> lista = nUsuario.list();
                        lista.forEach(u -> System.out.println(String.join(" | ", u)));
                    }
                } catch (SQLException ex) {
                    System.out.println("Error SQL: " + ex.getMessage());
                }
            }

            @Override
            public void evento(ParamsAction event) {

            }

            @Override
            public void reserva(ParamsAction event) {

            }

            @Override
            public void pago(ParamsAction event) {

            }

            @Override
            public void proveedor(ParamsAction event) {

            }

            @Override
            public void promocion(ParamsAction event) {

            }

            @Override
            public void patrocinador(ParamsAction event) {

            }

            @Override
            public void patrocinio(ParamsAction event) {

            }

            @Override
            public void rol(ParamsAction event) {

            }

            @Override
            public void servicio(ParamsAction event) {

            }

            @Override
            public void detalleEvento(ParamsAction event) {

            }

            @Override
            public void error(ParamsAction event) {
                System.out.println("Error desconocido: " + event);
            }

            @Override
            public void help(ParamsAction event) {

            }

            // Implement other methods as necessary
        });

        Thread thread = new Thread(interpreter);
        thread.setName("Interpreter Thread");
        thread.start();
    }

    /**
     * Método para demostrar la estructura de instrucción.
     */
    private static void _estructuraInstruccion() {
        String casoUso = "evento";
        String action = "add";
        List<String> params = new ArrayList<>();
        params.add("Evento de Gala");
        params.add("2024-12-31");
        NEvento nEvento = new NEvento();

        if (casoUso.equals("evento")) {
            if (action.equals("add")) {
                try {
                    nEvento.save(params);
                    System.out.println("Evento guardado exitosamente");
                } catch (SQLException ex) {
                    Logger.getLogger(pruebas.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            } else {
                System.out.println("Acción no soportada");
            }
        } else {
            System.out.println("Caso de uso no soportado");
        }
    }

    /**
     * Método para demostrar operaciones CRUD.
     */
    private static void _crudDB() {
        NEvento nEvento = new NEvento();
        List<String> evento = new ArrayList<>();
        evento.add("12345");
        evento.add("Concierto de Rock");
        evento.add("2024-10-05");
        try {
            nEvento.save(evento);
            System.out.println("Evento nuevo añadido");
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Método para interpretar y manejar emails.
     */
    public static void interprete(Email email) {
        NEvento nEvento = new NEvento();
        Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
        interpreter.setCasoUsoListener(new ICasoUsoListener() {
            @Override
            public void usuario(ParamsAction event) {

            }

            @Override
            public void evento(ParamsAction paramsAction) {
                System.out.println("CU: EVENTO");
                try {
                    if (paramsAction.getAction() == Token.GET) {
                        ArrayList<String[]> lista = nEvento.list();
                        lista.forEach(e -> System.out.println(String.join(" | ", e)));
                    }
                } catch (SQLException ex) {
                    System.out.println("Error SQL: " + ex.getMessage());
                }
            }

            @Override
            public void reserva(ParamsAction event) {

            }

            @Override
            public void pago(ParamsAction event) {

            }

            @Override
            public void proveedor(ParamsAction event) {

            }

            @Override
            public void promocion(ParamsAction event) {

            }

            @Override
            public void patrocinador(ParamsAction event) {

            }

            @Override
            public void patrocinio(ParamsAction event) {

            }

            @Override
            public void rol(ParamsAction event) {

            }

            @Override
            public void servicio(ParamsAction event) {

            }

            @Override
            public void detalleEvento(ParamsAction event) {

            }

            @Override
            public void error(ParamsAction event) {
                System.out.println("Error desconocido: " + event);
            }

            @Override
            public void help(ParamsAction event) {

            }
        });

        Thread thread = new Thread(interpreter);
        thread.setName("Interpreter Thread");
        thread.start();
    }
}
