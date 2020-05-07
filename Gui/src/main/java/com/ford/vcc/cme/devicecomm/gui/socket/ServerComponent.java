package com.ford.vcc.cme.devicecomm.gui.socket;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Logger;

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
import com.ford.vcc.cme.devicecomm.transport.socket.server.messaging.SocketClientListener;
import com.ford.vcc.cme.devicecomm.transport.socket.server.messaging.SocketServer;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SentDataTextArea;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;


public class ServerComponent extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;



    static final Logger log = Logger.getLogger(ServerComponent.class
            .getName());

    private SocketServer serverSocket = null;


    private JTextField serverPortTxtFld = new JTextField("8880");

    private JTextField sentToClientTxtFld = new JTextField();
    private JComboBox clientSessionsCmbBx = new JComboBox();

    private SentDataTextArea serverIncomingTxtArea = new SentDataTextArea();

    private SentDataTextArea serverOutgoingTxtArea = new SentDataTextArea();



    private enum ConnState {
        DISCONNECTED, CONNECTING, CONNECTED
    };

    private ConnState connState = ConnState.DISCONNECTED;

    private ExceptionHandler exceptionHandler = null;
    private MessageHandler messageHandler = null;
    private MessageSenderFactory messageSenderFactory = null;

    public ServerComponent(ExceptionHandler exceptionHandler, MessageHandler messageHandler, MessageSenderFactory messageSenderFactory) {
        this.exceptionHandler = exceptionHandler;
        this.messageHandler=messageHandler;
        this.messageSenderFactory=messageSenderFactory;

        this.setLayout(new BorderLayout());
        this.add(getServerComponent(), BorderLayout.CENTER);
    }

    // //////////
    // / SERVER
    // /////////
    private JComponent getServerComponent() {
        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Server communication");
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(border);

        panel.add(getServerHeaderComponent(), BorderLayout.NORTH);

        JPanel txtAreasPanel = new JPanel(new GridLayout(1, 2));
        txtAreasPanel.add(getServerIncomingTxtArea());
        txtAreasPanel.add(getServerSentTxtArea());

        panel.add(txtAreasPanel, BorderLayout.CENTER);

        return panel;

    }
    private JPanel getServerHeaderComponent(){
        JPanel panel = new JPanel(new GridLayout(1,2));

        panel.add(getServerConnectionComponent());
        panel.add(getClientConnectionsComponent());

        return panel;

    }

    private JPanel getServerConnectionComponent() {
        JPanel buttonPane = new JPanel(new GridLayout(2,1));
        buttonPane.add(getServerPortField());
        buttonPane.add(SwingUtils.flowWrap(connectServerButton(), disconnectServerButton()));
        return buttonPane;
    }

    private JPanel getClientConnectionsComponent(){
        JPanel panel = new JPanel(new GridLayout(2,2));

        panel.add(SwingUtils.flowWrap(new JLabel("Selected client"),clientSessionsCmbBx));
        panel.add(SwingUtils.flowWrap(getDisconnectClientButton()));

        sentToClientTxtFld.setPreferredSize(new Dimension(200,20));

        panel.add(SwingUtils.flowWrap(sentToClientTxtFld));
        panel.add(SwingUtils.flowWrap(getSendToClientButton()));

        return panel;
    }

    private JComponent getDisconnectClientButton() {
        JButton button = new JButton("Disconnect");

        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ClientSession cs = (ClientSession) clientSessionsCmbBx.getSelectedItem();

                try {
                    cs.closeSession();
                } catch (IOException e1) {
                    exceptionHandler.handleException("Exception occurred when closing "+cs.toString(), e1);
                } finally{
                    clientSessionsCmbBx.removeItem(cs);
                }

            }

        });

        return button;
    }



    private JComponent getSendToClientButton(){
        JButton button = new JButton("Send");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                sendToClient(sentToClientTxtFld.getText());

            }

        });
        return button;
    }

    private void sendToClient(String mess){
        if(clientSessionsCmbBx.getModel().getSize() < 1){
            messageHandler.showMessage("No clients connected yet!");
        } else{

            ClientSession cs = (ClientSession) clientSessionsCmbBx.getSelectedItem();
            try {
                cs.write(mess.getBytes());
                serverOutgoingTxtArea.append(cs.toString()+": "+ mess+"\n");
            } catch (IOException e) {
                exceptionHandler.handleException("Failed writing to client", e);
            }
        }
    }

    private JComponent getServerPortField() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(new JLabel("Listening Port:"));
        serverPortTxtFld.setPreferredSize(new Dimension(80, 20));
        panel.add(serverPortTxtFld);
        return panel;
    }

    private JButton connectServerButton() {
        JButton button = new JButton("Start Listening");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {

                try {
                    int port = Integer.parseInt(serverPortTxtFld.getText());

                    connectServer(port);
                } catch (NumberFormatException e1) {
                    showError("Server port number must be valid integer", e1);
                }
            }

        });

        return button;
    }

    private void connectServer(int port){
        connState = ConnState.CONNECTING;
        try {

            if(serverSocket == null){
                serverSocket = new SocketServer();
                serverSocket.setSocketClientListener(getSocketClientListener());
            }
            serverSocket.setPort(port);
            serverSocket.open();



            connState = ConnState.CONNECTED;
        } catch (IOException e) {
            connState = ConnState.DISCONNECTED;
            showError("Failed connecting to port: " + port + " ("
                    + e.getMessage() + ")", e);
        }
    }

    private JButton disconnectServerButton() {
        JButton button = new JButton("Stop Listening");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                disconnect();
            }

        });

        return button;
    }



    private JComponent getServerIncomingTxtArea() {



        JPanel panel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Recieved");
        panel.setBorder(border);
        panel.add(serverIncomingTxtArea);

        return panel;

    }



    private JComponent getServerSentTxtArea() {


        JPanel panel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createTitledBorder(BorderFactory
                .createEtchedBorder(), "Sent");
        panel.setBorder(border);


        panel.add(serverOutgoingTxtArea, BorderLayout.CENTER);

        return panel;

    }








    private SocketClientListener getSocketClientListener(){
        return new SocketClientListener() {

            public void socketClientConnected(SocketClient socketClient) {
                clientConnected(socketClient);

            }

        };
    }

    private void clientConnected(SocketClient socketClient) {

        //try {
        int clientId = ClientIDProvider.provideClientId();

        socketClient.setReceivingSink(messageSenderFactory.createDecodeSink(getClientMessageSink(clientId)));
        socketClient.setExceptionHandler(exceptionHandler);
        socketClient.setWaitForPullEngineStartTimeMilliSecs(0);
        //socketClient.open();

        clientSessionsCmbBx.addItem(new ClientSession(clientId,  messageSenderFactory.createEncodeMessageSender(socketClient)));

//		} catch (IOException e) {
//			exceptionHandler.handleException("Failed connecting client", e);
//		}

    }

    private void disconnect() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            showError("Failed closing server. (" + e.getMessage() + ")", e);
        } finally {
            serverSocket = null;
            connState = ConnState.DISCONNECTED;
        }
    }

    private Sink getClientMessageSink(int clientId){
        Sink messageSink = new Sink() {

            public void write(byte[] data, int off, int len) throws IOException {
                showClientMess(new String(data, off, len));

            }

            public void write(byte[] data) throws IOException {
                write(data, 0, data.length);

            }

        };

        return new ClientIdProtocol(clientId, messageSink);
    }

    private synchronized void showClientMess(final String mess) {

        Runnable updateAComponent = new Runnable() {
            public void run() {
                serverIncomingTxtArea.append(mess + "\n");
            }
        };
        SwingUtilities.invokeLater(updateAComponent);

    }

    void showError(String mess, Throwable thr) {
        exceptionHandler.handleException(mess, thr);

    }

}
