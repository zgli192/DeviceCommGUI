package com.ford.vcc.cme.devicecomm.gui.vcom;

import java.io.IOException;

import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.ford.vcc.cme.devicecomm.core.common.Channel;
import com.ford.vcc.cme.devicecomm.core.common.ExceptionHandler;
import com.ford.vcc.cme.devicecomm.gui.ClientSession;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;

/**
 * TODO Insert class description here.  <br/><br/>
 *
 * <pre>
 *
 *       Change log:
 *       ------ --------------- ------------ --------------------------------------------
 *       Ver    By              Date         Description
 *       ------ --------------- ------------ --------------------------------------------
 *       1.0    Patrik Lycke    2007 jun 13  Created.
 * </pre>
 */
public class CloseChannelExceptionHandler implements ExceptionHandler{


    private MessageHandler messageHandler;
    private ExceptionHandler exceptionHandler;
    private ClientSession clientSession;
    private JComboBox clientSessionsCmbBx;
    private String channelLabel;


    public void setChannelLabel(String channelLabel) {
        this.channelLabel = channelLabel;
    }

    public void setMessageHandler(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }
    public void handleException(String mess, Throwable thr) {

        try {
            clientSession.closeSession();
        } catch (IOException e1) {
            exceptionHandler.handleException("Exception occurred when closing client " + clientSession, e1);
        }

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                clientSessionsCmbBx.removeItem(clientSession);

            }

        });


        messageHandler.showMessage("Due to exception ["+thr.getMessage()+"] the resource["+channelLabel+"] has been closed down.");

        exceptionHandler.handleException(mess, thr);

    }
    public void setExceptionHandler(ExceptionHandler exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    public void setClientSession(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    public void setClientSessionsCmbBx(JComboBox clientSessionsCmbBx) {
        this.clientSessionsCmbBx = clientSessionsCmbBx;
    }

}
