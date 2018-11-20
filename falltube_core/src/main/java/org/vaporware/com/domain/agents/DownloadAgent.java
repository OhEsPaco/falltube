/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaporware.com.domain.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.util.Logger;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

/**
 *
 * @author pacog
 */
public class DownloadAgent extends Agent {

    @Override
    protected void setup() {
        // Registration with the DF 
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("DownloadAgent");
        sd.setName(getName());
        sd.setOwnership("Vaporware");
        dfd.setName(getAID());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
            ThreadedBehaviourFactory tbf=new ThreadedBehaviourFactory();
            DownloadBehaviour dBehaviour = new DownloadBehaviour(this);
            addBehaviour(tbf.wrap(dBehaviour));
        } catch (FIPAException e) {
            doDelete();
        }
    }

    private class DownloadBehaviour extends CyclicBehaviour {

        // YoutubeDownloader(String apiKey, String host, int port, String database, String user, String password)
        private YoutubeDownloader youtube;
        private int numberOfComments;

        public DownloadBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void onStart() {
            MessageTemplate performativa = MessageTemplate.MatchPerformative(Performatives.DATA_FOR_DOWNLOADER);
            ACLMessage msg = myAgent.blockingReceive(performativa);
            String[] s = msg.getContent().split("\\s+");
            this.youtube = new YoutubeDownloader(s[5], s[0], Integer.parseInt(s[1]), s[2], s[3], s[4]);
            this.numberOfComments = Integer.parseInt(s[6]);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null) {

                if (msg.getPerformative() == Performatives.ID_FOR_DOWNLOADER) {
                    String content = msg.getContent();
                    if (content != null) {
                        try {
                            youtube.videoIdToSql(content, numberOfComments);
                        } catch (Exception ex) {
                            System.out.println("<" + getName() + ">Error saving video id:" + content);
                        }
                    }

                }
                block();
            } else {
                block();
            }
        }

    }
}
