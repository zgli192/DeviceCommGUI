package com.ford.vcc.cme.devicecomm.gui.protocol;

import javax.swing.JPanel;

import com.ford.vcc.cme.devicecomm.core.messaging.DelegateMessageSenderFactory;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;

/**
 * TODO Insert class description here.  <br/><br/>
 *
 * <pre>
 *
 *       Change log:
 *       ------ --------------- ------------ --------------------------------------------
 *       Ver    By              Date         Description
 *       ------ --------------- ------------ --------------------------------------------
 *       1.0    Patrik Lycke    2006 nov 1  Created.
 * </pre>
 */
public class RawSocketAdm extends JPanel{
    /**
     *
     */
    private static final long serialVersionUID = 1L;


    public RawSocketAdm() {
        super();
    }



    public MessageSenderFactory getMessageSenderFactory(){
        return new DelegateMessageSenderFactory();
    }
}
