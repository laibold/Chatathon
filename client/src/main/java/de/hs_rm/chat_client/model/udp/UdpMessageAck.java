package de.hs_rm.chat_client.model.udp;

import java.io.Serializable;

public class UdpMessageAck implements Serializable {

    private int seqNumber;

    public UdpMessageAck(int packet) {
        super();
        this.seqNumber = packet;
    }

    public int getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(int seqNumber) {
        this.seqNumber = seqNumber;
    }
}
