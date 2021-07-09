package de.hs_rm.chat_server.model.chat_message;

public class IncomingChatRequest {
    private String sender;
    private String recipient;
    private int senderPort;

    public IncomingChatRequest(String sender, String recipient, int senderPort) {
        this.sender = sender;
        this.recipient = recipient;
        this.senderPort = senderPort;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }
}
