package com.ford.vcc.cme.devicecomm.gui.socket;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.ford.vcc.cme.devicecomm.core.common.ExceptionHandler;
import com.ford.vcc.cme.devicecomm.core.common.Sink;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.gui.ClientIDProvider;
import com.ford.vcc.cme.devicecomm.gui.ClientIdProtocol;
import com.ford.vcc.cme.devicecomm.gui.ClientSession;
import com.ford.vcc.cme.devicecomm.transport.socket.client.messaging.SocketClient;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SentDataTextArea;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;

public class ClientComponent extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JTextField clientPortTxtFld = new JTextField("8888");

    private JTextField clientHostTxtFld = new JTextField("localhost");

    private SentDataTextArea clientIncomingTxtArea = new SentDataTextArea();

    private SentDataTextArea clientOutgoingTxtArea = new SentDataTextArea();

    private JTextField sentToClientTxtFld = new JTextField();

    private JComboBox clientSessionsCmbBx = new JComboBox();

    private ExceptionHandler exceptionHandler = null;

    private MessageHandler messageHandler = null;

    private MessageSenderFactory messageSenderFactory = null;

    public ClientComponent(ExceptionHandler exceptionHandler,
                           MessageHandler messageHandler, MessageSenderFactory messageSenderFactory) {

        this.exceptionHandler = exceptionHandler;
        this.messageHandler = messageHandler;
        this.messageSenderFactory=messageSenderFactory;

        this.setLayout(new BorderLayout());
        this.add(getClientComponent(), BorderLayout.CENTER);

    }

    // //////////
    // / CLIENT
    // /////////

    private Component getClientComponent() {

        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Client communication");

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(border);
        panel.add(getClientHeaderComponents(), BorderLayout.NORTH);

        JPanel txtAreasPanel = new JPanel(new GridLayout(1, 2));
        txtAreasPanel.add(getClientRecievedTxtArea());
        txtAreasPanel.add(getClientSentTxtArea());

        panel.add(txtAreasPanel, BorderLayout.CENTER);

        return panel;
    }

    private Component getClientRecievedTxtArea() {


        JPanel panel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Recieved");
        panel.setBorder(border);
        panel.add(clientIncomingTxtArea);

        return panel;
    }

    private Component getClientSentTxtArea() {



        JPanel panel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Sent");
        panel.setBorder(border);
        panel.add(clientOutgoingTxtArea);

        return panel;
    }

    private JComponent getClientHeaderComponents(){
        JPanel panel = new JPanel(new GridLayout(1,2));


        panel.add(getClientAddressComponent());
        panel.add(getClientSendAndDisconnect());

        return panel;
    }

    private JComponent getClientSendAndDisconnect() {
        JPanel panel = new JPanel(new GridLayout(2,1));
        panel.add(getClientDisconnect());
        panel.add(getClientSendComponent());
        return panel;
    }

    private JComponent getClientDisconnect(){
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(SwingUtils.flowWrap(clientSessionsCmbBx, getDisconnectClientButton()));
        return panel;
    }

    private Component getClientSendComponent() {
        JPanel panel = new JPanel(new FlowLayout());

        Dimension dim = new Dimension();
        dim.setSize(200, 20);
        sentToClientTxtFld.setPreferredSize(dim);
        panel.add(sentToClientTxtFld);

        JButton sendToClient = new JButton("Send");
        sendToClient.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    sentToClient(sentToClientTxtFld.getText());
                } catch (NumberFormatException ex) {
                    showError("Client socket must be a valid numeric value", ex);
                }

            }

        });

        panel.add(sendToClient);

        return panel;

    }

    private Component getClientAddressComponent() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(SwingUtils.flowWrap(getClientPortComponent(),getClientHostComponent()));
        panel.add(SwingUtils.flowWrap(getClientConnectComponent()));

        return panel;
    }

    private JComponent getClientConnectComponent() {
        JButton button = new JButton("Connect");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(clientPortTxtFld.getText());
                    connectClient(clientHostTxtFld.getText(), port);
                } catch (NumberFormatException ne) {
                    exceptionHandler.handleException(
                            "Port must be of numeric value", ne);
                }

            }

        });

        return button;
    }

    private JComponent getDisconnectClientButton(){
        JButton button = new JButton("Disconnect");

        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(clientSessionsCmbBx.getModel().getSize() < 1){
                    messageHandler.showMessage("There is no connected clients");
                } else{
                    ClientSession cs = (ClientSession) clientSessionsCmbBx.getSelectedItem();

                    try {
                        cs.closeSession();
                    } catch (IOException e1) {
                        exceptionHandler.handleException("Exception occurred when closing client "+cs, e1);
                    }

                    clientSessionsCmbBx.removeItem(cs);
                }

            }

        });

        return button;
    }

    private void connectClient(String host, int port) {

        try {
            int clientId = ClientIDProvider.provideClientId();

            SocketClient asyncSocketClient = new SocketClient();
            asyncSocketClient.setRemoteHost(host);
            asyncSocketClient.setRemotePort(port);
            asyncSocketClient.setReceivingSink(messageSenderFactory.createDecodeSink(getClientMessageSink(clientId)));
            asyncSocketClient.setExceptionHandler(exceptionHandler);
            asyncSocketClient.setSleepBetweenReadsMilliSecs(5);
            asyncSocketClient.open();

            clientSessionsCmbBx.addItem(new ClientSession(clientId,  messageSenderFactory.createEncodeMessageSender(asyncSocketClient)));

        } catch (IOException e) {
            exceptionHandler.handleException("Failed connecting client", e);
        }

    }

    private JComponent getClientHostComponent() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Client host"));

        clientHostTxtFld.setPreferredSize(new Dimension(80, 20));

        panel.add(clientHostTxtFld);
        return panel;
    }

    private JComponent getClientPortComponent() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Client port"));

        clientPortTxtFld.setPreferredSize(new Dimension(80, 20));

        panel.add(clientPortTxtFld);
        return panel;
    }


    private Sink getClientMessageSink(int clientId) {
        Sink messageSink = new Sink() {

            public void write(byte[] data, int off, int len) throws IOException {
                showClientMess(new String(data, off, len));

            }

            public void write(byte[] data) throws IOException {
                write(data, 0, data.length);

            }

        };

        return new ClientIdProtocol(clientId,messageSink);
    }

    private synchronized void showClientMess(final String mess) {

        Runnable updateAComponent = new Runnable() {
            public void run() {
                clientIncomingTxtArea.append(mess + "\n");
            }
        };
        SwingUtilities.invokeLater(updateAComponent);

    }

    private void sentToClient(String mess) {


        if (clientSessionsCmbBx.getModel().getSize() < 1) {
            messageHandler.showMessage("No client are connected");
        }
        ClientSession cs = (ClientSession) clientSessionsCmbBx
                .getSelectedItem();

        try {
            cs.write(mess.getBytes());
            clientOutgoingTxtArea.append(cs+": "+mess+"\n");

        } catch (IOException e) {
            exceptionHandler.handleException("Failed sending message to " + cs,
                    e);
        }

    }



    private void showError(String mess, Throwable thr) {
        exceptionHandler.handleException(mess, thr);

    }

}
