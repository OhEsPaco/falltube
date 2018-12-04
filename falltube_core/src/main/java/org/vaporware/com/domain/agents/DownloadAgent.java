package org.vaporware.com.domain.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.vaporware.com.domain.agents.Performatives.MAX_RETRIES;
import static org.vaporware.com.domain.agents.Performatives.MS_WAIT_ON_RETRY;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

public class DownloadAgent extends Agent {

    private PropertiesObjDownloader props;

    @Override
    protected void setup() {
        System.out.println("<" + getName() + ">Setting up...");
        Object[] arguments = getArguments();
        props = (PropertiesObjDownloader) arguments[0];

        addBehaviour(new DownloadBehaviour(this));

    }

    @Override
    protected void takeDown() {
        System.out.println("<" + getName() + ">Taking down...");

    }

    private class DownloadBehaviour extends Behaviour {

        private YoutubeDownloader youtube;
        private long numberOfComments;
        private ArrayList<String> apiKeys;
        private int actualKey;
        private int actualRun;
        private boolean salir;
        private ArrayList<AID> searchers = new ArrayList<AID>();

        public DownloadBehaviour(Agent a) {
            super(a);
            apiKeys = props.getApiKeys();
        }

        @Override
        public void onStart() {
            this.actualKey = 0;
            this.actualRun = 0;
            this.salir = false;
            for (String s : props.getSearchers()) {
                searchers.add(new AID(s, AID.ISLOCALNAME));
            }
            this.youtube = new YoutubeDownloader(apiKeys.get(actualKey), props.getHost(), props.getPort(), props.getDatabase(), props.getUser(), props.getPassword(), props.getRegionCode());
            this.numberOfComments = props.getNumberOfComments();
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(Performatives.ID_FOR_DOWNLOADER));
            if (msg != null) {

                String content = msg.getContent();
                if (content != null) {
                    try {
                        System.out.println("<" + myAgent.getName() + ">Saving video id:" + content);
                        youtube.videoIdToSql(content, numberOfComments);
                    } catch (IOException e) {
                       
                        //Ponemos otra vez el id a la cola
                        myAgent.send(msg);
                        if (actualRun < MAX_RETRIES) {
                            if (actualKey >= apiKeys.size() - 1) {
                                actualKey = 0;
                                actualRun++;
                                block(MS_WAIT_ON_RETRY);
                            } else {
                                actualKey++;
                            }
                            System.out.println("<" + myAgent.getName() + ">Changing key...");
                            this.youtube = new YoutubeDownloader(apiKeys.get(actualKey), props.getHost(), props.getPort(), props.getDatabase(), props.getUser(), props.getPassword(), props.getRegionCode());
                        } else {
                            salir = true;
                        }

                    } catch (SQLException ex) {
                        System.out.println("<" + myAgent.getName() + ">Error saving video id:" + content);
                    } catch (Exception exc) {
                        System.out.println("<" + myAgent.getName() + ">Unknown error.");
                    }

                }
                block();
            } else {
                block();
            }
        }

        @Override
        public boolean done() {

            return salir;
        }

        public int onEnd() {
            ACLMessage msg = new ACLMessage(Performatives.DOWNLOADER_DOWN);
            for (AID s : searchers) {
                msg.addReceiver(s);
            }
            msg.setSender(myAgent.getAID());
            myAgent.send(msg);
            System.out.println("<" + myAgent.getName() + ">Terminating downloader...");
            return 0;
        }

    }
}
