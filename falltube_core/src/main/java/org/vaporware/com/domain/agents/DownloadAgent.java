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
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.sql.SQLException;
import org.vaporware.com.domain.exceptions.AlreadyExistsException;
import org.vaporware.com.domain.exceptions.ImpossibleToCreateTable;
import org.vaporware.com.domain.objects.PropertiesObjDownloader;
import org.vaporware.com.domain.youtube.YoutubeDownloader;

public class DownloadAgent extends FalltubeAgent {

    private PropertiesObjDownloader props;
    private ThreadedBehaviourFactory tbf;

    @Override
    protected void setup() {

        try {
            Object[] arguments = getArguments();
            props = (PropertiesObjDownloader) arguments[0];
            tbf = new ThreadedBehaviourFactory();
            if (props == null) {
                doDelete();
                print(CCS.COLOR_RED, "<" + getName() + ">Error setting up downloader", true);
            } else {
                print(CCS.COLOR_MAGENTA, "<" + getName() + ">Setting up downloader", true);
                registerAgent(CCS.DOWNLOADER_DF);
                addBehaviour(new DownloadBehaviourNew());
                addBehaviour(tbf.wrap(new Die()));
            }
        } catch (Exception e) {
            doDelete();
            print(CCS.COLOR_RED, "<" + getName() + ">Error setting up downloader", true);
        }

    }

    @Override
    protected void takeDown() {
        tbf.interrupt();
        deregisterAgent();
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

    private class DownloadBehaviourNew extends Behaviour {

        private String apiKey;
        private YoutubeDownloader youtube;
        private boolean end = false;

        @Override
        public void onStart() {
            youtube = null;
            apiKey = null;

        }

        @Override
        public void action() {

            if (youtube != null) {

                if (youtube.isTableOnDatabase()) {
                    ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.ID_FOR_DOWNLOADER));
                    if (msg != null) {
                        String content = msg.getContent();
                        if (content != null) {
                            try {
                                boolean alreadyExists = youtube.videoIdToSql(content);
                                if (!alreadyExists) {
                                    ACLMessage videoOk = new ACLMessage(CCS.DOWNLOADED_OK);
                                    videoOk.setSender(myAgent.getAID());
                                    sendToAll(CCS.MANAGEMENT_DF, videoOk);
                                }
                            } catch (IOException e) {

                                //Ponemos otra vez el id a la cola
                                myAgent.send(msg);
                                apiKey = null;
                                youtube = null;

                            } catch (SQLException ex) {
                                print(CCS.COLOR_RED, "<" + myAgent.getName() + ">Error saving video id:" + content, true);
                            } catch (AlreadyExistsException po) {
                                //print(CCS.COLOR_RED, "<" + myAgent.getName() + ">Already exists in database: " + content, true);
                            } catch (Exception exc) {

                            }

                        }
                        block();
                    } else {
                        block();
                    }
                } else {
                    try {
                        youtube.createTable();
                    } catch (ImpossibleToCreateTable ex) {
                        if (!youtube.isTableOnDatabase()) {
                            print(CCS.COLOR_RED, "<" + myAgent.getName() + ">Can't create table.", true);
                            ACLMessage msg = new ACLMessage(CCS.KILL_YOURSELF);
                            for (DFAgentDescription df : getAgents(CCS.MANAGEMENT_DF)) {
                                msg.addReceiver(df.getName());
                            }
                            myAgent.send(msg);
                        }

                    }
                }

            } else {
                //Want api and youtube
                AID manager = randomAgent(CCS.MANAGEMENT_DF);
                if (manager != null) {
                    ACLMessage msg = new ACLMessage(CCS.WANT_API);
                    msg.addReceiver(manager);
                    msg.setSender(myAgent.getAID());
                    myAgent.send(msg);
                    print(CCS.COLOR_RED, "<" + getName() + ">Waiting for API...", true);
                    ACLMessage apiMsg = myAgent.blockingReceive(MessageTemplate.MatchPerformative(CCS.TAKE_YOUR_API));
                    apiKey = apiMsg.getContent();
                    youtube = new YoutubeDownloader(apiKey, props.getHost(), props.getPort(), props.getDatabase(), props.getUser(), props.getPassword(), props.getRegionCode());
                    try {
                        youtube.tryDatabase();
                    } catch (SQLException ex) {
                        print(CCS.COLOR_RED, "<" + myAgent.getName() + ">Error accesing database.", true);
                        end = true;
                    }
                } else {
                    myAgent.doDelete();
                }
            }
        }

        @Override
        public boolean done() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.KILL_YOURSELF));
            if (msg != null || end == true) {

                return true;
            } else {
                return false;
            }
        }

        @Override
        public int onEnd() {
            myAgent.doDelete();
            return 0;
        }

    }

}
