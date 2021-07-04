package de.hs_rm.chat_server.model.message;

public class OutgoingChatRequestResponse {
    private Boolean acceptance;
    private String username;

    public OutgoingChatRequestResponse(Boolean acceptance, String username) {
        this.acceptance = acceptance;
        this.username = username;
    }

    public Boolean getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Boolean acceptance) {
        this.acceptance = acceptance;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
