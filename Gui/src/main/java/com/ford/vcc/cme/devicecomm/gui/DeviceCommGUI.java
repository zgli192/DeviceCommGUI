package com.ford.vcc.cme.devicecomm.gui;

import java.awt.BorderLayout;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import com.ford.vcc.cme.devicecomm.core.common.ExceptionHandler;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.gui.datagram.DatagramComponent;
import com.ford.vcc.cme.devicecomm.gui.protocol.ProtocolsAdm;
import com.ford.vcc.cme.devicecomm.gui.socket.SocketComponent;
import com.ford.vcc.cme.devicecomm.gui.vcom.VCOMComponent;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;
import com.ford.vcc.ng.eie.swingutils.SystemConsole;

public class DeviceCommGUI extends JFrame implements ExceptionHandler, MessageHandler {

    private static final Logger log = Logger.getLogger(DeviceCommGUI.class.getName());

    private ExceptionHandler exceptionHandler = null;

    private MessageHandler messageHandler = null;

    private SystemConsole systemConsole = new SystemConsole();

    private JSplitPane splitPane;



    public DeviceCommGUI() {
        super("Comm GUI");
        exceptionHandler = this;
        messageHandler = this;

        JTabbedPane tabbedPane = createTabs();

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tabbedPane, systemConsole);
        splitPane.setOneTouchExpandable(true);

        this.getContentPane().add(splitPane, BorderLayout.CENTER);


        this.addWindowListener(SwingUtils.getSystemExitOnWindowClosing(this));

        SwingUtils.setFullScreen(this);
        SwingUtils.centerOnScreen(this);

        this.setVisible(true);




    }



    private JTabbedPane createTabs() {
        JTabbedPane tabbedPane = new JTabbedPane();



        //UDP Communication
        ProtocolsAdm protocolsAdmUDP = new ProtocolsAdm(messageHandler);
        MessageSenderFactory messageSenderFactoryUDP = protocolsAdmUDP.getProtocolFactory();
        tabbedPane.add("华鼎", new DatagramComponent(exceptionHandler, messageHandler, messageSenderFactoryUDP));
        tabbedPane.add("UDP-Protocols", protocolsAdmUDP);

        //TCP Communication
        ProtocolsAdm protocolsAdmTCP = new ProtocolsAdm(messageHandler);
        MessageSenderFactory messageSenderFactoryTCP = protocolsAdmTCP.getProtocolFactory();
        tabbedPane.add("TCP-Comm", new SocketComponent(exceptionHandler, messageHandler, messageSenderFactoryTCP));
        tabbedPane.add("TCP-Protocols", protocolsAdmTCP);


        //VCOM Communication
        ProtocolsAdm protocolsAdmVcom = new ProtocolsAdm(messageHandler);
        MessageSenderFactory messageSenderFactoryVcom = protocolsAdmVcom.getProtocolFactory();
        tabbedPane.add("VCOM-Comm", new VCOMComponent(exceptionHandler, messageHandler, messageSenderFactoryVcom));
        tabbedPane.add("VCOM-Protocols", protocolsAdmVcom);

        this.setLayout(new BorderLayout());
        return tabbedPane;
    }



    // //////////////
    // / General Stuff
    // ////////////



    private void showError(final String mess, final Throwable thr) {

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
//				JOptionPane.showMessageDialog(DeviceCommGUI.this, mess + "\n(" + thr.getMessage()
//						+ ")", "Error", JOptionPane.ERROR_MESSAGE);
                log.log(Level.WARNING, mess, thr);

            }

        });


    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information",
                JOptionPane.INFORMATION_MESSAGE);

    }

    public void handleException(String mess, Throwable trow) {
        showError(mess, trow);

    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new DeviceCommGUI();

    }

}
