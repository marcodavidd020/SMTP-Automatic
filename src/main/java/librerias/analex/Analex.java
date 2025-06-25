/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias.analex;

/**
 *
 * @author Jairo
 */
public class Analex {
    public static final char EOF = 0;   //fin de linea
    public static final char SPACE = 32;//spacio
    public static final char TAB = 9;   //tab
    public static final char NL = 10;   //saltoLn
    
    private String ac;
    private Cinta cinta;
    private Token R;
    private TSParams tsp; // Necesitamos las demas tablas de memorias

    public Analex(String cinta) {
        if (cinta == null) {
            cinta = ""; // Asigna un string vacío si el input es null
        }
        this.cinta = new Cinta(cinta);
        tsp = new TSParams();
        init();
    }


    public void init(){
        cinta.init();
        tsp.init();// contruyo de nuevo las tablas
        dt();        
    }
    
    /**
     * Devuelve el token analizado.
     * @return 
     */
    public Token Preanalisis(){
        return R;
    }
    
    /**
     * Realiza el siguiente analisis.
     */
    public void next(){
        dt();
    }
    
    /**
     * Muestra los parametros.
     */
    public void showParams(){
        System.out.println(tsp);
    }
    
    /**
     * Devuelve el parametro en la posicion pos.
     * @param pos
     * @return 
     */
    public String getParam(int pos){
        return tsp.getStr(pos);
    }
    
    /**
     * Devuelve el lexema.
     * @return 
     */
    public String lexeme(){
        return ac;
    }
    
    private boolean isSpace(char c){
        return (c == SPACE || c == TAB || c == NL);
    }

    private boolean isDigit(char c){
        return ('0' <= c && c <= '9');
    }

    private boolean isLetter(char c){
        return (('A' <= c && c <= 'Z') || ('a' <= c && c <= 'z'));
    }
    
    private boolean isBeginParams(char c){
        return c == '<';// < [ { 
    }
    
    private boolean isEndParams(char c){
        return c == '>';// > ] }
    }
    
    private boolean isComa(char c){
        return c == ',';// , ; | es el simbolo para separar parametros
    }
    
    private boolean isDate(char c){        
        return c == '-' || c == '/' || c == ':';
    }
    
    private boolean isEmail(char c){        
        return c == 64 || c == '.' || c == '_' || c == '-';
    }

    /**
     * Realiza el analisis del comando.
     */
    private void dt(){
        int status = 0;
        int index = -1;
        while(true){
            char c = cinta.CC();
            switch(status){
                case 0:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 0;
                    }else if(isLetter(c)){
                        ac = c + "";
                        cinta.forward();
                        status = 1;
                    }else if(isBeginParams(c) || isComa(c)){                       
                        ac = "";
                        cinta.forward();
                        status = 33;
                    }else if(c == EOF || isEndParams(c)){
                        ac = "";
                        status = 6;
                    }else{
                        ac = "" + c;
                        status = 7;
                    }
                    break;

                case 1:
                    if(isLetter(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 1;
                    }else if(isSpace(c) || isBeginParams(c)){
                        status = 2;
                    }else{
                        ac = "" + c;
                        status = 7;
                    }
                    break;

                case 2:
                    R = new Token(ac);
                    return;
                    
                case 33:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 33;
                    }else if(isLetter(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isDigit(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 34;
                    }else if(isEndParams(c)){
                        status = 5;
                    }else{
                        ac = "" + c;
                        status = 8;
                    }
                    break;
                    
                case 34:
                    if(isSpace(c)){
                        cinta.forward();
                        status = 34;
                    }else if(isDigit(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 34;
                    }else if(isEndParams(c)){
                        status = 5;
                    }else if(isComa(c)){
                        status = 4;
                    }else if(isLetter(c)){
                        status = 3;
                    }else {
                        status = 3;
                    }
                    break;

                case 3:
                    if(isLetter(c) || isDigit(c) || isDate(c) || isEmail(c)){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isEndParams(c)){
                        status = 5;
                    } else if(c == SPACE){
                        ac = ac + c;
                        cinta.forward();
                        status = 3;
                    }else if(isSpace(c)){
                        cinta.forward();
                        status = 3;
                    }else if(isComa(c)){
                        status = 4;
                    } else {
                        ac = "" + c;
                        status = 8;
                    }
                    break;

                case 4:
                    index = tsp.add(ac);
                    R = new Token(Token.PARAMS, index);
                    return;

                case 5:
                    index = tsp.add(ac);
                    R = new Token(Token.PARAMS, index);
                    return;

                case 6:
                    R = new Token(Token.END);
                    return;

                case 7:
                    R = new Token(Token.ERROR, Token.ERROR_COMMAND);
                    return;
                case 8:
                    R = new Token(Token.ERROR, Token.ERROR_CHARACTER);
                    return;
            }
        }
    }
}
