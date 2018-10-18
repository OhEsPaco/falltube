/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import javax.swing.JTextArea;

/**
 *
 * @author pacog
 */
public class Communicator {
    public static JTextArea jTextArea1;
    public Communicator(){
        
    }
     public Communicator(JTextArea jTextArea1){
        this.jTextArea1=jTextArea1;
    }
     public JTextArea getArea(){
         return jTextArea1;
     }
}
