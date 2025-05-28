/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package postgresConecction;

import java.io.*;
import java.net.*;

/**
 *
 * @author MARCO
 */
public class Servidor {

    static final int PUERTO = 5000;

    public Servidor() {
        try {
            ServerSocket skServidor = new ServerSocket(PUERTO);
            System.out.println(" S : Escucho el puerto " + PUERTO);
            for (int numCli = 0; numCli < 3; numCli++) {
                Socket skCliente = skServidor.accept(); //Crea objeto
                System.out.println(" S : Sirvo al cliente " + numCli);
                DataOutputStream salida = new DataOutputStream(skCliente.getOutputStream());
                salida.writeBytes("Hola cliente " + numCli);
                skCliente.close();
            }
            System.out.println(" S : Demasiados clientes por hoy");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Servidor s = new Servidor();
        System.out.println("Servidor");
    }
}
