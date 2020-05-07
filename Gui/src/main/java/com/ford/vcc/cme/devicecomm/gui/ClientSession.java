package com.ford.vcc.cme.devicecomm.gui;

import java.io.IOException;

import com.ford.vcc.cme.devicecomm.core.messaging.MessageSender;

public class ClientSession {
    private String clientId;



    private MessageSender messageSender;

    public ClientSession(Integer clientId, MessageSender messageSender) {
        this.clientId = clientId.toString();
        this.messageSender = messageSender;
    }

    public ClientSession(String clientId, MessageSender messageSender) {
        this.clientId = clientId;
        this.messageSender = messageSender;
    }

    @Override
    public String toString() {
        return "Client ID: " + clientId;
    }

    public void closeSession() throws IOException {
        messageSender.close();

    }

    /**
     * @param bytes
     * @throws IOException
     */
    public void write(byte[] bytes) throws IOException {
        messageSender.write(bytes);

    }

}
