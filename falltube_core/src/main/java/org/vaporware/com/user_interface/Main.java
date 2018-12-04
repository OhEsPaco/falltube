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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.objects.PropertiesObjSearcher;
import org.vaporware.com.domain.utilities.ReadFileLineByLineUsingBufferedReader;

/**
 *
 * @author pacog
 */
public class Main extends javax.swing.JFrame {
    
    private static jade.wrapper.AgentContainer mainContainer;
    private static int PORT;
    
    ;



    public static void main(String args[]) throws StaleProxyException, FileNotFoundException, IOException {
        //String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfigPath = "falltube.properties";
        
        Properties appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        PORT = Integer.parseInt(appProps.getProperty("jadePort"));
        String[] querysfiles = appProps.getProperty("querys").split(",");
        String[] downloadAgents = appProps.getProperty("downloadAgents").split(",");
        String[] searchAgents = appProps.getProperty("searchAgents").split(",");
        String[] apiKeys = appProps.getProperty("apiKeys").split(",");
        String regionCode = appProps.getProperty("regionCode");
        String host = appProps.getProperty("host");
        int port = Integer.parseInt(appProps.getProperty("port"));
        String database = appProps.getProperty("database");
        String user = appProps.getProperty("user");
        String password = appProps.getProperty("password");
        long numberOfComments = Long.parseLong(appProps.getProperty("numberOfComments"));
        
        System.out.print("Las querys son: ");
        for (String s : querysfiles) {
            System.out.print(s + " ");
        }
        System.out.println();
        
        System.out.print("Los downloadAgents son: ");
        for (String s : downloadAgents) {
            System.out.print(s + " ");
        }
        System.out.println();
        
        System.out.print("Los searchAgents son: ");
        for (String s : searchAgents) {
            System.out.print(s + " ");
        }
        System.out.println();
        
        System.out.println("Las apiKeys son: ");
        for (String s : apiKeys) {
            System.out.println(s);
        }
        System.out.println();
        
        System.out.println("Host:" + host);
        System.out.println("Database:" + database);
        System.out.println("User:" + user);
        System.out.println("Comentarios por video:" + numberOfComments);
        System.out.println();

        //CREACION DE OBJETOS PropertiesObjSearcher
        ArrayList<PropertiesObjSearcher> posearcher = new ArrayList<PropertiesObjSearcher>();
        
        for (String nombre : searchAgents) {
            
            posearcher.add(new PropertiesObjSearcher(nombre));
            
        }
        
        for (PropertiesObjSearcher p : posearcher) {
            for (String apik : apiKeys) {
                p.addApiKey(apik);
            }
        }
        
        ArrayList<String> querys = new ArrayList<String>();
        ArrayList<String> querysAux = new ArrayList<String>();
        //Leer de los ficheros

        for (String s : querysfiles) {
            querysAux = ReadFileLineByLineUsingBufferedReader.read(s);
            for (String z : querysAux) {
                querys.add(z);
            }
        }
        
        int api = 0;
        for (String query : querys) {
            if (api >= posearcher.size()) {
                api = 0;
            }
            posearcher.get(api).addQuery(query);
            api++;
        }
        
        for (int i = 0; i < posearcher.size(); i++) {
            for (int j = 0; j < downloadAgents.length; j++) {
                posearcher.get(i).adddownloadAgent(downloadAgents[j]);
            }
        }
        ///////////////////////////////////////////////////////

        for (int i = 0; i < posearcher.size(); i++) {
            System.out.println(posearcher.get(i));
        }

        //CREACION DE OBJETOS PropertiesObjDownloader 
        ArrayList<PropertiesObjDownloader> podownloader = new ArrayList<PropertiesObjDownloader>();
        
        for (String name : downloadAgents) {
            
            podownloader.add(new PropertiesObjDownloader(name, host, port, database, user, password, numberOfComments, regionCode));
            
        }
        
        for (PropertiesObjDownloader p3 : podownloader) {
            for (String apik : apiKeys) {
                p3.addApiKey(apik);
            }
        }
        
        for (PropertiesObjDownloader p3 : podownloader) {
            for (String searcher : searchAgents) {
                p3.addSearcher(searcher);
            }
        }
        ///////////////////////////////////////////////////////
        for (int i = 0; i < podownloader.size(); i++) {
            System.out.println(podownloader.get(i));
        }

        //Lanzamiento de Agentes
        // Llamamos a la runtime de JADE
        jade.core.Runtime rt = jade.core.Runtime.instance();

        // Salimos de la JVM cuando no hay mas containers
        rt.setCloseVM(true);
        System.out.print("<Runtime Creado>\n");

        // Creamos un perfil por defecto
        Profile profile = new ProfileImpl(null, PORT, null);
        System.out.print("<Perfil Creado>\n");
        
        System.out.println("<Lanzando Plataforma>" + profile);
        mainContainer = rt.createMainContainer(profile);

        // Ponemos un perfil por defecto y creamos un container
        ProfileImpl pContainer = new ProfileImpl(null, PORT, null);
        System.out.println("<Lanzando Containers>" + pContainer);
        
        jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
        System.out.println("<Containers Creados>");
        
        ArrayList<AgentController> agentControllers = new ArrayList();
        System.out.println("<Creando DownloadAgents>");
        for (PropertiesObjDownloader pod : podownloader) {
            Object[] ob = {pod};
            agentControllers.add(mainContainer.createNewAgent(pod.getName(),
                    "org.vaporware.com.domain.agents.DownloadAgent", ob));
        }
        
        System.out.println("<Creando SearchAgents>");
        for (PropertiesObjSearcher pod : posearcher) {
            Object[] ob = {pod};
            agentControllers.add(mainContainer.createNewAgent(pod.getNombre(),
                    "org.vaporware.com.domain.agents.SearchAgent", ob));
        }
        
        System.out.println("<Lanzando el agentes en el container principal>");
        for (AgentController ag : agentControllers) {
            ag.start();
        }
        
    }
    
    private static int aleatorio(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }
    
}
