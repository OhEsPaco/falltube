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

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ThreadedBehaviourFactory;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.Font;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class UIAgent extends FalltubeAgent {

    private ThreadedBehaviourFactory tbf;
    private JTextPane txtPaneOutput;
    private JScrollPane scrollPane;

    @Override
    protected void setup() {
        tbf = new ThreadedBehaviourFactory();

        try {
            java.awt.EventQueue.invokeAndWait(new Runnable() {
                public void run() {
                    new UIAgentFrame().setVisible(true);
                }
            });
            registerAgent(CCS.UI_DF);
            addBehaviour(tbf.wrap(new Printer()));
        } catch (InterruptedException ex) {

            print(CCS.COLOR_RED, "<" + getName() + ">Failed to launch the interface.", true);
        } catch (InvocationTargetException ex) {

            print(CCS.COLOR_RED, "<" + getName() + ">Failed to launch the interface.", true);
        }

    }

    @Override
    protected void takeDown() {
        deregisterAgent();
        print(CCS.COLOR_RED, "<" + getName() + ">Taking down...", true);
        tbf.interrupt();
    }

    private class Printer extends CyclicBehaviour {

        private StyledDocument doc;
        private Style style;

        @Override
        public void onStart() {
            this.doc = txtPaneOutput.getStyledDocument();
            this.style = txtPaneOutput.addStyle("I'm a Style", null);
        }

        @Override
        public void action() {
            ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(CCS.UI_PRINT));
            if (msg != null) {
                StyleConstants.setForeground(style, stringToColor(msg.getLanguage()));
                try {
                    doc.insertString(doc.getLength(), msg.getContent() + "\n", style);
                    if (isViewAtBottom()) {
                        scrollToBottom();
                    }
                } catch (BadLocationException e) {
                }
                block();
            } else {
                block();
            }
        }

        private boolean isViewAtBottom() {
            JScrollBar sb = scrollPane.getVerticalScrollBar();
            int min = sb.getValue() + sb.getVisibleAmount();
            int max = sb.getMaximum();
            return (min + 200) >= max;
        }

        private void scrollToBottom() {
            SwingUtilities.invokeLater(
                    new Runnable() {
                public void run() {
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
                }
            });
        }

        private Color stringToColor(String str) {
            switch (str) {
                case CCS.COLOR_BLACK:
                    return Color.BLACK;
                case CCS.COLOR_RED:
                    return Color.RED;
                case CCS.COLOR_GREEN:
                    return new Color(25600);
                case CCS.COLOR_BLUE:
                    return Color.BLUE;
                case CCS.COLOR_MAGENTA:
                    return Color.MAGENTA;
                default:
                    return Color.BLACK;

            }
        }
    }

    private class UIAgentFrame extends JFrame {

        private JPanel mainPane;

        /**
         * Create the frame.
         */
        public UIAgentFrame() {
            setTitle("Falltube");
            setFont(new Font("Consolas", Font.PLAIN, 12));
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setBounds(100, 100, 450, 300);
            mainPane = new JPanel();
            mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
            setContentPane(mainPane);
            mainPane.setLayout(new BorderLayout(0, 0));

            scrollPane = new JScrollPane();
            mainPane.add(scrollPane, BorderLayout.CENTER);

            txtPaneOutput = new JTextPane();
            txtPaneOutput.setEditable(false);

            txtPaneOutput.setToolTipText("Output");
            txtPaneOutput.setFont(new Font("Consolas", Font.PLAIN, 11));
            scrollPane.setViewportView(txtPaneOutput);

        }

    }
}
