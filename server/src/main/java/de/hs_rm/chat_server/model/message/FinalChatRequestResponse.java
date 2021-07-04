package de.hs_rm.chat_server.model.message;

import de.hs_rm.chat_server.model.client.Client;

public class FinalChatRequestResponse {
    private Boolean acceptance;
    private Client client;

    public Boolean getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(Boolean acceptance) {
        this.acceptance = acceptance;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
