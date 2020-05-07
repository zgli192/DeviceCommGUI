package com.ford.vcc.cme.devicecomm.gui.vcom;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.ford.vcc.cme.devicecomm.core.common.ExceptionHandler;
import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.ng.eie.swingutils.MessageHandler;

/**
 * TODO Insert class description here. <br/><br/>
 *
 * <pre>
 *
 *        Change log:
 *        ------ --------------- ------------ --------------------------------------------
 *        Ver    By              Date         Description
 *        ------ --------------- ------------ --------------------------------------------
 *        1.0    Patrik Lycke    2006 nov 6  Created.
 * </pre>
 */
public class VCOMComponent extends JPanel {

    public VCOMComponent(ExceptionHandler exceptionHandler, MessageHandler messageHandler,
                         MessageSenderFactory messageSenderFactory) {

        setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new ServerClientComponent(exceptionHandler,
                messageHandler, messageSenderFactory, "VCOM_1"), new ServerClientComponent(exceptionHandler, messageHandler,
                messageSenderFactory, "VCOM_2"));
        splitPane.setOneTouchExpandable(true);

        this.add(splitPane, BorderLayout.CENTER);

        this.addComponentListener(getComponentListener(splitPane));

    }

    private ComponentListener getComponentListener(final JSplitPane splitPane){
        return new ComponentAdapter() {

            boolean firstTime = true;

            public void componentResized(ComponentEvent e) {
                if(firstTime){
                    splitPane.setDividerLocation(0.5);
                }

            }



        };
    }

}
