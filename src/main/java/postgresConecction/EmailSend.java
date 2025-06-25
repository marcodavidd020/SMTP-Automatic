/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import librerias.Email;

/**
 * Clase de envío de emails que puede usar diferentes servidores SMTP
 *
 * @author Jairo
 */
public class EmailSend implements Runnable {

    // 🔧 CONFIGURACIÓN DEL SERVIDOR - Cambia según necesites

    /*
     * // Opción 1: Servidor personalizado
     * private final static String PORT_SMTP = "2525";
     * private final static String HOST = "localhost"; // IP de tu servidor
     * personalizado
     * private final static String MAIL = "admin@mi-servidor.local";
     * private final static boolean USE_AUTH = false; // Tu servidor no necesita
     * auth
     */

    // Opción 2: Tecnoweb (ACTIVO para EmailApp Tecnoweb)
    private final static String PORT_SMTP = "25"; // Puerto SMTP estándar (CONFIRMADO FUNCIONAL)
    private final static String HOST = "mail.tecnoweb.org.bo";
    private final static String MAIL = "grupo21sc@tecnoweb.org.bo";
    private final static boolean USE_AUTH = false;

    private final static String USER = "grupo21sc";
    private final static String MAIL_PASSWORD = "grup021grup021*";

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        properties.setProperty("mail.smtp.auth", "false"); // Sin autenticación

        // Configuración específica para tecnoweb - servidor con restricciones
        properties.setProperty("mail.smtp.starttls.enable", "false");
        properties.setProperty("mail.smtp.ssl.enable", "false");
        properties.setProperty("mail.smtp.ssl.trust", "*");
        properties.setProperty("mail.smtp.connectiontimeout", "60000"); // 60 segundos
        properties.setProperty("mail.smtp.timeout", "60000"); // 60 segundos
        properties.setProperty("mail.smtp.writetimeout", "60000"); // 60 segundos

        // Configuración adicional para servidores restrictivos
        properties.setProperty("mail.smtp.localhost", "localhost");
        properties.setProperty("mail.smtp.ehlo", "false"); // Usar HELO en lugar de EHLO
        properties.setProperty("mail.debug", "true"); // Debug para ver qué pasa

        // Para servidores que no requieren autenticación como tecnoweb
        System.out.println("🔧 Configurando SMTP sin autenticación:");
        System.out.println("   🌐 Host: " + HOST);
        System.out.println("   🔌 Puerto: " + PORT_SMTP);
        System.out.println("   🔐 Auth: false");
        System.out.println("   🔒 SSL/TLS: false");

        // Crear sesión SIN autenticación
        Session session = Session.getInstance(properties, null);

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo()) };

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
            System.out.println("✅ Email enviado exitosamente desde " + HOST + ":" + PORT_SMTP);

        } catch (NoSuchProviderException | AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("❌ Error de dirección: " + ex.getMessage());
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("❌ Error de conexión SMTP: " + ex.getMessage());
            System.out.println("💡 Asegúrate de que el servidor SMTP esté ejecutándose en " + HOST + ":" + PORT_SMTP);
        }
    }
}