/*
MIT License

Copyright (c) 2018 OhEsPaco

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package org.vaporware.com.main;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.objects.PropertiesObjSearcher;
import org.vaporware.com.domain.utilities.ReadFileLineByLineUsingBufferedReader;
import org.vaporware.com.domain.agents.ConstantsClass;

public class Main extends javax.swing.JFrame {

    private static jade.wrapper.AgentContainer mainContainer;
    private static int PORT;

    public static void main(String args[]) throws StaleProxyException, FileNotFoundException, IOException {

        String appConfigPath = "falltube.properties";
        try {
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
            //long numberOfComments = Long.parseLong(appProps.getProperty("numberOfComments"));
            long numberOfComments = 0;

            System.out.print("Querys: ");
            for (String s : querysfiles) {
                System.out.print(s + " ");
            }
            System.out.println();

            System.out.print("DownloadAgents: ");
            for (String s : downloadAgents) {
                System.out.print(s + " ");
            }
            System.out.println();

            System.out.print("SearchAgents: ");
            for (String s : searchAgents) {
                System.out.print(s + " ");
            }
            System.out.println();

            System.out.println("ApiKeys: ");
            for (String s : apiKeys) {
                System.out.println(s);
            }
            System.out.println();

            System.out.println("Host:" + host);
            System.out.println("Database:" + database);
            System.out.println("User:" + user);
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
            System.out.print("<Runtime created>\n");

            // Creamos un perfil por defecto
            Profile profile = new ProfileImpl(null, PORT, null);
            System.out.print("<Profile created>\n");

            System.out.println("<Launching platform>" + profile);
            mainContainer = rt.createMainContainer(profile);

            // Ponemos un perfil por defecto y creamos un container
            ProfileImpl pContainer = new ProfileImpl(null, PORT, null);
            System.out.println("<Launching containers>" + pContainer);

            jade.wrapper.AgentContainer cont = rt.createAgentContainer(pContainer);
            System.out.println("<Containers created>");

            ArrayList<AgentController> agentControllers = new ArrayList();
            System.out.println("<Creating user interface>");
            agentControllers.add(mainContainer.createNewAgent(ConstantsClass.UI_AGENT_NAME,
                    "org.vaporware.com.domain.agents.UIAgent", new Object[0]));
            System.out.println("<Creating DownloadAgents>");
            for (PropertiesObjDownloader pod : podownloader) {
                Object[] ob = {pod};
                agentControllers.add(mainContainer.createNewAgent(pod.getName(),
                        "org.vaporware.com.domain.agents.DownloadAgent", ob));
            }

            System.out.println("<Creating SearchAgents>");
            for (PropertiesObjSearcher pod : posearcher) {
                Object[] ob = {pod};
                agentControllers.add(mainContainer.createNewAgent(pod.getNombre(),
                        "org.vaporware.com.domain.agents.SearchAgent", ob));
            }

            System.out.println("<Starting agents>");
            for (AgentController ag : agentControllers) {
                ag.start();
            }
        } catch (Exception e) {
            System.out.println("Error.");

        }
    }

}
