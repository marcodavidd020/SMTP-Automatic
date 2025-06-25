/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import librerias.analex.Token;

/**
 *
 * @author Jairo
 */
public class ParamsAction extends EventObject {
    
    private int action;// accion del caso de uso: add. modify, delete...
    private List<String> params; // lista de parametros
    private Object source;// objeto que envio el comando
    
    private String sender;// correo que envio el comando

    private String command; // comando recibido
    
    public ParamsAction(Object source) {
        super(source);
        params = new ArrayList<>();
    }
    
    public ParamsAction(Object source, String sender) {
        super(source);
        this.sender = sender;
        params = new ArrayList<>();
    }

    public ParamsAction(Object source, String sender, String command) {
        super(source);
        this.sender = sender;
        params = new ArrayList<>();
        this.command = command;
    }

    public List<String> getParams() {
        return params;
    }

    public void setParams(List<String> params) {
        this.params = params;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getSender() {
        return sender;
    }

    public String getCommand() {
        return command;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    

    public Object getSource() {
        return source;
    }

    public void setSource(Object source) {
        this.source = source;
    }    
    
    public void addParams(String param){
        if(!param.isEmpty())
            params.add(param);
    }
    
    public String getParams(int pos){
        return params.get(pos);
    }
    
    public int countParams(){
        return params.size();
    }
    
    @Override
    public String toString(){
        Token token = new Token();
        String s = "";
        s = s + "Remitente= " + sender + "\n";
        s = s + "Comando= " + command + "\n";
        s = s + "Action= " + token.getStringToken(action) + "\n";
        s = s + "Params= \n";
        for(String param : params){
            s = s + "   " + param + "\n";
        }
        return s;
    }
}
