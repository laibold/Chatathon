package de.hs_rm.chat_client.model.chat_message;

public class OutgoingChatRequestResponse {

    private String username;
    private boolean accepted;

    public OutgoingChatRequestResponse() {

    }

    public OutgoingChatRequestResponse(String username, boolean accepted) {
        this.username = username;
        this.accepted = accepted;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
