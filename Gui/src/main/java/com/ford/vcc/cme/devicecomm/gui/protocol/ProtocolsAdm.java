package com.ford.vcc.cme.devicecomm.gui.protocol;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.ford.vcc.cme.devicecomm.core.common.Sink;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;

/**
 * TODO Insert class description here. <br/><br/>
 *
 * <pre>
 *
 *         Change log:
 *         ------ --------------- ------------ --------------------------------------------
 *         Ver    By              Date         Description
 *         ------ --------------- ------------ --------------------------------------------
 *         1.0    Patrik Lycke    2006 okt 24  Created.
 * </pre>
 */
public class ProtocolsAdm extends JPanel {
    private MessageHandler messageHandler;

    private MessageSenderFactory messageSenderFactory = null;

    private List<ProtocolItem> pItems = null;

    private JPanel protocolPanel = null;

    public ProtocolsAdm(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.setLayout(new BorderLayout());
        this.add(getProtocolPanel(), BorderLayout.CENTER);
        this.add(SwingUtils.flowWrap(getChooseProtocolComponent()), BorderLayout.NORTH);


    }

    /**
     * @return
     */
    private Component getProtocolPanel() {
        protocolPanel = new JPanel(new CardLayout());

        for (ProtocolItem protocolItem : getProtocolItems())  {
            protocolPanel.add(protocolItem.comp, protocolItem.protocolName);
        }

        return protocolPanel;
    }

    private List<ProtocolItem> getProtocolItems() {

        if (pItems == null) {
            pItems = new ArrayList<ProtocolItem>();

            RawSocketAdm rawSocketProtocol = new RawSocketAdm();
            pItems.add(new ProtocolItem("Raw/No protocol",
                    rawSocketProtocol, rawSocketProtocol.getMessageSenderFactory()));

            BufferCharTermProtocolAdm bufferCharTerm = new BufferCharTermProtocolAdm(
                    messageHandler);
            pItems.add(new ProtocolItem("Buffer character terminator protocol", bufferCharTerm,
                    bufferCharTerm.getMessageSenderFactory()));

            AddCharTermProtocolAdm addCharTermProtocol = new AddCharTermProtocolAdm(
                    messageHandler);
            pItems.add(new ProtocolItem("Add/Remove character terminator",
                    addCharTermProtocol, addCharTermProtocol.getMessageSenderFactory()));

            BufferBytesTermProtocolAdm bufferBytesTermProtocolAdm = new BufferBytesTermProtocolAdm(messageHandler);
            pItems.add(new ProtocolItem("Buffer bytes terminator protocol", bufferBytesTermProtocolAdm,
                    bufferBytesTermProtocolAdm.getMessageSenderFactory()));

            AddBufferBytesTermProtocolAdm addBufferBytesTermProtocolAdm = new AddBufferBytesTermProtocolAdm(messageHandler);
            pItems.add(new ProtocolItem("Add/Remove bytes terminator protocol", addBufferBytesTermProtocolAdm,
                    addBufferBytesTermProtocolAdm.getMessageSenderFactory()));



            setSelectedProtocolItem(pItems.get(0));
        }
        return pItems;
    }

    private void setSelectedProtocolItem(ProtocolItem protocolItem) {

        boolean firstProtocol = messageSenderFactory == null;
        messageSenderFactory = protocolItem.messageSenderFactory;
        CardLayout cl = (CardLayout) protocolPanel.getLayout();

        cl.show(protocolPanel, protocolItem.protocolName);

        if(!firstProtocol){
            messageHandler
                    .showMessage("The new protocol will only be used for new connections!");
        }

    }

    /**
     * @return
     */
    private JComponent getChooseProtocolComponent() {
        final JComboBox protocolsCmbBx = new JComboBox();

        BufferCharTermProtocolAdm firstProtocol = new BufferCharTermProtocolAdm(
                messageHandler);

        for (ProtocolItem protocolItem : getProtocolItems()) {
            protocolsCmbBx.addItem(protocolItem);
        }

        protocolsCmbBx.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                ProtocolItem protocolItem = (ProtocolItem) protocolsCmbBx
                        .getSelectedItem();
                setSelectedProtocolItem(protocolItem);

            }
        });

        return protocolsCmbBx;
    }

    private class ProtocolItem {
        String protocolName;

        JComponent comp;

        MessageSenderFactory messageSenderFactory;

        private ProtocolItem(String protocolName, JComponent comp,
                             MessageSenderFactory messageSenderFactory) {
            this.protocolName = protocolName;
            this.comp = comp;
            this.messageSenderFactory = messageSenderFactory;

        }

        @Override
        public String toString() {
            return protocolName;
        }

    }

    public MessageSenderFactory getProtocolFactory() {
        return new MessageSenderFactory() {

            public MessageSender createDecodeMessageSender() {
                return messageSenderFactory.createDecodeMessageSender();
            }

            public MessageSender createDecodeMessageSender(MessageSender subSender) {
                return messageSenderFactory.createDecodeMessageSender(subSender);
            }

            public MessageSender createEncodeMessageSender() {
                return messageSenderFactory.createEncodeMessageSender();
            }

            public MessageSender createEncodeMessageSender(MessageSender subSender) {
                return messageSenderFactory.createEncodeMessageSender(subSender);
            }

            public Sink createDecodeSink() {
                return messageSenderFactory.createDecodeSink();
            }

            public Sink createDecodeSink(Sink subSink) {
                return messageSenderFactory.createDecodeSink(subSink);
            }

            public Sink createEncodeSink() {
                return messageSenderFactory.createEncodeSink();
            }

            public Sink createEncodeSink(Sink subSink) {
                return messageSenderFactory.createEncodeSink(subSink);
            }



        };
    }

}
