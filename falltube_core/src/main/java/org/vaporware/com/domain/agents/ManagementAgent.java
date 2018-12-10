/*
MIT License

Copyright (c) 2018 Francisco Manuel Garcia Sanchez

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
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import org.vaporware.com.domain.objects.PropertiesObjManagement;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

public class ManagementAgent extends FalltubeAgent {

    private PropertiesObjManagement pom;
    private ThreadedBehaviourFactory tbf;
    private AgentNumberChecker checker;
    private ArrayList<String> wordsDownloaded = new ArrayList<String>();
    private long videosOk = 0;

    //Reparte keys y queries
    @Override
    protected void setup() {
        //queries keys
        try {
            Object[] arguments = getArguments();
            pom = (PropertiesObjManagement) arguments[0];
            tbf = new ThreadedBehaviourFactory();
            ParallelBehaviour par = new ParallelBehaviour(ParallelBehaviour.WHEN_ALL);

            if (pom == null) {
                doDelete();
                print(CCS.COLOR_RED, "<" + getName() + ">Error setting up manager", true);
            } else {
                for (int i = 0; i < pom.getNumberOfUIAgents(); i++) {
                    launchAgent(generateName(CCS.UI_DF), CCS.UI_AGENT_CLASS, null);
                }
                print(CCS.COLOR_GREEN, "<" + getName() + ">Setting up manager", true);
                registerAgent(CCS.MANAGEMENT_DF);
                checker = new AgentNumberChecker();
                par.addSubBehaviour(tbf.wrap(checker));
                par.addSubBehaviour(new ApiKeyDispatcher());
                par.addSubBehaviour(new QueryDispatcher());
                par.addSubBehaviour(new CountVideos());
                par.addSubBehaviour(tbf.wrap(new Die()));
                addBehaviour(par);
            }
        } catch (Exception e) {
            doDelete();
            print(CCS.COLOR_RED, "<" + getName() + ">Error setting up manager", true);
        }

    }

    @Override
    protected void takeDown() {
        deregisterAgent();
        tbf.interrupt();
        print(CCS.COLOR_RED, "<" + getName() + ">Taking down...", true);
        print(CCS.COLOR_GREEN, "Number of downloaded videos: " + videosOk, true);
        print(CCS.COLOR_GREEN, "Number of downloaded words: " + wordsDownloaded.size(), true);
        wordsDownloaded.sort(String::compareToIgnoreCase);
        print(CCS.COLOR_GREEN, "Downloaded words: " + wordsDownloaded.toString(), true);
        ACLMessage msg = new ACLMessage(CCS.KILL_YOURSELF);

        DFAgentDescription[] agents = getAgents(CCS.SEARCHER_DF);
        for (DFAgentDescription df : agents) {
            msg.addReceiver(df.getName());
        }

        agents = getAgents(CCS.DOWNLOADER_DF);
        for (DFAgentDescription df : agents) {
            msg.addReceiver(df.getName());
        }

        agents = getAgents(CCS.MANAGEMENT_DF);
        for (DFAgentDescription df : agents) {
            msg.addReceiver(df.getName());
        }

        send(msg);
    }

    private String generateName(String type) {
        return type.toUpperCase() + "-" + UUID.randomUUID().toString();
    }

    protected boolean launchAgent(String agentName, String agentClass, Object[] args) {
        try {
            ContainerController cc = getContainerController();
            AgentController ac = cc.createNewAgent(agentName, agentClass, args);
            ac.start();
            print(CCS.COLOR_GREEN, "<" + getName() + ">Launching " + agentName, true);
            return true;
        } catch (StaleProxyException ex) {
            print(CCS.COLOR_RED, "<" + getName() + ">Error launching " + agentName, true);
            return false;
        }
    }

    private class CountVideos extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.DOWNLOADED_OK));
            if (msg != null) {
                videosOk++;
            }
        }
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

    private class ApiKeyDispatcher extends CyclicBehaviour {

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.WANT_API));
            if (msg != null) {
                AID receiver = msg.getSender();
                Iterator<String> iter = pom.getApiKeys().iterator();
                String keyToSend = null;
                while (iter.hasNext()) {
                    String str = iter.next();
                    if (YoutubeDownloader.validKey(str)) {
                        keyToSend = str;
                        break;
                    }
                }

                if (keyToSend != null) {
                    print(CCS.COLOR_GREEN, "<" + getName() + ">Dispatching api key to " + receiver.getName(), true);
                    msg = new ACLMessage(CCS.TAKE_YOUR_API);
                    msg.addReceiver(receiver);
                    msg.setContent(keyToSend);
                    msg.setSender(myAgent.getAID());
                    myAgent.send(msg);
                    block();
                } else {
                    print(CCS.COLOR_RED, "<" + getName() + ">No valid key found.", true);
                    msg = new ACLMessage(CCS.KILL_YOURSELF);
                    msg.addReceiver(myAgent.getAID());
                    myAgent.send(msg);
                }

            } else {
                block();
            }
        }

    }

    private class AgentNumberChecker extends CyclicBehaviour {

        @Override
        public void action() {
            if (numberOfAgents(CCS.DOWNLOADER_DF) < pom.getNumberOfDownloaderAgents()) {
                int n = pom.getNumberOfDownloaderAgents() - numberOfAgents(CCS.DOWNLOADER_DF);
                for (int i = 0; i < n; i++) {
                    Object[] ob = {pom.getPropertiesObjDownloader()};
                    launchAgent(generateName(CCS.DOWNLOADER_DF), CCS.DOWNLOAD_AGENT_CLASS, ob);
                }
            }

            if (numberOfAgents(CCS.SEARCHER_DF) < pom.getNumberOfSearchAgents()) {
                int n = pom.getNumberOfSearchAgents() - numberOfAgents(CCS.SEARCHER_DF);
                for (int i = 0; i < n; i++) {
                    launchAgent(generateName(CCS.SEARCHER_DF), CCS.SEARCH_AGENT_CLASS, null);
                }
            }
            block(5000);

        }

    }

    private class QueryDispatcher extends Behaviour {

        ArrayList<Query> queries = new ArrayList<Query>();

        public QueryDispatcher() {
            for (String s : pom.getQuerys()) {
                queries.add(new Query(s));
            }
        }

        @Override
        public void action() {
            checkFailed();
            checkDone();
            dispatchQuery();
        }

        @Override
        public boolean done() {

            return queries.isEmpty();
        }

        @Override
        public int onEnd() {
            checker.block();
            myAgent.doDelete();
            return 0;
        }

        private void checkFailed() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.FAILED_QUERY));
            if (msg != null) {
                String st = msg.getContent();
                print(CCS.COLOR_RED, "<" + getName() + ">Query failed: " + st, true);

                Iterator<Query> iter = queries.iterator();
                while (iter.hasNext()) {
                    Query str = iter.next();

                    if (str.getQuery().equals(st)) {
                        str.setSearcher(null);
                    }
                }

            }
        }

        private void checkDone() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.COMPLETED_QUERY));
            if (msg != null) {
                String st = msg.getContent();
                print(CCS.COLOR_GREEN, "<" + getName() + ">Query completed: " + st, true);

                Iterator<Query> iter = queries.iterator();
                while (iter.hasNext()) {
                    Query str = iter.next();

                    if (str.getQuery().equals(st)) {
                        iter.remove();
                        wordsDownloaded.add(st);
                    }
                }
            }
        }

        private void dispatchQuery() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.WANT_QUERY));
            if (msg != null) {

                Iterator<Query> iter = queries.iterator();
                while (iter.hasNext()) {
                    Query str = iter.next();

                    if (str.getSearcher() == null) {
                        str.setSearcher(msg.getSender());
                        msg = new ACLMessage(CCS.TAKE_YOUR_QUERY);
                        msg.addReceiver(str.getSearcher());
                        msg.setSender(myAgent.getAID());
                        msg.setContent(str.getQuery());
                        myAgent.send(msg);
                        print(CCS.COLOR_GREEN, "<" + getName() + ">Sending query: " + str.getQuery(), true);
                        break;
                    }
                }

            }
        }

    }

    private class Query {

        private String query;
        private AID searcher;

        public Query(String query) {
            this.query = query;
            this.searcher = null;
        }

        public AID getSearcher() {
            return searcher;
        }

        public void setSearcher(AID searcher) {
            this.searcher = searcher;
        }

        public String getQuery() {
            return query;
        }

    }
}
