package de.hs_rm.chat_client.model.chat_message;

public class IncomingChatRequest {
    private String sender;
    private String recipient;

    public IncomingChatRequest(String sender, String recipient) {
        this.sender = sender;
        this.recipient = recipient;
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
}
