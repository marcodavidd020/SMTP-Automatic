package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Maneja las conexiones de clientes SMTP y procesa los comandos
 *
 * @author MARCO
 */
public class SMTPClientHandler implements Runnable {

    private final Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String mailFrom;
    private List<String> mailTo;
    private StringBuilder messageData;
    private boolean dataMode = false;

    public SMTPClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.mailTo = new ArrayList<>();
        this.messageData = new StringBuilder();
    }

    @Override
    public void run() {
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);

            // Saludo inicial del servidor
            sendResponse("220 mi-servidor.local SMTP Server Ready");

            String command;
            while ((command = reader.readLine()) != null) {
                System.out.println("📨 Cliente: " + command);

                if (dataMode) {
                    if (".".equals(command.trim())) {
                        // Fin de datos - procesar y enviar email
                        dataMode = false;
                        processEmail();
                        sendResponse("250 Message accepted for delivery");
                        resetSession();
                    } else {
                        messageData.append(command).append("\n");
                    }
                } else {
                    processCommand(command);
                }
            }

        } catch (IOException e) {
            System.err.println("Error manejando cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error cerrando conexión: " + e.getMessage());
            }
        }
    }

    private void processCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0].toUpperCase();

        switch (cmd) {
            case "HELO":
            case "EHLO":
                sendResponse("250 mi-servidor.local Hello");
                break;

            case "MAIL":
                if (parts.length > 1 && parts[1].toUpperCase().startsWith("FROM:")) {
                    mailFrom = extractEmail(parts[1]);
                    sendResponse("250 OK");
                } else {
                    sendResponse("501 Syntax error");
                }
                break;

            case "RCPT":
                if (parts.length > 1 && parts[1].toUpperCase().startsWith("TO:")) {
                    String email = extractEmail(parts[1]);
                    mailTo.add(email);
                    sendResponse("250 OK");
                } else {
                    sendResponse("501 Syntax error");
                }
                break;

            case "DATA":
                sendResponse("354 Start mail input; end with <CRLF>.<CRLF>");
                dataMode = true;
                break;

            case "QUIT":
                sendResponse("221 Bye");
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error cerrando conexión: " + e.getMessage());
                }
                break;

            default:
                sendResponse("502 Command not implemented");
                break;
        }
    }

    private void processEmail() {
        System.out.println("📮 Procesando email:");
        System.out.println("   From: " + mailFrom);
        System.out.println("   To: " + String.join(", ", mailTo));
        System.out.println("   Message: " + messageData.toString().substring(0, Math.min(100, messageData.length())) + "...");

        // Aquí es donde enviaremos el email a través de Gmail
        GmailRelay relay = new GmailRelay();
        for (String recipient : mailTo) {
            relay.sendEmail(mailFrom, recipient, "Mensaje desde mi servidor", messageData.toString());
        }
    }

    private String extractEmail(String mailCommand) {
        // Extraer email de "FROM:<email>" o "TO:<email>"
        int start = mailCommand.indexOf('<');
        int end = mailCommand.indexOf('>');
        if (start != -1 && end != -1) {
            return mailCommand.substring(start + 1, end);
        }
        return mailCommand.split(":")[1].trim();
    }

    private void sendResponse(String response) {
        System.out.println("📤 Servidor: " + response);
        writer.println(response);
    }

    private void resetSession() {
        mailFrom = null;
        mailTo.clear();
        messageData.setLength(0);
    }
}
