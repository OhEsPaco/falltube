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
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import org.vaporware.com.domain.youtube.YoutubeSearcher;
import org.vaporware.com.domain.exceptions.NoMorePagesException;
import org.vaporware.com.domain.exceptions.NoResultsException;
import org.vaporware.com.domain.search.SearchObject;

public class SearchAgent extends FalltubeAgent {

    private ThreadedBehaviourFactory tbf;
    private static final long MAX_PER_CYCLE = 10;

    @Override
    protected void setup() {
        try {
            tbf = new ThreadedBehaviourFactory();

            print(CCS.COLOR_GREEN, "<" + getName() + ">Setting up searcher", true);
            registerAgent(CCS.SEARCHER_DF);
            addBehaviour(new SearchBehaviourNew());
            addBehaviour(tbf.wrap(new Die()));
        } catch (Exception e) {
            doDelete();
            print(CCS.COLOR_RED, "<" + getName() + ">Error setting up searcher", true);
        }
    }

    @Override
    protected void takeDown() {
        deregisterAgent();
        tbf.interrupt();
        print(CCS.COLOR_RED, "<" + getName() + ">Taking down...", true);
    }

    private class Die extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.KILL_YOURSELF));
            if (msg != null) {
                myAgent.doDelete();
            }
        }

    }

    private class SearchBehaviourNew extends Behaviour {

        private String query;
        private YoutubeSearcher searcher;
        private String apiKey;
        private SearchObject aux;
        private String[] ids;

        @Override
        public void onStart() {
            query = null;
            searcher = null;
            apiKey = null;
            aux = null;
        }

        @Override
        public void action() {

            AID manager = randomAgent(CCS.MANAGEMENT_DF);
            AID receptor = randomAgent(CCS.DOWNLOADER_DF);

            if (receptor == null) {
                block(60000);
            } else {
                if (manager != null) {
                    if (searcher != null) {
                        if (query != null) {
                            try {
                                if (aux == null) {
                                    aux = searcher.search(query, MAX_PER_CYCLE);
                                } else {
                                    aux = searcher.search(query, MAX_PER_CYCLE, aux);
                                }

                                ids = searcher.searchObjectToIDs(aux);

                                for (String id : ids) {
                                    ACLMessage msg = new ACLMessage(CCS.ID_FOR_DOWNLOADER);
                                    msg.addReceiver(receptor);
                                    msg.setContent(id);
                                    send(msg);
                                }

                            } catch (NoResultsException | NoMorePagesException e) {
                                ACLMessage msg = new ACLMessage(CCS.COMPLETED_QUERY);
                                msg.setSender(myAgent.getAID());
                                msg.addReceiver(manager);
                                msg.setContent(query);
                                myAgent.send(msg);
                                query = null;
                                aux = null;

                            } catch (IOException z) {
                                apiKey = null;
                                searcher = null;
                            } catch (Exception e) {

                                print(CCS.COLOR_RED, "<" + myAgent.getName() + ">Unknown error.", true);
                            }
                        } else {
                            //want query
                            ACLMessage msg = new ACLMessage(CCS.WANT_QUERY);
                            msg.addReceiver(manager);
                            msg.setSender(myAgent.getAID());
                            myAgent.send(msg);
                            print(CCS.COLOR_RED, "<" + getName() + ">Waiting for query...", true);
                            ACLMessage queryMsg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(CCS.TAKE_YOUR_QUERY));
                            query = queryMsg.getContent();
                            aux = null;
                        }
                    } else {
                        //want api and searcher
                        ACLMessage msg = new ACLMessage(CCS.WANT_API);
                        msg.addReceiver(manager);
                        msg.setSender(myAgent.getAID());
                        myAgent.send(msg);
                        print(CCS.COLOR_RED, "<" + getName() + ">Waiting for API...", true);
                        ACLMessage apiMsg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(CCS.TAKE_YOUR_API));
                        apiKey = apiMsg.getContent();
                        searcher = new YoutubeSearcher(apiKey);
                    }

                } else {
                    myAgent.doDelete();
                }
            }

        }

        @Override
        public boolean done() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.KILL_YOURSELF));
            if (msg != null) {
                myAgent.doDelete();
                return true;
            } else {
                return false;
            }

        }

    }

}
