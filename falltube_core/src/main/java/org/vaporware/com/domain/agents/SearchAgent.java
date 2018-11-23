package org.vaporware.com.domain.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.util.ArrayList;
import org.vaporware.com.domain.youtube.YoutubeSearcher;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.objects.PropertiesObjSearcher;
import org.vaporware.com.domain.search.SearchObject;

/**
 *
 * @author pacog
 */
public class SearchAgent extends Agent {

    private ThreadedBehaviourFactory tbf;
    private static final long MAX_PER_CYCLE = 10;
    private PropertiesObjSearcher props;

    @Override
    protected void setup() {
        System.out.println("<" + getName() + ">Setting up");
        Object[] arguments = getArguments();
        props = (PropertiesObjSearcher) arguments[0];
        tbf = new ThreadedBehaviourFactory();
        addBehaviour(new SearchBehaviour(this));

    }

    @Override
    protected void takeDown() {
        System.out.println("<" + getName() + ">Taking down");
        tbf.interrupt();
    }

    private int aleatorio(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    private class SearchBehaviour extends Behaviour {

        private String[] query;
        private String[] downloaders;

        private int queryIndex = 0;
        private YoutubeSearcher searcher;
        private boolean salir = false;
        private SearchObject aux = null;
        private String[] ids;

        public SearchBehaviour(Agent a) {
            super(a);

        }

        @Override
        public void onStart() {
            searcher = new YoutubeSearcher(props.getApiKey());
            query = new String[props.getQuerys().size()];
            downloaders = new String[props.getDownloadAgents().size()];
            ArrayList<String> querys = props.getQuerys();
            ArrayList<String> down = props.getDownloadAgents();
            for (int i = 0; i < query.length; i++) {
                query[i] = querys.get(i);
            }

            for (int i = 0; i < downloaders.length; i++) {
                downloaders[i] = down.get(i);
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

                    for (String id : ids) {
                        String d = downloaders[aleatorio(0, downloaders.length - 1)];
                        AID receptor = new AID(d, AID.ISLOCALNAME);
                        ACLMessage msg = new ACLMessage(Performatives.ID_FOR_DOWNLOADER);
                        msg.addReceiver(receptor);
                        msg.setContent(id);
                        System.out.println("<" + myAgent.getName() + ">Sending:" + id + " to: " + d);
                        send(msg);
                    }

                } catch (NoResultsException | NoMorePagesException e) {

                    if (queryIndex < query.length - 1) {
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
