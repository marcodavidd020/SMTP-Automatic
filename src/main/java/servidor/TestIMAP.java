package servidor;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Test simple para verificar conexión IMAP con Gmail
 */
public class TestIMAP {
    
    public static void main(String[] args) {
        System.out.println("🧪 Probando conexión IMAP a Gmail...");
        
        String host = "imap.gmail.com";
        String username = "marcodavidtoledo@gmail.com";
        String password = "muknnpzrymdkduss";
        
        try {
            // Configurar propiedades IMAP
            Properties props = new Properties();
            props.put("mail.store.protocol", "imaps");
            props.put("mail.imaps.host", host);
            props.put("mail.imaps.port", "993");
            props.put("mail.imaps.ssl.enable", "true");
            props.put("mail.imaps.ssl.protocols", "TLSv1.2");
            props.put("mail.imaps.ssl.trust", "*");
            
            Session session = Session.getInstance(props);
            
            // Conectar al servidor
            System.out.println("🔗 Conectando a " + host + "...");
            Store store = session.getStore("imaps");
            store.connect(host, username, password);
            
            System.out.println("✅ Conexión IMAP exitosa!");
            
            // Abrir bandeja de entrada
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // Obtener información básica
            int totalMessages = inbox.getMessageCount();
            int unreadMessages = inbox.getUnreadMessageCount();
            
            System.out.println("📊 Información de la bandeja:");
            System.out.println("   📧 Total de mensajes: " + totalMessages);
            System.out.println("   📨 Mensajes no leídos: " + unreadMessages);
            
            // Mostrar últimos 3 mensajes
            if (totalMessages > 0) {
                System.out.println("\n📋 Últimos 3 mensajes:");
                int start = Math.max(1, totalMessages - 2);
                Message[] messages = inbox.getMessages(start, totalMessages);
                
                for (int i = 0; i < messages.length; i++) {
                    Message msg = messages[i];
                    System.out.println("   " + (i+1) + ". From: " + msg.getFrom()[0]);
                    System.out.println("      Subject: " + msg.getSubject());
                    System.out.println("      Date: " + msg.getReceivedDate());
                    System.out.println("      Read: " + msg.isSet(Flags.Flag.SEEN));
                    System.out.println();
                }
            }
            
            // Cerrar conexiones
            inbox.close(false);
            store.close();
            
            System.out.println("✅ Test IMAP completado exitosamente!");
            
        } catch (Exception e) {
            System.err.println("❌ Error en test IMAP: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 