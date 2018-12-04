package org.vaporware.com.domain.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import static org.vaporware.com.domain.agents.Performatives.MAX_RETRIES;
import static org.vaporware.com.domain.agents.Performatives.MS_WAIT_ON_RETRY;
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
        System.out.println("<" + getName() + ">Setting up...");
        Object[] arguments = getArguments();
        props = (PropertiesObjSearcher) arguments[0];
        tbf = new ThreadedBehaviourFactory();
        addBehaviour(new SearchBehaviour(this));

    }

    @Override
    protected void takeDown() {
        System.out.println("<" + getName() + ">Taking down...");
        tbf.interrupt();
    }

    private int aleatorio(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

    private class SearchBehaviour extends Behaviour {

        private String[] query;
        private ArrayList<AID> downloaders;

        private int queryIndex = 0;
        private YoutubeSearcher searcher;
        private boolean salir = false;
        private SearchObject aux = null;
        private String[] ids;

        private ArrayList<String> apiKeys;
        private int actualKey;
        private int actualRun;

        public SearchBehaviour(Agent a) {
            super(a);
            apiKeys = props.getApiKeys();
        }

        @Override
        public void onStart() {

            this.actualKey = 0;
            this.actualRun = 0;
            this.salir = false;

            query = new String[props.getQuerys().size()];
            downloaders = new ArrayList<AID>();
            ArrayList<String> querys = props.getQuerys();
            ArrayList<String> down = props.getDownloadAgents();

            for (int i = 0; i < query.length; i++) {
                query[i] = querys.get(i);
            }

            for (int i = 0; i < down.size(); i++) {
                downloaders.add(new AID(down.get(i), AID.ISLOCALNAME));
            }

            searcher = new YoutubeSearcher(apiKeys.get(actualKey));
        }

        @Override
        public void action() {
            //Performatives.DOWNLOADER_DOWN
            ACLMessage msgCancel = myAgent.receive(MessageTemplate.MatchPerformative(Performatives.DOWNLOADER_DOWN));

            if (msgCancel != null) {
                for (int i = 0; i < downloaders.size(); i++) {
                    if (downloaders.get(i).equals(msgCancel.getSender())) {
                        downloaders.remove(i);
                        break;
                    }
                }
            }

            if (downloaders.isEmpty()) {
                salir = true;
                System.out.println("<" + getName() + ">No more downloaders active...");
            }
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
                        AID receptor = downloaders.get(aleatorio(0, downloaders.size() - 1));
                        ACLMessage msg = new ACLMessage(Performatives.ID_FOR_DOWNLOADER);
                        msg.addReceiver(receptor);
                        msg.setContent(id);
                        System.out.println("<" + myAgent.getName() + ">Sending:" + id + " to: " + receptor.getLocalName());
                        send(msg);
                    }

                } catch (NoResultsException | NoMorePagesException e) {

                    if (queryIndex < query.length - 1) {
                        queryIndex++;
                        aux = null;
                        System.out.println("<" + getName() + ">Changing query to:" + query[queryIndex]);
                        block(2000);
                    } else {
                        System.out.println("<" + getName() + ">No more results");
                        salir = true;
                    }

                } catch (IOException z) {
                   
                    if (actualRun < MAX_RETRIES) {
                        if (actualKey >= apiKeys.size() - 1) {
                            actualKey = 0;
                            actualRun++;
                            block(MS_WAIT_ON_RETRY);
                        } else {
                            actualKey++;
                        }
                        System.out.println("<" + myAgent.getName() + ">Changing key...");
                        searcher = new YoutubeSearcher(apiKeys.get(actualKey));
                    } else {
                        salir = true;
                    }
                } catch (Exception e) {
                    System.out.println("<" + myAgent.getName() + ">Unknown error.");
                }
            }
        }

        @Override
        public boolean done() {

            return salir;

        }

        public int onEnd() {
            System.out.println("<" + myAgent.getName() + ">Terminating searcher...");
            return 0;
        }
    }

}
