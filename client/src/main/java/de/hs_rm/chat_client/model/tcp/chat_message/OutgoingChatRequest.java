package de.hs_rm.chat_client.model.tcp.chat_message;

public class OutgoingChatRequest {
    private String sender;
    private String senderIpAddress;
    private int senderUdpPort;

    public OutgoingChatRequest(String sender, String senderIpAddress, int senderUdpPort) {
        this.sender = sender;
        this.senderIpAddress = senderIpAddress;
        this.senderUdpPort = senderUdpPort;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderIpAddress() {
        return senderIpAddress;
    }

    public void setSenderIpAddress(String senderIpAddress) {
        this.senderIpAddress = senderIpAddress;
    }

    public int getSenderUdpPort() {
        return senderUdpPort;
    }

    public void setSenderUdpPort(int senderUdpPort) {
        this.senderUdpPort = senderUdpPort;
    }
}
