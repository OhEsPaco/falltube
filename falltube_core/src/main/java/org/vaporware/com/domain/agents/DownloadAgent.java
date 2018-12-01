package org.vaporware.com.domain.agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

public class DownloadAgent extends Agent {

    private PropertiesObjDownloader props;
    private ThreadedBehaviourFactory tbf;

    @Override
    protected void setup() {
        System.out.println("<" + getName() + ">Setting up");
        Object[] arguments = getArguments();
        props = (PropertiesObjDownloader) arguments[0];
        tbf = new ThreadedBehaviourFactory();
        addBehaviour(new DownloadBehaviour(this));

    }

    protected void takeDown() {
        System.out.println("<" + getName() + ">Taking down");
        tbf.interrupt();
    }

    private class DownloadBehaviour extends CyclicBehaviour {

        private YoutubeDownloader youtube;
        private long numberOfComments;

        public DownloadBehaviour(Agent a) {
            super(a);
        }

        @Override
        public void onStart() {
            this.youtube = new YoutubeDownloader(props.getApiKey(), props.getHost(), props.getPort(), props.getDatabase(), props.getUser(), props.getPassword(),props.getRegionCode());
            this.numberOfComments = props.getNumberOfComments();
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(Performatives.ID_FOR_DOWNLOADER));
            if (msg != null) {

                String content = msg.getContent();
                if (content != null) {
                    try {
                        System.out.println("<" + myAgent.getName() + ">Saving video id:" + content);
                        youtube.videoIdToSql(content, numberOfComments);
                    } catch (Exception ex) {
                       
                        System.out.println("<" + myAgent.getName() + ">Error saving video id:" + content);
                        try {
                            System.out.println("<" + myAgent.getName() + ">Saving video id:" + content + "[Retry]");
                            youtube.videoIdToSql(content, numberOfComments);
                        } catch (Exception ex1) {
                            System.out.println("<" + myAgent.getName() + ">Error saving video id:" + content+"[Retry]");
                        }

                    }
                    
                   
                }

            }
        }

    }
}
