package com.ford.vcc.cme.devicecomm.gui.protocol;

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
 *       1.0    Patrik Lycke    2006 okt 24  Created.
 * </pre>
 */
public class AddCharTermProtocolAdm extends BufferCharTermProtocolAdm {

    /**
     * @param messageHandler
     */
    public AddCharTermProtocolAdm(MessageHandler messageHandler) {
        super(messageHandler);

    }

    @Override
    protected BufferCharTermMessageSenderFactory getCharTermMessageSenderFactoryImpl() {
        return new AddCharTermMessageSenderFactory();
    }



}
