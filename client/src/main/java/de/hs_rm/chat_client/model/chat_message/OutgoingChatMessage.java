package de.hs_rm.chat_client.model.chat_message;

public class OutgoingChatMessage {

    private String username;
    private boolean acceptance;

    public OutgoingChatMessage(String username, boolean acceptance) {
        this.username = username;
        this.acceptance = acceptance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAcceptance() {
        return acceptance;
    }

    public void setAcceptance(boolean acceptance) {
        this.acceptance = acceptance;
    }
}
