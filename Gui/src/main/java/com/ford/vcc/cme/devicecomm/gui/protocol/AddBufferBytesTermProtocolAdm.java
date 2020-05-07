package com.ford.vcc.cme.devicecomm.gui.protocol;

import com.ford.vcc.cme.devicecomm.core.common.DelegateSink;
import com.ford.vcc.cme.devicecomm.core.common.Sink;
import com.ford.vcc.cme.devicecomm.core.messaging.DelegateMessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSender;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.protocol.byteterm.common.AddLastBytesSink;
import com.ford.vcc.cme.devicecomm.protocol.byteterm.common.LastBytesTermSink;
import com.ford.vcc.cme.devicecomm.protocol.byteterm.common.RemoveLastBytesSink;
import com.ford.vcc.cme.devicecomm.protocol.charterm.messaging.AddCharTermMessageSenderFactory;
import com.ford.vcc.cme.devicecomm.protocol.charterm.messaging.BufferCharTermMessageSenderFactory;
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
 *       1.0    Patrik Lycke    2007 okt 16  Created.
 * </pre>
 */
public class AddBufferBytesTermProtocolAdm extends BufferBytesTermProtocolAdm {

    /**
     * @param messageHandler
     */
    public AddBufferBytesTermProtocolAdm(MessageHandler messageHandler) {
        super(messageHandler);

    }

    @Override
    public MessageSenderFactory getMessageSenderFactory() {
        return new MessageSenderFactory() {

            public Sink createEncodeSink(Sink sink) {
                AddLastBytesSink addLastBytesSink = new AddLastBytesSink();
                addLastBytesSink.setBytesToAdd(terminationBytes);
                addLastBytesSink.setSubSink(sink);
                return addLastBytesSink;
            }

            public Sink createEncodeSink() {
                return new DelegateSink();
            }

            public Sink createDecodeSink(Sink sink) {
                LastBytesTermSink bytesTermSink = new LastBytesTermSink();
                bytesTermSink.setLastBytesOfMessage(terminationBytes);


                RemoveLastBytesSink removeLastBytesSink = new RemoveLastBytesSink();
                removeLastBytesSink.setNoBytesToRemove(terminationBytes.length);


                bytesTermSink.setSubSink(removeLastBytesSink);
                removeLastBytesSink.setSubSink(sink);

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





