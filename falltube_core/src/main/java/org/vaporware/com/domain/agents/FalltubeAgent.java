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
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

public abstract class FalltubeAgent extends Agent {

    protected void print(String color, String msg, boolean console) {
        DFAgentDescription[] uiagents = getAgents(CCS.UI_DF);
        ACLMessage msgUi = new ACLMessage(CCS.UI_PRINT);
        msgUi.setSender(getAID());
        msgUi.setLanguage(color);
        msgUi.setContent(msg);
        for (DFAgentDescription df : uiagents) {
            msgUi.addReceiver(df.getName());
        }

        if (uiagents.length > 0) {
            send(msgUi);
        }

        if (console) {
            System.out.println(msg);
        }

    }

    protected void registerAgent(String type) {
        //Registration within the DF
        ServiceDescription sd = new ServiceDescription();
        sd.setType(type);
        sd.setName(getLocalName());
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    protected void deregisterAgent() {
        try {
            DFService.deregister(this);
        } catch (Exception e) {
        }
    }

    protected DFAgentDescription[] getAgents(String type) {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);

            return DFService.search(this, dfd);

        } catch (FIPAException ex) {
            return new DFAgentDescription[0];
        }
    }

    protected AID randomAgent(String type) {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, dfd);
            if (result.length > 0) {
                return result[random(0, result.length - 1)].getName();
            } else {
                return null;
            }
        } catch (FIPAException ex) {
            return null;
        }
    }

    protected int sendToAll(String type, ACLMessage msg) {
        DFAgentDescription[] agents = getAgents(type);
        for (DFAgentDescription df : agents) {
            msg.addReceiver(df.getName());
        }
        if (agents.length != 0) {
            send(msg);
        }
        return agents.length;
    }

    protected int numberOfAgents(String type) {
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType(type);
            dfd.addServices(sd);

            DFAgentDescription[] result = DFService.search(this, dfd);
            return result.length;
        } catch (FIPAException ex) {
            return 0;
        }
    }

    protected int random(int Min, int Max) {
        return Min + (int) (Math.random() * ((Max - Min) + 1));
    }

}
