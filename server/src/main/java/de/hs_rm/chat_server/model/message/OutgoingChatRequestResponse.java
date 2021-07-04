package de.hs_rm.chat_server.model.message;

public class OutgoingChatRequestResponse {
    private Boolean response;
    private String username;

    public OutgoingChatRequestResponse(Boolean response, String username) {
        this.response = response;
        this.username = username;
    }

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
