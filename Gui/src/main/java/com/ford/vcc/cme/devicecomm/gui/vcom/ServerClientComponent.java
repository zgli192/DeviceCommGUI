package com.ford.vcc.cme.devicecomm.gui.vcom;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import com.ford.vcc.cme.devicecomm.core.common.Channel;
import com.ford.vcc.cme.devicecomm.core.common.ExceptionHandler;
import com.ford.vcc.cme.devicecomm.core.common.MultiChannel;
import com.ford.vcc.cme.devicecomm.core.common.PullSourcePushSinkChannel;
import com.ford.vcc.cme.devicecomm.core.common.Sink;
import com.ford.vcc.cme.devicecomm.core.messaging.DelegateMessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageReceiver;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.gui.ClientIdProtocol;
import com.ford.vcc.cme.devicecomm.gui.ClientSession;
import com.ford.vcc.cme.devicecomm.transport.vcom.conversation.VCOMConversationClient;
import com.ford.vcc.cme.devicecomm.transport.vcom.conversation.VCOMConversationServer;
import com.ford.vcc.cme.devicecomm.transport.vcom.messaging.VCOMMessageReceiver;
import com.ford.vcc.cme.devicecomm.transport.vcom.messaging.VCOMMessageSender;
import com.ford.vcc.cme.devicecomm.transport.vcom.messaging.VCOMMessaging;
import com.ford.vcc.ng.eie.common.logging.Logger;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SentDataTextArea;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;

public class ServerClientComponent extends JPanel {

    private static final Logger logger = Logger.getLogger(ServerClientComponent.class);

    /**
     *
     */
    private static final long serialVersionUID = 1L;



    private JTextField serverHostTxtFld = new JTextField("");
    private JTextField serverPortTxtFld = new JTextField("");

    //	private JTextField serverHostTxtFld = new JTextField("md4r.gothenburg.vcc.ford.com");
//	private JTextField serverPortTxtFld = new JTextField("30262");
//
    private JTextField expediterNameTxtFld = new JTextField("VDE-OUT");

    private JTextField partnerNameTxtFld = new JTextField("VDP-OUT");

    private JRadioButton vcomDistributionRdBtn = new JRadioButton("Distribution", false);

    private JRadioButton vcomConversationRdBtn = new JRadioButton("Conversation", true);

    private SentDataTextArea clientIncomingTxtArea = new SentDataTextArea();

    private SentDataTextArea clientOutgoingTxtArea = new SentDataTextArea();

    private JTextField sentToClientTxtFld = new JTextField();

    private JComboBox clientSessionsCmbBx = new JComboBox();

    private ExceptionHandler exceptionHandler = null;

    private MessageHandler messageHandler = null;

    private MessageSenderFactory messageSenderFactory = null;

    private JCheckBox usePartnerChkBx = new JCheckBox("", true);

    private JCheckBox useExpediterChkBx = new JCheckBox("", true);

    private String componentName = "";

    public ServerClientComponent(ExceptionHandler exceptionHandler, MessageHandler messageHandler,
                                 MessageSenderFactory messageSenderFactory, String componentName) {

        this.exceptionHandler = exceptionHandler;
        this.messageHandler = messageHandler;
        this.messageSenderFactory = messageSenderFactory;
        this.componentName=componentName;

        this.setLayout(new BorderLayout());
        this.add(getClientComponent(), BorderLayout.CENTER);

        loadPreferences();

    }

    private void loadPreferences(){
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        serverHostTxtFld.setText(prefs.get(componentName+"_serverHost", ""));
        serverPortTxtFld.setText(prefs.get(componentName+"_serverPort", "0"));

        usePartnerChkBx.setSelected(prefs.getBoolean(componentName+"_usePartner", true));
        partnerNameTxtFld.setText(prefs.get(componentName+"_partnerName", ""));

        useExpediterChkBx.setSelected(prefs.getBoolean(componentName+"_useExpediter", true));
        expediterNameTxtFld.setText(prefs.get(componentName+"_expediterName", ""));

        if(prefs.getBoolean(componentName+"_CONS_MODE", true)){
            vcomConversationRdBtn.setSelected(true);
        } else{
            vcomDistributionRdBtn.setSelected(true);
        }


    }

    private void savePreferences(){
        Preferences prefs = Preferences.userNodeForPackage(this.getClass());

        prefs.put(componentName+"_serverHost", serverHostTxtFld.getText());
        prefs.put(componentName+"_serverPort", serverPortTxtFld.getText());

        prefs.putBoolean(componentName+"_usePartner", usePartnerChkBx.isSelected());
        prefs.put(componentName+"_partnerName", partnerNameTxtFld.getText());

        prefs.putBoolean(componentName+"_useExpediter", useExpediterChkBx.isSelected());
        prefs.put(componentName+"_expediterName", expediterNameTxtFld.getText());

        prefs.putBoolean(componentName+"_CONS_MODE", vcomConversationRdBtn.isSelected());
    }

    // //////////
    // / CLIENT
    // /////////

    private Component getClientComponent() {

        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "VCOM communication");

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

        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Recieved");
        panel.setBorder(border);
        panel.add(clientIncomingTxtArea);

        return panel;
    }

    private Component getClientSentTxtArea() {

        JPanel panel = new JPanel(new BorderLayout());

        Border border = BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Sent");
        panel.setBorder(border);
        panel.add(clientOutgoingTxtArea);

        return panel;
    }

    private JComponent getClientHeaderComponents() {
        JPanel panel = new JPanel(new GridLayout(1, 2));

        panel.add(getAddressComponent());
        panel.add(getClientSendAndDisconnect());

        return panel;
    }

    private JComponent getClientSendAndDisconnect() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(getClientDisconnect());
        panel.add(getClientSendComponent());
        return panel;
    }

    private JComponent getClientDisconnect() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.add(SwingUtils.flowWrap(clientSessionsCmbBx, getDisconnectButton()));
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

    private Component getAddressComponent() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(SwingUtils.flowWrap(getHostComponent(), getPortComponent(), getDistConvChoiceComponent()));
        panel.add(SwingUtils.flowWrap(getPartnerNameComponent(), getExpediterNameComponent(), getConnectComponent()));

        return panel;
    }

    private JComponent getConnectComponent() {
        JButton button = new JButton("Connect");
        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    int port = Integer.parseInt(serverPortTxtFld.getText());
                    boolean conversation = vcomConversationRdBtn.isSelected();
                    connectExpediter(serverHostTxtFld.getText(), port, expediterNameTxtFld.getText(), partnerNameTxtFld
                            .getText(), conversation);

                    savePreferences();
                } catch (NumberFormatException ne) {
                    exceptionHandler.handleException("Port must be of numeric value", ne);
                }

            }

        });

        return button;
    }

    private JComponent getDisconnectButton() {
        JButton button = new JButton("Disconnect");

        button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (clientSessionsCmbBx.getModel().getSize() < 1) {
                    messageHandler.showMessage("There is no connected clients");
                } else {
                    ClientSession cs = (ClientSession) clientSessionsCmbBx.getSelectedItem();

                    try {
                        cs.closeSession();
                    } catch (IOException e1) {
                        exceptionHandler.handleException("Exception occurred when closing client " + cs, e1);
                    }

                    clientSessionsCmbBx.removeItem(cs);
                }

            }

        });

        return button;
    }

    private void connectExpediter(String host, int port, String expediterName, String partnerName, boolean conversation) {

        try {
            boolean useExpediter = useExpediterChkBx.isSelected();
            boolean usePartner = usePartnerChkBx.isSelected();


            if (conversation) {

                // We have two things we want to open and close, both vcom
                // conversation and the pull/push-channel
                final MultiChannel multiChannel = new MultiChannel();

                // Adapt the conversation to a message sender
                DelegateMessageSender adaptedSender = new DelegateMessageSender();
                adaptedSender.setDelegateChannel(multiChannel);

                CloseChannelExceptionHandler closeChannelExceptionHandler = new CloseChannelExceptionHandler();
                String resources = "";

                if (useExpediter) {

                    VCOMConversationServer vcomServer = new VCOMConversationServer();
                    vcomServer.setServerHostName(host);
                    vcomServer.setServerPort(port);
                    vcomServer.setExpediterName(expediterName);
                    vcomServer.setTimeOutConnect(-1);
                    vcomServer.setTimeOutReceive(-1);
                    vcomServer.setSenderTag("");
                    vcomServer.setTrace(false);
                    vcomServer.setOpenImmediately(false);

                    vcomServer.setExceptionHandler(closeChannelExceptionHandler);

                    // Set up pulling the conversation and push to the channel
                    PullSourcePushSinkChannel pullSourcePushSinkChannel = new PullSourcePushSinkChannel();
                    pullSourcePushSinkChannel.setNoDataRaiseException(false);
                    pullSourcePushSinkChannel.setOnExceptionStopPulling(false);
                    pullSourcePushSinkChannel.setPullSource(vcomServer);
                    pullSourcePushSinkChannel.setPushSink(messageSenderFactory
                            .createDecodeSink(getClientMessageSink(expediterName)));
                    pullSourcePushSinkChannel.setReopenChannelAfterWrite(true);
                    pullSourcePushSinkChannel.setChannel(vcomServer);
                    //multiChannel.addChannel(vcomServer);
                    multiChannel.addChannel(pullSourcePushSinkChannel);


                    pullSourcePushSinkChannel.setExceptionHandler(closeChannelExceptionHandler);

                    resources += expediterName+" ";

                }

                if (usePartner) {

                    VCOMConversationClient vcomClient = new VCOMConversationClient();
                    vcomClient.setServerHostName(host);
                    vcomClient.setServerPort(port);
                    vcomClient.setPartnerName(partnerName);
                    vcomClient.setTimeOutConnect(-1);
                    vcomClient.setSenderTag("");
                    vcomClient.setTrace(false);

                    vcomClient.setExceptionHandler(closeChannelExceptionHandler);

                    multiChannel.addChannel(vcomClient);
                    adaptedSender.setDelegateSink(vcomClient);

                    resources += partnerName;


                }

                ClientSession clientSession = new ClientSession((resources), messageSenderFactory
                        .createEncodeMessageSender(adaptedSender));

                closeChannelExceptionHandler.setChannelLabel(expediterName);
                closeChannelExceptionHandler.setClientSession(clientSession);
                closeChannelExceptionHandler.setClientSessionsCmbBx(clientSessionsCmbBx);

                closeChannelExceptionHandler.setExceptionHandler(this.exceptionHandler);
                closeChannelExceptionHandler.setMessageHandler(this.messageHandler);


                new Thread(new Runnable() {

                    public void run() {
                        try {
                            multiChannel.open();
                        } catch (IOException e) {
                            exceptionHandler.handleException("Failed opening vcom server", e);
                        }

                    }

                }).start();

                clientSessionsCmbBx.addItem(clientSession);

            } else {


                Channel channelToOpen = null;
                MessageSender messageSender = null;

                String resources = "";



                if(usePartner && useExpediter){
                    VCOMMessaging vcomMessaging = new VCOMMessaging();
                    vcomMessaging.setRestartTrialsOnError(10);
                    vcomMessaging.setSecsBetweenRestartTrials(2);
                    vcomMessaging.setServerHostName(host);
                    vcomMessaging.setServerPort(port);
                    vcomMessaging.setReceivingSink(messageSenderFactory
                            .createDecodeSink(getClientMessageSink(expediterName)));

                    vcomMessaging.setPartnerName(partnerName);
                    resources += partnerName+" ";
                    vcomMessaging.setExpediterName(expediterName);
                    resources += expediterName;

                    channelToOpen = vcomMessaging;
                    messageSender = vcomMessaging;

                } else
                if(usePartner){
                    VCOMMessageSender vcomMessageSender = new VCOMMessageSender();
                    vcomMessageSender.setRestartTrialsOnError(10);
                    vcomMessageSender.setSecsBetweenRestartTrials(2);
                    vcomMessageSender.setServerHostName(host);
                    vcomMessageSender.setServerPort(port);
                    vcomMessageSender.setPartnerName(partnerName);
                    resources += partnerName+" ";
                    channelToOpen = vcomMessageSender;
                    messageSender=vcomMessageSender;

                } else
                if(useExpediter){
                    VCOMMessageReceiver vcomMessageReceiver = new VCOMMessageReceiver();
                    vcomMessageReceiver.setRestartTrialsOnError(10);
                    vcomMessageReceiver.setSecsBetweenRestartTrials(2);
                    vcomMessageReceiver.setServerHostName(host);
                    vcomMessageReceiver.setServerPort(port);
                    vcomMessageReceiver.setReceivingSink(messageSenderFactory
                            .createDecodeSink(getClientMessageSink(expediterName)));

                    vcomMessageReceiver.setExpediterName(expediterName);
                    resources += expediterName;
                    channelToOpen = vcomMessageReceiver;
                    messageSender=new DelegateMessageSender();
                }
                channelToOpen.open();

                clientSessionsCmbBx.addItem(new ClientSession(resources, messageSenderFactory
                        .createEncodeMessageSender(messageSender)));
            }

        } catch (IOException e) {
            exceptionHandler.handleException("Failed connecting client", e);
        }

    }

    private JComponent getDistConvChoiceComponent() {
        JPanel panel = new JPanel();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(vcomDistributionRdBtn);
        buttonGroup.add(vcomConversationRdBtn);

        panel.add(SwingUtils.flowWrap(vcomDistributionRdBtn, vcomConversationRdBtn));
        return panel;
    }

    private JComponent getExpediterNameComponent() {
        JPanel panel = new JPanel();

        useExpediterChkBx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expediterNameTxtFld.setEnabled(useExpediterChkBx.isSelected());
            }
        });

        expediterNameTxtFld.setPreferredSize(new Dimension(80, 20));
        panel.add(SwingUtils.flowWrap(useExpediterChkBx, new JLabel("Expediter Name"), expediterNameTxtFld));
        return panel;
    }

    private JComponent getPartnerNameComponent() {
        JPanel panel = new JPanel();

        usePartnerChkBx.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                partnerNameTxtFld.setEnabled(usePartnerChkBx.isSelected());
            }
        });

        partnerNameTxtFld.setPreferredSize(new Dimension(80, 20));
        panel.add(SwingUtils.flowWrap(usePartnerChkBx, new JLabel("Partner Name"), partnerNameTxtFld));
        return panel;
    }

    private JComponent getHostComponent() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("VCOM Server Host Name"));

        serverHostTxtFld.setPreferredSize(new Dimension(80, 20));

        panel.add(serverHostTxtFld);
        return panel;
    }

    private JComponent getPortComponent() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(new JLabel("VCOM Server Port"));

        serverPortTxtFld.setPreferredSize(new Dimension(80, 20));

        panel.add(serverPortTxtFld);
        return panel;
    }

    private Sink getClientMessageSink(String clientId) {
        Sink messageSink = new Sink() {

            public void write(byte[] data, int off, int len) throws IOException {
                String writeStr = new String(data, off, len);
                showClientMess(writeStr);
                logger.info(writeStr);

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
                clientIncomingTxtArea.append(mess + "\n");
            }
        };
        SwingUtilities.invokeLater(updateAComponent);

    }

    private void sentToClient(String mess) {

        if (clientSessionsCmbBx.getModel().getSize() < 1) {
            messageHandler.showMessage("No client are connected");
        }
        ClientSession cs = (ClientSession) clientSessionsCmbBx.getSelectedItem();

        try {
            cs.write(mess.getBytes());
            clientOutgoingTxtArea.append(cs + ": " + mess + "\n");

        } catch (IOException e) {
            exceptionHandler.handleException("Failed sending message to " + cs, e);
        }

    }

    private void showError(String mess, Throwable thr) {
        exceptionHandler.handleException(mess, thr);

    }

}
