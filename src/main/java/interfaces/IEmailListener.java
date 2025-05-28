/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import java.util.List;
import librerias.Email;

/**
 *
 * @author MARCO
 */
public interface IEmailListener {
    
    void onReceiptEmail(List<Email> emails );
    
}
