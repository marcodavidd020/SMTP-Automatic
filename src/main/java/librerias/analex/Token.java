package librerias.analex;

/**
 * Clase Token para manejar los identificadores y acciones en el análisis léxico.
 */
public class Token {
    private int name; // Si es CU, ACTION o ERROR
    private int attribute; // Tipo específico, ya sea CU, ACTION o ERROR

    // Constantes numéricas para manejar el análisis léxico
    public static final int CU = 0;
    public static final int ACTION = 1;
    public static final int PARAMS = 2;
    public static final int END = 3;
    public static final int ERROR = 4;

    // Constantes para los títulos de casos de uso
    public static final int USUARIO = 100;
    public static final int EVENTO = 101;
    public static final int RESERVA = 102;
    public static final int PAGO = 103;
    public static final int PROVEEDOR = 104;
    public static final int PROMOCION = 105;
    public static final int PATROCINADOR = 106;
    public static final int PATROCINIO = 107;
    public static final int ROL = 108;
    public static final int SERVICIO = 109;
    public static final int DETALLEEVENTO = 110;
    public static final int PRODUCTO = 112;
    public static final int CATEGORIA = 113;
    public static final int CLIENTE = 114;
    public static final int TIPO_PAGO = 115;

    public static final int HELP = 111;

    // Constantes para las acciones generales
    public static final int ADD = 200;
    public static final int DELETE = 201;
    public static final int MODIFY = 202;
    public static final int GET = 203;
    public static final int VERIFY = 204;
    public static final int CANCEL = 205;
    public static final int REPORT = 206;

    // Constantes de errores
    public static final int ERROR_COMMAND = 300;
    public static final int ERROR_CHARACTER = 301;

    // Constructor por defecto
    public Token() {}

    /**
     * Constructor parametrizado 2.
     * @param name
     */
    public Token(int name){
        this.name = name;
    }

    /**
     * Constructor parametrizado 3.
     * @param name
     * @param attribute
     */
    public Token(int name, int attribute){
        this.name = name;
        this.attribute = attribute;
    }

    // Constructor parametrizado por el literal del token
    public Token(String token) {
        int id = findByLexeme(token);
        if (id != -1) {
            if (100 <= id && id < 200) {
                this.name = CU;
                this.attribute = id;
            } else if (200 <= id && id < 300) {
                this.name = ACTION;
                this.attribute = id;
            }
        } else {
            this.name = ERROR;
            this.attribute = ERROR_COMMAND;
            System.err.println("Error: El lexema enviado al constructor no es reconocido como un token. Lexema: " + token);
        }
    }

    // Getters y setters
    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getAttribute() {
        return attribute;
    }

    public void setAttribute(int attribute) {
        this.attribute = attribute;
    }

    // Método para obtener el string asociado a un token
    public String getStringToken(int token) {
        switch (token) {
            case CU:
                return "caso de uso";
            case ACTION:
                return "action";
            case PARAMS:
                return "params";
            case END:
                return "end";
            case ERROR:
                return "error";
            case USUARIO:
                return "usuario";
            case EVENTO:
                return "evento";
            case RESERVA:
                return "reserva";
            case PAGO:
                return "pago";
            case PROVEEDOR:
                return "proveedor";
            case PROMOCION:
                return "promocion";
            case PATROCINADOR:
                return "patrocinador";
            case PATROCINIO:
                return "patrocinio";
            case ROL:
                return "rol";
            case SERVICIO:
                return "servicio";
            case DETALLEEVENTO:
                return "detalleevento";
            case PRODUCTO:
                return "producto";
            case CATEGORIA:
                return "categoria";
            case CLIENTE:
                return "cliente";
            case TIPO_PAGO:
                return "tipo_pago";
            case HELP:
                return "help";
            case ADD:
                return "add";
            case DELETE:
                return "delete";
            case MODIFY:
                return "modify";
            case GET:
                return "get";
            case VERIFY:
                return "verify";
            case CANCEL:
                return "cancel";
            case REPORT:
                return "report";
            case ERROR_COMMAND:
                return "UNKNOWN COMMAND";
            case ERROR_CHARACTER:
                return "UNKNOWN CHARACTER";
            default:
                return "N: " + token;
        }
    }

    // Método para encontrar un token por lexema
    private int findByLexeme(String lexeme) {
        switch (lexeme.toLowerCase()) {
            case "caso de uso":
                return CU;
            case "action":
                return ACTION;
            case "params":
                return PARAMS;
            case "end":
                return END;
            case "error":
                return ERROR;
            case "usuario":
                return USUARIO;
            case "evento":
                return EVENTO;
            case "reserva":
                return RESERVA;
            case "pago":
                return PAGO;
            case "proveedor":
                return PROVEEDOR;
            case "promocion":
                return PROMOCION;
            case "patrocinador":
                return PATROCINADOR;
            case "patrocinio":
                return PATROCINIO;
            case "rol":
                return ROL;
            case "servicio":
                return SERVICIO;
            case "detalleevento":
                return DETALLEEVENTO;
            case "producto":
                return PRODUCTO;
            case "categoria":
                return CATEGORIA;
            case "cliente":
                return CLIENTE;
            case "tipo_pago":
                return TIPO_PAGO;
            case "help":
                return HELP;
            case "add":
                return ADD;
            case "delete":
                return DELETE;
            case "modify":
                return MODIFY;
            case "get":
                return GET;
            case "verify":
                return VERIFY;
            case "cancel":
                return CANCEL;
            case "report":
                return REPORT;
            case "UNKNOWN COMMAND":
                return ERROR_COMMAND;
            case "UNKNOWN CHARACTER":
                return ERROR_CHARACTER;
            default:
                return -1;
        }
    }
}
