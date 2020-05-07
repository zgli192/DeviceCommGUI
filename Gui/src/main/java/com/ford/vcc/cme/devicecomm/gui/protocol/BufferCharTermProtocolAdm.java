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

import com.ford.vcc.cme.devicecomm.core.messaging.MessageSenderFactory;
import com.ford.vcc.cme.devicecomm.protocol.charterm.messaging.BufferCharTermMessageSenderFactory;
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
public class BufferCharTermProtocolAdm extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private char charTerm = '?';
    private BufferCharTermMessageSenderFactory  charTermMessageSenderFactory = null;

    private MessageHandler messageHandler = null;

    public BufferCharTermProtocolAdm(MessageHandler messageHandler) {
        this.messageHandler=messageHandler;
        this.charTermMessageSenderFactory = getCharTermMessageSenderFactoryImpl();
        this.charTermMessageSenderFactory.setTermChar(charTerm);

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

        charTermInputTxtFld.setPreferredSize(new Dimension(20, 20));

        final JTextField charTermByteTxtFld = new JTextField();
        charTermByteTxtFld.setPreferredSize(new Dimension(20, 20));
        charTermByteTxtFld.setEditable(false);

        JButton saveBtn = new JButton("Save");
        JButton cancelBtn = new JButton("Cancel");

        saveBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String ctStr = charTermInputTxtFld.getText();
                ctStr = getUnEscapedString(ctStr);

                if(ctStr.length() == 1){
                    charTerm = ctStr.charAt(0);
                    charTermInputTxtFld.setText(getEscapedString(charTerm));
                    charTermByteTxtFld.setText(new Byte((byte)charTerm).toString());
                    charTermMessageSenderFactory.setTermChar(charTerm);
                } else{
                    messageHandler.showMessage("Only ONE character must be entered as terminator!");
                }

            }

        });

        cancelBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                charTermInputTxtFld.setText(getEscapedString(charTerm));
                charTermByteTxtFld.setText(new Byte((byte)charTerm).toString());

            }

        });

        charTermInputTxtFld.setText(getEscapedString(charTerm));
        charTermByteTxtFld.setText(new Byte((byte)charTerm).toString());

        panel.add(SwingUtils.flowWrap(new JLabel("Char Terminator"),
                charTermInputTxtFld, charTermByteTxtFld, saveBtn, cancelBtn));

        return panel;
    }

    private String getEscapedString(char chr) {

        switch (chr) {
            case '\t':
                return "\\t";
            case '\b':
                return "\\b";
            case '\n':
                return "\\n";
            case '\r':
                return "\\r";
            case '\f':
                return "\\f";
            case '\'':
                return "\\'";
            case '\"':
                return "\\\"";
            case '\\':
                return "\\\\";
            default:
                return Character.toString(chr);
        }

    }

    private String getUnEscapedString(String escStr) {

        return escStr.replace("\\t", "\t").replace("\\b", "\b").replace("\\n",
                "\n").replace("\\r", "\r").replace("\\f", "\f").replace("\\'",
                "\'").replace("\\\"", "\"").replace("\\\\", "\\");

    }

    public MessageSenderFactory getMessageSenderFactory(){
        return charTermMessageSenderFactory;
    }
}
