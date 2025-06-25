/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package librerias;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jairo
 */
public class Instruccion {
    private int casoUso; // CASO DE USO
    private int action; // ACCION
    private List<Integer> params;// LISTA DE PARAMETROS 
    
    public Instruccion() {
        params = new ArrayList<>();
    }

    public int getCasoUso() {
        return casoUso;
    }

    public void setCasoUso(int casoUso) {
        this.casoUso = casoUso;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public List<Integer> getParams() {
        return params;
    }

    public void setParams(List<Integer> params) {
        this.params = params;
    }
    
    public void addParams(int pos){
        params.add(pos);
    }
    
    public int getParams(int pos){
        return params.get(pos);
    }
    
    public int countParams(){
        return params.size();
    }
}
