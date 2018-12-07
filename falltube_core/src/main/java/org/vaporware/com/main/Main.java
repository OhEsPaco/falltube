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
import java.util.UUID;
import org.vaporware.com.domain.utilities.ReadFileLineByLineUsingBufferedReader;
import org.vaporware.com.domain.objects.PropertiesObjManagement;

public class Main extends javax.swing.JFrame {

    private static jade.wrapper.AgentContainer mainContainer;
    private static int PORT;

    public static void main(String args[]) throws StaleProxyException, FileNotFoundException, IOException {
     
        String appConfigPath = "falltube.properties";
        try {
            Properties appProps = new Properties();
            appProps.load(new FileInputStream(appConfigPath));
            String regionCode = appProps.getProperty("regionCode");
            String[] apiKeysFiles = appProps.getProperty("apiKeys").split(",");
            String[] querysfiles = appProps.getProperty("querys").split(",");
            PORT = Integer.parseInt(appProps.getProperty("jadePort"));

            int downloadAgents = Integer.parseInt(appProps.getProperty("downloadAgents"));
            int searchAgents = Integer.parseInt(appProps.getProperty("searchAgents"));
            int uiAgents = Integer.parseInt(appProps.getProperty("uiAgents"));

            String host = appProps.getProperty("host");
            int port = Integer.parseInt(appProps.getProperty("port"));
            String database = appProps.getProperty("database");
            String user = appProps.getProperty("user");
            String password = appProps.getProperty("password");

            System.out.print("Querys: ");
            for (String s : querysfiles) {
                System.out.print(s + " ");
            }
            System.out.println();

            System.out.println("DownloadAgents: " + downloadAgents);

            System.out.println("SearchAgents: " + searchAgents);

            System.out.println("UIAgents: " + uiAgents);

            System.out.println("ApiKeys: ");
            for (String s : apiKeysFiles) {
                System.out.println(s);
            }
            System.out.println();

            System.out.println("Host:" + host);
            System.out.println("Database:" + database);
            System.out.println("User:" + user);
            System.out.println();

            PropertiesObjManagement p1 = new PropertiesObjManagement(host, port, database, user, password, regionCode, uiAgents, downloadAgents, searchAgents);

            for (String s : querysfiles) {
                for (String z : ReadFileLineByLineUsingBufferedReader.read(s)) {
                    p1.addQuery(z);
                }
            }

            for (String s : apiKeysFiles) {
                for (String z : ReadFileLineByLineUsingBufferedReader.read(s)) {
                    p1.addApiKey(z);
                }
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

            Object[] ob = {p1};
            agentControllers.add(mainContainer.createNewAgent(generateName("manager"),
                    "org.vaporware.com.domain.agents.ManagementAgent", ob));

            System.out.println("<Starting agents>");
            for (AgentController ag : agentControllers) {
                ag.start();
            }
        } catch (Exception e) {
            System.out.println("Error. Check properties file.");

        }
    }

    private static String generateName(String type) {
        return type.toUpperCase() + "-" + UUID.randomUUID().toString();
    }
}
