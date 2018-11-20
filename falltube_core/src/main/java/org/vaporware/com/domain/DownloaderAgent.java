/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.search.SearchObject;

/**
 *
 * @author pacog
 */
public class DownloaderAgent extends Agent {

    private DownloaderBehaviour[] comps = new DownloaderBehaviour[5];
    private ThreadedBehaviourFactory tbf;
    private static final long MAX_PER_CYCLE = 10;

    @Override
    protected void setup() {
        //String[] query = {"OT+2018", "fortnite", "wismichu", "frank+cuesta", "lifehacks"};
        String[] query = {"OT+2018", "fortnite"};
        //String name, String apikey, String query, long maxResults
        comps[0] = new DownloaderBehaviour("D0", "AIzaSyB-qDUXf_uuFatsVZxvcR6dTJZIfigg7_8", query, MAX_PER_CYCLE);
        comps[1] = new DownloaderBehaviour("D1", "AIzaSyDkfZmnEr57TZODbzVheGc2V0YdVLejt_w", query, MAX_PER_CYCLE);
        comps[2] = new DownloaderBehaviour("D2", "AIzaSyApQPReZsm3AuAmr3GF-XT1Fyjxr_goQiQ", query, MAX_PER_CYCLE);
        comps[3] = new DownloaderBehaviour("D3", "AIzaSyC58IFEuX6tvUtaYcM1NBZACMx1kS3H0uI", query, MAX_PER_CYCLE);
        comps[4] = new DownloaderBehaviour("D4", "AIzaSyAzybpaX__BhzsZrEi-dJFZvsJxjNyvg3Q", query, MAX_PER_CYCLE);

        tbf = new ThreadedBehaviourFactory();
        for (int i = 0; i < 1; i++) {
            addBehaviour(tbf.wrap(comps[i]));

        }
        addBehaviour(tbf.wrap(new Stopper()));
    }

    @Override
    protected void takeDown() {
        tbf.interrupt();
        System.out.println("<Agente>Terminado");
    }

    private class DownloaderBehaviour extends Behaviour {

        private String APIKEY;
        private String name;
        private String[] query;
        private int queryIndex;
        private long maxResults;
        private YoutubeDataDownloader downloader;
        private boolean salir = false;
        private SearchObject aux = null;
        private String[] ids;
        private boolean first = true;

        public DownloaderBehaviour(String name, String apikey, String query[], long maxResults) {
            this.name = name;
            this.APIKEY = apikey;
            this.query = query;
            this.maxResults = maxResults;

        }

        @Override
        public void onStart() {
            downloader = new YoutubeDataDownloader(APIKEY);
            queryIndex = 0;
        }

        public String getName() {
            return name;
        }

        @Override
        public void action() {

            if (!salir) {
                System.out.println("<" + name + ">Downloading...");
                try {
                    if (aux == null) {
                        aux = downloader.search(query[queryIndex], maxResults);
                    } else {
                        aux = downloader.search(query[queryIndex], maxResults, aux);
                    }

                    ids = downloader.searchObjectToIDs(aux);
                    for (String id : ids) {
                        // downloader.videoIdToSql(ids[a], 50);
                        System.out.println("<" + name + ">Downloading:" + id);
                    }

                } catch (NoResultsException | NoMorePagesException e) {

                    if (queryIndex < ids.length-1) {
                        queryIndex++;
                        aux=null;
                        System.out.println("<" + name + ">Cambiando query a:" + query[queryIndex]);
                        block(2000);
                    } else {
                        System.out.println("<" + name + ">No hay mas resultados");
                        salir = true;
                    }

                } catch (IOException z) {
                    System.out.println("<" + name + ">Exception irrecuperable");
                    salir = true;
                }
            }
        }

        @Override
        public boolean done() {
            return salir;
        }

    }

    private class Stopper extends Behaviour {

        private boolean salir;

        @Override
        public void action() {

            salir = true;
            for (DownloaderBehaviour comp : comps) {
                if (comp.done() == false) {
                    salir = false;
                    break;
                }
            }

        }

        @Override
        public boolean done() {

            return salir;
        }

        @Override
        public int onEnd() {
            System.out.println("<Stopper>Todos los agentes parados");
            myAgent.doDelete();
            return 0;
        }

    }
}
