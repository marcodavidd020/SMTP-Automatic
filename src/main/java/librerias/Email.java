/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias;

/**
 *
 * @author Jairo
 */
public class Email {

    public static final String SUBJECT = "Request response";
    private String from;
    private String to;
    private String subject;
    private String message;

    public Email() {
    }

    public Email(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public Email(String from, String subject) {
        this.from = from;
        this.subject = subject;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMesssage(String messsage) {
        this.message = messsage;
    }

    public static Email getEmail(String plain_text) {
        return new Email(getFrom(plain_text), getSubject(plain_text));
    }

    private static String getFrom(String plain_text) {
        String search = "Return-Path: <";
        int index_begin = plain_text.indexOf(search) + search.length();
        int index_end = plain_text.indexOf(">");
        return plain_text.substring(index_begin, index_end);
    }

    private static String getSubject(String plain_text) {
        try {
            String search = "Subject: ";
            int i = plain_text.indexOf(search) + search.length();
            int e = plain_text.indexOf("\n", i);  // Usar el salto de línea para limitar el final del asunto

            if (e == -1) {  // Si no se encuentra el salto de línea, usar la longitud del texto
                e = plain_text.length();
            }

            String subjectPart = plain_text.substring(i, e).trim();

            // Quitar "RV:" si está presente al inicio del asunto
            String rv = "RV:";
            if (subjectPart.startsWith(rv)) {
                subjectPart = subjectPart.substring(rv.length()).trim();
            }

            System.out.println("Subject extracted: " + subjectPart);
            return subjectPart + " ";
        } catch (Exception e) {
            System.out.println("Error extracting subject: " + e);
        }
        return null;
    }

    @Override
    public String toString() {
        return "[From: " + from + ", To: " + to + ", Subject: " + subject + ", Message: " + message + "]";
    }
}
