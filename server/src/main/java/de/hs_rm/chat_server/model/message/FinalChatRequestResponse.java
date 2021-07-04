package de.hs_rm.chat_server.model.message;

import de.hs_rm.chat_server.model.client.Client;

public class FinalChatRequestResponse {
    private Boolean response;
    private Client client;

    public Boolean getResponse() {
        return response;
    }

    public void setResponse(Boolean response) {
        this.response = response;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
