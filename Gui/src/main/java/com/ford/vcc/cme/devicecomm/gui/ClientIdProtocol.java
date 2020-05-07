package com.ford.vcc.cme.devicecomm.gui;

import java.io.IOException;

import com.ford.vcc.cme.devicecomm.core.common.Sink;

public class ClientIdProtocol implements Sink{




    private byte[] clientId;
    private Sink targetSink;

    public ClientIdProtocol(int clientId, Sink targetSink){
        this(Integer.toString(clientId), targetSink);
    }

    public ClientIdProtocol(String clientId, Sink targetSink){


        this.clientId=("Client ID "+clientId+": ").getBytes();
        this.targetSink=targetSink;
    }

    public void write(byte[] data, int off, int len) throws IOException {
        byte[] newData = new byte[clientId.length+len];

        System.arraycopy(clientId, 0, newData, 0, clientId.length);
        System.arraycopy(data, off, newData, clientId.length, len);


        targetSink.write(newData, 0, newData.length);
    }
    public void write(byte[] data) throws IOException {
        write(data, 0, data.length);
    }




}
