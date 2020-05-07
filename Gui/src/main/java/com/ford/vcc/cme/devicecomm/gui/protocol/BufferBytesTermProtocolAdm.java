package com.ford.vcc.cme.devicecomm.gui.protocol;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ford.vcc.cme.devicecomm.core.common.DelegateSink;
import com.ford.vcc.cme.devicecomm.core.common.Sink;
import com.ford.vcc.cme.devicecomm.core.messaging.DelegateMessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.protocol.byteterm.common.LastBytesTermSink;
import com.ford.vcc.cme.devicecomm.protocol.charterm.messaging.BufferCharTermMessageSenderFactory;
import com.ford.vcc.ng.eie.common.util.TypeHelper;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;
import com.ford.vcc.ng.eie.swingutils.SwingUtils;

/**
 * TODO Insert class description here.  <br/><br/>
 *
 * <pre>
 *
 *       Change log:
 *       ------ --------------- ------------ --------------------------------------------
 *       Ver    By              Date         Description
 *       ------ --------------- ------------ --------------------------------------------
 *       1.0    Patrik Lycke    2007 okt 16  Created.
 * </pre>
 */
public class BufferBytesTermProtocolAdm  extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected byte[] terminationBytes = new byte[]{0};


    private MessageHandler messageHandler = null;

    public BufferBytesTermProtocolAdm(MessageHandler messageHandler) {
        this.messageHandler=messageHandler;


        this.setLayout(new BorderLayout());
        this.add(getEnterCharTermComponent(), BorderLayout.CENTER);

    }

    /**
     * @return
     */
    protected BufferCharTermMessageSenderFactory getCharTermMessageSenderFactoryImpl() {
        return new BufferCharTermMessageSenderFactory();
    }

    private JComponent getEnterCharTermComponent() {
        JPanel panel = new JPanel(new FlowLayout());

        final JTextField charTermInputTxtFld = new JTextField();

        charTermInputTxtFld.setPreferredSize(new Dimension(150, 20));




        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String ctStr = charTermInputTxtFld.getText();
                terminationBytes = TypeHelper.octetString2ByteArray(ctStr);



            }

        });

        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                charTermInputTxtFld.setText(TypeHelper.bytes2OctetString(terminationBytes));


            }

        });

        charTermInputTxtFld.setText(TypeHelper.bytes2OctetString(terminationBytes));

        panel.add(SwingUtils.flowWrap(new JLabel("Char Terminator"),
                charTermInputTxtFld, saveBtn, cancelBtn));

        return panel;
    }



    public MessageSenderFactory getMessageSenderFactory(){
        return new MessageSenderFactory() {

            public Sink createEncodeSink(Sink sink) {
                return new DelegateSink(sink);
            }

            public Sink createEncodeSink() {
                return new DelegateSink();
            }

            public Sink createDecodeSink(Sink sink) {
                LastBytesTermSink bytesTermSink = new LastBytesTermSink();
                bytesTermSink.setSubSink(sink);
                bytesTermSink.setLastBytesOfMessage(terminationBytes);
                return bytesTermSink;
            }

            public Sink createDecodeSink() {
                return new DelegateSink();
            }

            public MessageSender createEncodeMessageSender(MessageSender messagesender) {
                DelegateMessageSender delegateMessageSender = new DelegateMessageSender();
                delegateMessageSender.setDelegateSink(createEncodeSink(messagesender));
                delegateMessageSender.setDelegateChannel(messagesender);
                return delegateMessageSender;
            }

            public MessageSender createEncodeMessageSender() {
                DelegateMessageSender delegateMessageSender = new DelegateMessageSender();

                return delegateMessageSender;
            }

            public MessageSender createDecodeMessageSender(MessageSender messagesender) {
                DelegateMessageSender delegateMessageSender = new DelegateMessageSender();
                delegateMessageSender.setDelegateSink(createDecodeSink(messagesender));
                delegateMessageSender.setDelegateChannel(messagesender);
                return delegateMessageSender;
            }

            public MessageSender createDecodeMessageSender() {
                DelegateMessageSender delegateMessageSender = new DelegateMessageSender();

                return delegateMessageSender;
            }

        };
    }
}
