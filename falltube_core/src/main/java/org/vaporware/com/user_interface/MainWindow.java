/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.user_interface;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;


/**
 *
 * @author pacog
 */
public class MainWindow extends javax.swing.JFrame {
   
    private static jade.wrapper.AgentContainer mainContainer;
    private static int port;

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws StaleProxyException {
        
        
         port = 4552;
        // Llamamos a la runtime de JADE
        jade.core.Runtime rt = jade.core.Runtime.instance();

        // Salimos de la JVM cuando no hay mas containers
        rt.setCloseVM(true);
        System.out.print("<Runtime Creado>\n");

        // Creamos un perfil por defecto
        Profile profile = new ProfileImpl(null, port, null);
        System.out.print("<Perfil Creado>\n");

        System.out.println("<Lanzando Plataforma>" + profile);
        mainContainer = rt.createMainContainer(profile);

        // Ponemos un perfil por defecto y creamos un container
        ProfileImpl pContainer = new ProfileImpl(null, port, null);
        System.out.println("<Lanzando Containers>" + pContainer);

        jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
        System.out.println("<Containers Creados>");

        System.out.println("<Lanzando el agente en el container principal>");

        AgentController rma = mainContainer.createNewAgent("Ag1",
                "org.vaporware.com.domain.agents.SearchAgent", new Object[0]);
        rma.start();
        
    }

                  
             
}
