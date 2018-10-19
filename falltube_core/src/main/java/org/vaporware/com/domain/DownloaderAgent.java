/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.core.behaviours.TickerBehaviour;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.search.SearchObject;

/**
 *
 * @author pacog
 */
public class DownloaderAgent extends Agent {

    private int agentesParados = 0;
   

    @Override
    protected void setup() {
        DownloaderBehaviour[] comps = new DownloaderBehaviour[5];
        //String name, String apikey, String query, long maxResults
        comps[0] = new DownloaderBehaviour("D0", "AIzaSyB-qDUXf_uuFatsVZxvcR6dTJZIfigg7_8", "OT+2018", 50l);
        comps[1] = new DownloaderBehaviour("D1", "AIzaSyDkfZmnEr57TZODbzVheGc2V0YdVLejt_w", "fortnite", 50l);
        comps[2] = new DownloaderBehaviour("D2", "AIzaSyApQPReZsm3AuAmr3GF-XT1Fyjxr_goQiQ", "wismichu", 50l);
        comps[3] = new DownloaderBehaviour("D3", "AIzaSyC58IFEuX6tvUtaYcM1NBZACMx1kS3H0uI", "frank+cuesta", 50l);
        comps[4] = new DownloaderBehaviour("D4", "AIzaSyAzybpaX__BhzsZrEi-dJFZvsJxjNyvg3Q", "lifehacks", 50l);

        ParallelBehaviour par = new ParallelBehaviour();
        Ticker tic = new Ticker(this, 60000, comps);

        for (int i = 0; i < comps.length; i++) {
            par.addSubBehaviour(comps[i]);
        }
        ThreadedBehaviourFactory tbf = new ThreadedBehaviourFactory();
        par.addSubBehaviour(tbf.wrap(tic));
        addBehaviour(par);
    }

    @Override
    protected void takeDown() {
      
        System.out.println("<Agente>Terminado");
    }

    private class Ticker extends TickerBehaviour {

        private DownloaderBehaviour[] comps;
        private int behaviourToSleep = 0;

        public Ticker(Agent a, long time, DownloaderBehaviour[] comps) {
            super(a, time);
            this.comps = comps;
            
            System.out.println("<Ticker>Blocking " + comps[behaviourToSleep].getName());
            comps[behaviourToSleep].block();
        }

        @Override
        protected void onTick() {
            
            System.out.println("<Ticker>Restarting " + comps[behaviourToSleep].getName());
            comps[behaviourToSleep].restart();
            if (behaviourToSleep >= comps.length - 1) {
                behaviourToSleep = 0;
            } else {
                behaviourToSleep++;
            }
           
            System.out.println("<Ticker>Blocking " + comps[behaviourToSleep].getName());
            comps[behaviourToSleep].block();
            if (agentesParados >= comps.length) {
                //  com.getArea().append("<Ticker>Stopping...\n");
                System.out.println("<Ticker>Stopping...");
                this.stop();
            }
        }

        @Override
        public int onEnd() {
            myAgent.doDelete();
            return 0;
        }

    }

    private class DownloaderBehaviour extends Behaviour {

        private String APIKEY;
        private String name;
        private String query;
        private long maxResults;
        private YoutubeDataDownloader downloader;
        private boolean continuar = true;
        private SearchObject aux;
        private String[] ids;
        private boolean first = true;

        public DownloaderBehaviour(String name, String apikey, String query, long maxResults) {
            this.name = name;
            this.APIKEY = apikey;
            this.query = query;
            this.maxResults = maxResults;
            downloader = new YoutubeDataDownloader(APIKEY);

        }

        public String getName() {
            return name;
        }

        public void onStart() {
           
                try {
                    
                    System.out.println("<" + name + ">Starting...");
                    aux = downloader.search(query, maxResults);
                    ids = downloader.searchObjectToIDs(aux);
                    for (int a = 0; a < ids.length; a++) {
                        downloader.videoIdToSql(ids[a], 50);
                    }
                } catch (IOException ex) {
                
                    System.out.println("<" + name + ">IOException");
                    continuar = false;
                    agentesParados++;
                } catch (NoResultsException ex) {
                  
                    System.out.println("<" + name + ">No more results");
                    continuar = false;
                    agentesParados++;
                } catch (Exception ex) {
                   
                    System.out.println("<" + name + ">Exception");
                    continuar = false;
                    agentesParados++;
                }

            
        }

        @Override
        public void action() {
          
            
            if (continuar) {
                System.out.println("<" + name + ">Downloading...");
                try {
                    aux = downloader.search(query, maxResults, aux);
                    ids = downloader.searchObjectToIDs(aux);
                    for (int a = 0; a < ids.length; a++) {
                        downloader.videoIdToSql(ids[a], 50);
                    }
                } catch (IOException ex) {
                   
                    System.out.println("<" + name + ">IOException");
                    continuar = false;
                    agentesParados++;
                } catch (NoResultsException ex) {
                    
                    System.out.println("<" + name + ">No more results");
                    continuar = false;
                    agentesParados++;
                } catch (Exception ex) {
                   
                    System.out.println("<" + name + ">Exception");
                    continuar = false;
                    agentesParados++;
                }
            }
        }

        @Override
        public boolean done() {
            return continuar;
        }

    }
}
