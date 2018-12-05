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
package org.vaporware.com.domain.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import org.vaporware.com.domain.youtube.YoutubeSearcher;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.objects.PropertiesObjSearcher;
import org.vaporware.com.domain.search.SearchObject;

public class SearchAgent extends Agent {

    private ThreadedBehaviourFactory tbf;
    private static final long MAX_PER_CYCLE = 10;
    private PropertiesObjSearcher props;
    private AID uiAid;

    @Override
    protected void setup() {
        uiAid = new AID(ConstantsClass.UI_AGENT_NAME, AID.ISLOCALNAME);
        sendToUI(ConstantsClass.COLOR_MAGENTA, "<" + getName() + ">Setting up...");
        System.out.println("<" + getName() + ">Setting up...");
        Object[] arguments = getArguments();
        props = (PropertiesObjSearcher) arguments[0];
        tbf = new ThreadedBehaviourFactory();
        addBehaviour(tbf.wrap(new SearchBehaviour(this)));

    }

    private void sendToUI(String color, String msg) {
        if (uiAid != null) {
            ACLMessage msgUi = new ACLMessage(ConstantsClass.UI_PRINT);
            msgUi.setSender(getAID());
            msgUi.addReceiver(uiAid);
            msgUi.setLanguage(color);
            msgUi.setContent(msg);
            send(msgUi);
        }
    }

    @Override
    protected void takeDown() {
        sendToUI(ConstantsClass.COLOR_MAGENTA, "<" + getName() + ">Taking down...");
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
            
            ACLMessage msgCancel = myAgent.receive(MessageTemplate.MatchPerformative(ConstantsClass.DOWNLOADER_DOWN));

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
                sendToUI(ConstantsClass.COLOR_RED, "<" + getName() + ">No more downloaders active...");
                System.out.println("<" + getName() + ">No more downloaders active...");
            }
            if (!salir) {
                sendToUI(ConstantsClass.COLOR_BLUE, "<" + getName() + ">Searching...");
                
                try {
                    if (aux == null) {
                        aux = searcher.search(query[queryIndex], MAX_PER_CYCLE);
                    } else {
                        aux = searcher.search(query[queryIndex], MAX_PER_CYCLE, aux);
                    }

                    ids = searcher.searchObjectToIDs(aux);

                    for (String id : ids) {
                        AID receptor = downloaders.get(aleatorio(0, downloaders.size() - 1));
                        ACLMessage msg = new ACLMessage(ConstantsClass.ID_FOR_DOWNLOADER);
                        msg.addReceiver(receptor);
                        msg.setContent(id);
                        sendToUI(ConstantsClass.COLOR_BLUE, "<" + myAgent.getName() + ">Sending:" + id + " to: " + receptor.getLocalName());

                        send(msg);
                    }

                } catch (NoResultsException | NoMorePagesException e) {

                    if (queryIndex < query.length - 1) {
                        queryIndex++;
                        aux = null;
                        sendToUI(ConstantsClass.COLOR_RED, "<" + getName() + ">Changing query to: " + query[queryIndex]);
                        System.out.println("<" + getName() + ">Changing query to: " + query[queryIndex]);
                        block(2000);
                    } else {
                        sendToUI(ConstantsClass.COLOR_BLUE, "<" + getName() + ">No more results.");
                        System.out.println("<" + getName() + ">No more results.");
                        salir = true;
                    }

                } catch (IOException z) {

                    if (actualRun < ConstantsClass.MAX_RETRIES) {
                        if (actualKey >= apiKeys.size() - 1) {
                            actualKey = 0;
                            actualRun++;
                            block(ConstantsClass.MS_WAIT_ON_RETRY);
                        } else {
                            actualKey++;
                        }
                        sendToUI(ConstantsClass.COLOR_BLUE, "<" + myAgent.getName() + ">Changing key...");
                        System.out.println("<" + myAgent.getName() + ">Changing key...");
                        searcher = new YoutubeSearcher(apiKeys.get(actualKey));
                    } else {
                        salir = true;
                    }
                } catch (Exception e) {
                    sendToUI(ConstantsClass.COLOR_RED, "<" + myAgent.getName() + ">Unknown error.");
                    System.out.println("<" + myAgent.getName() + ">Unknown error.");
                }
            }
        }

        @Override
        public boolean done() {

            return salir;

        }

        public int onEnd() {
            sendToUI(ConstantsClass.COLOR_BLUE, "<" + myAgent.getName() + ">Terminating searcher...");
            System.out.println("<" + myAgent.getName() + ">Terminating searcher...");
            return 0;
        }
    }

}
