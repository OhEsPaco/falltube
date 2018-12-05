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
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import static org.vaporware.com.domain.agents.ConstantsClass.MAX_RETRIES;
import static org.vaporware.com.domain.agents.ConstantsClass.MS_WAIT_ON_RETRY;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

public class DownloadAgent extends Agent {

    private PropertiesObjDownloader props;
    private AID uiAid;

    @Override
    protected void setup() {
        uiAid = new AID(ConstantsClass.UI_AGENT_NAME, AID.ISLOCALNAME);
        sendToUI(ConstantsClass.COLOR_MAGENTA, "<" + getName() + ">Setting up...");
        System.out.println("<" + getName() + ">Setting up...");
        Object[] arguments = getArguments();
        props = (PropertiesObjDownloader) arguments[0];
        addBehaviour(new DownloadBehaviour(this));

    }

    private void sendToUI(String color, String msg) {
        if(uiAid!=null){
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
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ConstantsClass.ID_FOR_DOWNLOADER));
            if (msg != null) {

                String content = msg.getContent();
                if (content != null) {
                    try {
                        sendToUI(ConstantsClass.COLOR_GREEN, "<" + myAgent.getName() + ">Saving video id:" + content);

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
                            sendToUI(ConstantsClass.COLOR_GREEN, "<" + myAgent.getName() + ">Changing key...");

                            this.youtube = new YoutubeDownloader(apiKeys.get(actualKey), props.getHost(), props.getPort(), props.getDatabase(), props.getUser(), props.getPassword(), props.getRegionCode());
                        } else {
                            salir = true;
                        }

                    } catch (SQLException ex) {
                        sendToUI(ConstantsClass.COLOR_RED, "<" + myAgent.getName() + ">Error saving video id:" + content);
                        System.out.println("<" + myAgent.getName() + ">Error saving video id:" + content);
                    } catch (Exception exc) {
                       
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
            ACLMessage msg = new ACLMessage(ConstantsClass.DOWNLOADER_DOWN);
            for (AID s : searchers) {
                msg.addReceiver(s);
            }
            msg.setSender(myAgent.getAID());
            myAgent.send(msg);
            sendToUI(ConstantsClass.COLOR_GREEN, "<" + myAgent.getName() + ">Terminating downloader...");
            System.out.println("<" + myAgent.getName() + ">Terminating downloader...");
            return 0;
        }

    }
}
