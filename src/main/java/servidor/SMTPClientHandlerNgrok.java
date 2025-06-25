package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handler SMTP optimizado para conexiones a través de ngrok
 * @author Jairo
 */
public class SMTPClientHandlerNgrok implements Runnable {
    
    private final Socket clientSocket;
    private final String serverDomain;
    private BufferedReader reader;
    private PrintWriter writer;
    private String mailFrom;
    private List<String> mailTo;
    private StringBuilder messageData;
    private boolean dataMode = false;
    private String clientInfo;
    
    public SMTPClientHandlerNgrok(Socket clientSocket, String serverDomain) {
        this.clientSocket = clientSocket;
        this.serverDomain = serverDomain;
        this.mailTo = new ArrayList<>();
        this.messageData = new StringBuilder();
        this.clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
    
    @Override
    public void run() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream(), true);
            
            System.out.println("🔗 [" + timestamp + "] Cliente conectado: " + clientInfo);
            
            // Saludo inicial con dominio ngrok
            sendResponse("220 " + serverDomain + " SMTP Server Ready via ngrok");
            
            String command;
            while ((command = reader.readLine()) != null) {
                logCommand("📨", command);
                
                if (dataMode) {
                    if (".".equals(command.trim())) {
                        dataMode = false;
                        processEmail();
                        sendResponse("250 Message accepted for delivery via ngrok");
                        resetSession();
                    } else {
                        messageData.append(command).append("\n");
                    }
                } else {
                    processCommand(command);
                }
            }
            
        } catch (IOException e) {
            System.err.println("❌ [" + timestamp + "] Error con cliente " + clientInfo + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("🔌 [" + timestamp + "] Cliente desconectado: " + clientInfo);
            } catch (IOException e) {
                System.err.println("❌ Error cerrando conexión: " + e.getMessage());
            }
        }
    }
    
    private void processCommand(String command) {
        String[] parts = command.split(" ", 2);
        String cmd = parts[0].toUpperCase();
        
        switch (cmd) {
            case "HELO":
            case "EHLO":
                sendResponse("250 " + serverDomain + " Hello via ngrok");
                break;
                
            case "MAIL":
                if (parts.length > 1 && parts[1].toUpperCase().startsWith("FROM:")) {
                    mailFrom = extractEmail(parts[1]);
                    sendResponse("250 OK - Sender accepted");
                } else {
                    sendResponse("501 Syntax error in MAIL command");
                }
                break;
                
            case "RCPT":
                if (parts.length > 1 && parts[1].toUpperCase().startsWith("TO:")) {
                    String email = extractEmail(parts[1]);
                    mailTo.add(email);
                    sendResponse("250 OK - Recipient accepted");
                } else {
                    sendResponse("501 Syntax error in RCPT command");
                }
                break;
                
            case "DATA":
                sendResponse("354 Start mail input; end with <CRLF>.<CRLF>");
                dataMode = true;
                break;
                
            case "QUIT":
                sendResponse("221 " + serverDomain + " closing connection");
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("❌ Error cerrando conexión: " + e.getMessage());
                }
                break;
                
            case "RSET":
                resetSession();
                sendResponse("250 Reset OK");
                break;
                
            case "NOOP":
                sendResponse("250 OK");
                break;
                
            default:
                sendResponse("502 Command '" + cmd + "' not implemented");
                break;
        }
    }
    
    private void processEmail() {
        System.out.println("📮 [" + getCurrentTime() + "] Procesando email via ngrok:");
        System.out.println("   👤 From: " + mailFrom);
        System.out.println("   📧 To: " + String.join(", ", mailTo));
        System.out.println("   🌐 Via: " + clientInfo);
        System.out.println("   📝 Preview: " + getMessagePreview());
        
        // Procesar headers del mensaje para extraer Subject
        String subject = extractSubjectFromMessage();
        
        // Enviar a través de Gmail Relay
        GmailRelay relay = new GmailRelay();
        for (String recipient : mailTo) {
            relay.sendEmail(mailFrom, recipient, subject, messageData.toString());
        }
        
        System.out.println("✅ [" + getCurrentTime() + "] Email procesado para " + mailTo.size() + " destinatario(s)");
    }
    
    private String extractSubjectFromMessage() {
        String[] lines = messageData.toString().split("\n");
        for (String line : lines) {
            if (line.toLowerCase().startsWith("subject:")) {
                return line.substring(8).trim(); // "Subject: ".length() = 8
            }
        }
        return "Mensaje desde " + serverDomain;
    }
    
    private String getMessagePreview() {
        String message = messageData.toString();
        return message.length() > 100 ? 
            message.substring(0, 100) + "..." : 
            message;
    }
    
    private String extractEmail(String mailCommand) {
        int start = mailCommand.indexOf('<');
        int end = mailCommand.indexOf('>');
        if (start != -1 && end != -1) {
            return mailCommand.substring(start + 1, end);
        }
        return mailCommand.split(":")[1].trim();
    }
    
    private void sendResponse(String response) {
        logCommand("📤", response);
        writer.println(response);
    }
    
    private void logCommand(String icon, String message) {
        System.out.println(icon + " [" + getCurrentTime() + "] " + clientInfo + ": " + message);
    }
    
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    private void resetSession() {
        mailFrom = null;
        mailTo.clear();
        messageData.setLength(0);
    }
} 