/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.agents;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.Iterator;
import org.vaporware.com.domain.youtube.YoutubeSearcher;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.search.SearchObject;

/**
 *
 * @author pacog
 */
public class SearchAgent extends Agent {

    private ThreadedBehaviourFactory tbf;
    private static final long MAX_PER_CYCLE = 10;
    private DFAgentDescription[] downloaders;

    @Override
    protected void setup() {

        tbf = new ThreadedBehaviourFactory();
        ServiceDescription servicio = new ServiceDescription();
        servicio.setType("DownloadAgent");
        DFAgentDescription descripcion = new DFAgentDescription();
        // Servicio que busca el agente
        descripcion.addServices(servicio);
        try {
            // Todas las descripciones que encajan con la plantilla proporcionada en el DF
            downloaders = DFService.search(this, descripcion);

            if (downloaders.length == 0) {
                System.out.println("Ningun agente ofrece el servicio deseado");
            }

            doDelete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void takeDown() {
        tbf.interrupt();
        System.out.println("<Agente>Terminado");
    }

    private class SearchBehaviour extends Behaviour {
//downloaders[i].getName()

        private String[] query;
        private int queryIndex=0;
        private int downloadersIndex=0;
        private YoutubeSearcher searcher;
        private boolean salir = false;
        private SearchObject aux = null;
        private String[] ids;

        public SearchBehaviour(Agent a) {
            super(a);

        }

        @Override
        public void onStart() {
            MessageTemplate performativa = MessageTemplate.MatchPerformative(Performatives.DATA_FOR_SEARCHER);
            ACLMessage msg = myAgent.blockingReceive(performativa);
            String[] s = msg.getContent().split("\\s+");
            this.searcher = new YoutubeSearcher(s[0]);
            this.query = new String[s.length - 1];
            for (int i = 1; i < s.length; i++) {
                query[i - 1] = s[i];
            }
        }

        @Override
        public void action() {

            if (!salir) {
                System.out.println("<" + getName() + ">Searching...");
                try {
                    if (aux == null) {
                        aux = searcher.search(query[queryIndex], MAX_PER_CYCLE);
                    } else {
                        aux = searcher.search(query[queryIndex], MAX_PER_CYCLE, aux);
                    }

                    ids = searcher.searchObjectToIDs(aux);
                    downloadersIndex=0;
                    for (String id : ids) {

                        // System.out.println("<" + name + ">Searching:" + id);
                    }

                } catch (NoResultsException | NoMorePagesException e) {

                    if (queryIndex < ids.length - 1) {
                        queryIndex++;
                        aux = null;
                        System.out.println("<" + getName() + ">Cambiando query a:" + query[queryIndex]);
                        block(2000);
                    } else {
                        System.out.println("<" + getName() + ">No hay mas resultados");
                        salir = true;
                    }

                } catch (IOException z) {
                    System.out.println("<" + getName() + ">Exception irrecuperable");
                    salir = true;
                }
            }
        }

        @Override
        public boolean done() {
            return salir;
        }

    }

}
