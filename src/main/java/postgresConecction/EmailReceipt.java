/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.sasl.AuthenticationException;

import interfaces.IEmailListener;
import librerias.Email;

/**
 *
 * @author MARCO
 */
public class EmailReceipt implements Runnable {

    static final String HOST = "mail.tecnoweb.org.bo";//"localhost";  152.70.216.169
    private final static int PORT_POP = 110;
    private final static String USER = "grupo21sc";
    private final static String PASSWORD = "grup021grup021*";

    private Socket socket;
    private BufferedReader input;
    private DataOutputStream output;

    private IEmailListener emailListener;

    public IEmailListener getEmailListener() {
        return emailListener;
    }

    public void setEmailListener(IEmailListener emailListener) {
        this.emailListener = emailListener;
    }

    public EmailReceipt(Socket socket, BufferedReader input, DataOutputStream output) {
        this.socket = null;
        this.input = null;
        this.output = null;
    }

    public EmailReceipt() {
    }

    @Override
    public void run() {
        System.out.println(" C : Conectado a <" + HOST + ">");
        while (true) {
            try {
                this.socket = new Socket(HOST, PORT_POP);
                List<Email> emails = null;
                //abrimos un enlace con el cliente, un flujo, recibimos
                input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                //env un mensaje;
                output = new DataOutputStream(socket.getOutputStream());
                System.out.println("_________ Conexion establecida __________");
                authUser(USER, PASSWORD);
                int cant = this.getEmailCount();
                if (cant > 0) {
                    emails = this.getEmails(cant);
                    System.out.println(emails);
                    this.deleteEmails(cant);
                }
                output.writeBytes("QUIT \r\n");
                input.readLine();
                input.close();
                output.close();
                socket.close();
                System.out.println("__________ Conexion cerrada ___________");

                if (cant > 0) {
                    this.emailListener.onReceiptEmail(emails);
                }
            } catch (IOException e) {
                System.out.println(" C : " + e.getMessage());
                System.out.println(" C: " + e.getLocalizedMessage());
                System.out.println(" C: " + e.getMessage());
            }
            System.out.println(" C : Desconectado del <" + HOST + ">");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(EmailReceipt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void deleteEmails(int emails) throws IOException {
        for (int i = 1; i <= emails; i++) {
            output.writeBytes("DELE " + i + "\r\n");//Command.dele(i));
        }
    }

    private void authUser(String email, String password) throws IOException {
        if (socket != null && input != null && output != null) {
            try {
                input.readLine();
                output.writeBytes("USER " + email + "\r\n");//Command.user(email));
                input.readLine();
                output.writeBytes("PASS " + password + "\r\n");//Command.pass(password));
                String message = input.readLine();
                System.out.println(" C : " + message);
                if (message.contains("-ERR")) {
                    throw new AuthenticationException();
                }
            } catch (Exception e) {
                System.out.println(" C : " + e.getMessage());
                System.out.println(" C: " + e.getStackTrace());
                System.out.println(" C: " + e.getCause());
                System.out.println(" C: " + e.getLocalizedMessage());
                System.out.println(" C: " + e.getMessage());
            }
        }
    }

    private int getEmailCount() throws IOException {
        output.writeBytes("STAT \r\n");//Command.stat());
        String line = input.readLine();
        //+OK 14 6410
        String[] data = line.split(" ");
        return Integer.parseInt(data[1]); //14
    }

    private List<Email> getEmails(int count) throws IOException {
        List<Email> emails = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            output.writeBytes("RETR " + i + "\r\n");//Command.retr(i));
            String text = readMultiline();
            emails.add(Email.getEmail(text));
        }
        return emails;
    }

    private String readMultiline() throws IOException {
        String lines = "";
        while (true) {
            String line = input.readLine();
            if (line == null) {
                throw new IOException("Server no responde (error al abrir el correo)");
            }
            if (line.equals(".")) {
                break;
            }
            lines = lines + "\n" + line;
        }
        return lines;
    }
}
