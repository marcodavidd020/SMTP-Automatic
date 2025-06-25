package librerias;

import java.util.ArrayList;
import java.util.List;

/**
 * Generador de HTML mejorado para emails con CSS moderno
 * @author Jairo
 */
public class HtmlRes {
    
    /**
     * CSS moderno para emails responsivos
     */
    private static final String MODERN_CSS = """
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                line-height: 1.6;
                margin: 0;
                padding: 20px;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: #333;
            }
            .container {
                max-width: 800px;
                margin: 0 auto;
                background: white;
                border-radius: 12px;
                box-shadow: 0 10px 30px rgba(0,0,0,0.2);
                overflow: hidden;
            }
            .header {
                background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
                color: white;
                padding: 25px;
                text-align: center;
            }
            .header h1 {
                margin: 0;
                font-size: 24px;
                font-weight: 600;
            }
            .header .subtitle {
                margin: 5px 0 0 0;
                opacity: 0.9;
                font-size: 14px;
            }
            .content {
                padding: 30px;
            }
            .title {
                color: #2c3e50;
                font-size: 22px;
                margin: 0 0 20px 0;
                text-align: center;
                font-weight: 600;
            }
            .subtitle {
                color: #7f8c8d;
                font-size: 16px;
                text-align: center;
                margin: 0 0 30px 0;
            }
            .table-container {
                overflow-x: auto;
                margin: 20px 0;
            }
            table {
                width: 100%;
                border-collapse: collapse;
                margin: 0 auto;
                background: white;
                border-radius: 8px;
                overflow: hidden;
                box-shadow: 0 4px 6px rgba(0,0,0,0.1);
            }
            th {
                background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
                color: white;
                padding: 15px 12px;
                text-align: left;
                font-weight: 600;
                font-size: 14px;
                text-transform: uppercase;
                letter-spacing: 0.5px;
            }
            td {
                padding: 12px;
                border-bottom: 1px solid #ecf0f1;
                font-size: 14px;
                vertical-align: middle;
            }
            .image-cell {
                text-align: center;
                padding: 8px;
            }
            .product-image {
                max-width: 80px;
                max-height: 80px;
                border-radius: 6px;
                box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                object-fit: cover;
                transition: transform 0.2s ease;
            }
            .product-image:hover {
                transform: scale(1.1);
            }
            tr:nth-child(even) {
                background-color: #f8f9fa;
            }
            tr:hover {
                background-color: #e8f4fd;
                transition: background-color 0.3s ease;
            }
            .status-yes {
                color: #27ae60;
                font-weight: bold;
            }
            .status-no {
                color: #e74c3c;
                font-weight: bold;
            }
            .status-dev {
                color: #f39c12;
                font-weight: bold;
            }
            .message-box {
                background: #f8f9fa;
                border-left: 4px solid #3498db;
                padding: 20px;
                margin: 20px 0;
                border-radius: 0 8px 8px 0;
            }
            .error-box {
                background: #fdf2f2;
                border-left: 4px solid #e74c3c;
                padding: 20px;
                margin: 20px 0;
                border-radius: 0 8px 8px 0;
                color: #721c24;
            }
            .success-box {
                background: #f0f9ff;
                border-left: 4px solid #27ae60;
                padding: 20px;
                margin: 20px 0;
                border-radius: 0 8px 8px 0;
                color: #155724;
            }
            .footer {
                background: #2c3e50;
                color: #ecf0f1;
                padding: 20px;
                text-align: center;
                font-size: 12px;
            }
            .footer a {
                color: #3498db;
                text-decoration: none;
            }
            .badge {
                display: inline-block;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: bold;
                text-transform: uppercase;
            }
            .badge-success {
                background: #d4edda;
                color: #155724;
            }
            .badge-danger {
                background: #f8d7da;
                color: #721c24;
            }
            .badge-warning {
                background: #fff3cd;
                color: #856404;
            }
            @media only screen and (max-width: 600px) {
                .container {
                    margin: 10px;
                    border-radius: 8px;
                }
                .content {
                    padding: 20px;
                }
                table {
                    font-size: 12px;
                }
                th, td {
                    padding: 8px 6px;
                }
            }
        </style>
        """;

    /**
     * Genera una tabla HTML moderna con datos
     */
    public static String generateTable(String title, String[] headers, List<String[]> data) {
        StringBuilder tableHeaders = new StringBuilder();
        for (String header : headers) {
            tableHeaders.append("<th>").append(header).append("</th>");
        }

        StringBuilder tableBody = new StringBuilder();
        for (String[] row : data) {
            tableBody.append("<tr>");
            for (String cell : row) {
                String cellContent = formatCellContent(cell);
                tableBody.append("<td>").append(cellContent).append("</td>");
            }
            tableBody.append("</tr>");
        }

        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        
        String html = "<div class=\"container\">" +
            "<div class=\"header\">" +
                "<h1>📊 " + title + "</h1>" +
                "<div class=\"subtitle\">Sistema de Gestión por Email</div>" +
            "</div>" +
            "<div class=\"content\">" +
                "<div class=\"table-container\">" +
                    "<table>" +
                        "<thead>" +
                            "<tr>" + tableHeaders.toString() + "</tr>" +
                        "</thead>" +
                        "<tbody>" +
                            tableBody.toString() +
                        "</tbody>" +
                    "</table>" +
                "</div>" +
                "<div class=\"message-box\">" +
                    "<strong>💡 Información:</strong> Se encontraron " + data.size() + " registros. " +
                    "Envía <strong>'help'</strong> para ver más comandos disponibles." +
                "</div>" +
            "</div>" +
            "<div class=\"footer\">" +
                "🤖 Email Bot - Sistema Automatizado | " +
                "<a href=\"#\">Tecnoweb</a> | " +
                "Generado: " + timestamp +
            "</div>" +
        "</div>";

        return insertInHtml(html);
    }

    /**
     * Genera texto simple con formato moderno
     */
    public static String generateText(String[] args) {
        String title = args.length > 0 ? args[0] : "Información";
        StringBuilder content = new StringBuilder();
        
        if (args.length > 1) {
            content.append("<div class=\"message-box\">");
            for (int i = 1; i < args.length; i++) {
                content.append("<p>").append(args[i]).append("</p>");
            }
            content.append("</div>");
        }

        String timestamp = java.time.LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            
        String html = "<div class=\"container\">" +
            "<div class=\"header\">" +
                "<h1>📧 " + title + "</h1>" +
                "<div class=\"subtitle\">Sistema de Gestión por Email</div>" +
            "</div>" +
            "<div class=\"content\">" +
                content.toString() +
            "</div>" +
            "<div class=\"footer\">" +
                "🤖 Email Bot - Sistema Automatizado | " +
                "<a href=\"#\">Tecnoweb</a> | " +
                "Generado: " + timestamp +
            "</div>" +
        "</div>";

        return insertInHtml(html);
    }

    /**
     * Genera mensaje de error con formato atractivo
     */
    public static String generateError(String title, String message) {
        String html = "<div class=\"container\">" +
            "<div class=\"header\" style=\"background: linear-gradient(135deg, #e74c3c 0%, #c0392b 100%);\">" +
                "<h1>❌ " + title + "</h1>" +
                "<div class=\"subtitle\">Error del Sistema</div>" +
            "</div>" +
            "<div class=\"content\">" +
                "<div class=\"error-box\">" +
                    "<strong>🚨 Error:</strong> " + message +
                "</div>" +
                "<div class=\"message-box\">" +
                    "<strong>💡 Sugerencias:</strong>" +
                    "<ul>" +
                        "<li>Verifica que el comando esté bien escrito</li>" +
                        "<li>Envía <strong>'help'</strong> para ver comandos disponibles</li>" +
                        "<li>Asegúrate de estar registrado en el sistema</li>" +
                    "</ul>" +
                "</div>" +
            "</div>" +
            "<div class=\"footer\">" +
                "🤖 Email Bot - Sistema Automatizado | " +
                "<a href=\"#\">Tecnoweb</a>" +
            "</div>" +
        "</div>";

        return insertInHtml(html);
    }

    /**
     * Genera mensaje de éxito
     */
    public static String generateSuccess(String title, String message) {
        String html = "<div class=\"container\">" +
            "<div class=\"header\" style=\"background: linear-gradient(135deg, #27ae60 0%, #229954 100%);\">" +
                "<h1>✅ " + title + "</h1>" +
                "<div class=\"subtitle\">Operación Exitosa</div>" +
            "</div>" +
            "<div class=\"content\">" +
                "<div class=\"success-box\">" +
                    "<strong>🎉 Éxito:</strong> " + message +
                "</div>" +
            "</div>" +
            "<div class=\"footer\">" +
                "🤖 Email Bot - Sistema Automatizado | " +
                "<a href=\"#\">Tecnoweb</a>" +
            "</div>" +
        "</div>";

        return insertInHtml(html);
    }

    /**
     * Mensaje de bienvenida para nuevos usuarios
     */
    public static String generateWelcome(String email) {
        String html = "<div class=\"container\">" +
            "<div class=\"header\" style=\"background: linear-gradient(135deg, #9b59b6 0%, #8e44ad 100%);\">" +
                "<h1>🎉 ¡Bienvenido!</h1>" +
                "<div class=\"subtitle\">Sistema de Gestión por Email</div>" +
            "</div>" +
            "<div class=\"content\">" +
                "<div class=\"success-box\">" +
                    "<strong>¡Hola!</strong> Tu email <strong>" + email + "</strong> no está registrado en nuestro sistema." +
                "</div>" +
                "<div class=\"message-box\">" +
                    "<strong>📝 Para registrarte, envía un email con el asunto:</strong><br><br>" +
                    "<code style=\"background: #f1f2f6; padding: 8px; border-radius: 4px; font-family: monospace;\">" +
                    "registrar nombre apellido telefono genero" +
                    "</code>" +
                    "<br><br>" +
                    "<strong>Ejemplo:</strong><br>" +
                    "<code style=\"background: #f1f2f6; padding: 8px; border-radius: 4px; font-family: monospace;\">" +
                    "registrar Juan Pérez 123456789 masculino" +
                    "</code>" +
                "</div>" +
                "<div class=\"message-box\">" +
                    "<strong>📚 Comandos disponibles después del registro:</strong>" +
                    "<ul>" +
                        "<li><code>help</code> - Ver todos los comandos</li>" +
                        "<li><code>usuario get</code> - Ver usuarios</li>" +
                        "<li><code>producto get</code> - Ver productos</li>" +
                        "<li><code>categoria get</code> - Ver categorías</li>" +
                    "</ul>" +
                "</div>" +
            "</div>" +
            "<div class=\"footer\">" +
                "🤖 Email Bot - Sistema Automatizado | " +
                "<a href=\"#\">Tecnoweb</a>" +
            "</div>" +
        "</div>";

        return insertInHtml(html);
    }

    /**
     * Formatea el contenido de las celdas
     */
    private static String formatCellContent(String content) {
        if (content == null) return "";
        
        // Formatear estados
        if (content.contains("✅ SÍ")) {
            return "<span class=\"badge badge-success\">✅ SÍ</span>";
        } else if (content.contains("❌ NO")) {
            return "<span class=\"badge badge-danger\">❌ NO</span>";
        } else if (content.contains("⏳ DESARROLLO")) {
            return "<span class=\"badge badge-warning\">⏳ DESARROLLO</span>";
        }
        
        // Detectar y formatear URLs de imágenes
        if (isImageUrl(content)) {
            return formatImageUrl(content);
        }
        
        // Detectar y formatear URLs normales
        if (isUrl(content)) {
            return "<a href=\"" + content + "\" target=\"_blank\" style=\"color: #3498db; text-decoration: none;\">" + 
                   truncateUrl(content) + "</a>";
        }
        
        return content;
    }
    
    /**
     * Verifica si el contenido es una URL de imagen
     */
    private static boolean isImageUrl(String content) {
        if (content == null || content.trim().isEmpty()) return false;
        
        String lower = content.toLowerCase().trim();
        return (lower.startsWith("http://") || lower.startsWith("https://")) &&
               (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || 
                lower.endsWith(".gif") || lower.endsWith(".webp") || lower.endsWith(".svg") ||
                lower.contains("image") || lower.contains("img") || lower.contains("photo"));
    }
    
    /**
     * Verifica si el contenido es una URL
     */
    private static boolean isUrl(String content) {
        if (content == null || content.trim().isEmpty()) return false;
        
        String lower = content.toLowerCase().trim();
        return lower.startsWith("http://") || lower.startsWith("https://") || lower.startsWith("www.");
    }
    
    /**
     * Formatea una URL de imagen como elemento <img>
     */
    private static String formatImageUrl(String imageUrl) {
        String truncatedUrl = truncateUrl(imageUrl);
        return "<div class=\"image-cell\">" +
               "<img src=\"" + imageUrl + "\" alt=\"Imagen del producto\" class=\"product-image\"/>" +
               "<br><small style=\"color: #7f8c8d; font-size: 10px; margin-top: 4px; display: block;\">" + 
               truncatedUrl + "</small>" +
               "</div>";
    }
    
    /**
     * Trunca URLs largas para mostrar mejor
     */
    private static String truncateUrl(String url) {
        if (url == null || url.length() <= 30) return url;
        return url.substring(0, 15) + "..." + url.substring(url.length() - 12);
    }

    /**
     * Método para generar tabla simple (mantener compatibilidad)
     */
    public static String generateTableForSimpleData(String title, String[] headers, String[] data) {
        List<String[]> dataList = new ArrayList<>();
        dataList.add(data);
        return generateTable(title, headers, dataList);
    }
    
    /**
     * Inserta contenido en estructura HTML completa
     */
    private static String insertInHtml(String data) {
        return "<!DOCTYPE html>" +
            "<html lang=\"es\">" +
            "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Sistema de Gestión por Email</title>" +
                MODERN_CSS +
            "</head>" +
            "<body>" +
                data +
            "</body>" +
            "</html>";
    }
}
