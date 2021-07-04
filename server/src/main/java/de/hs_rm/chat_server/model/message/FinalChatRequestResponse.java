package de.hs_rm.chat_server.model.message;

import de.hs_rm.chat_server.model.client.Client;

public class FinalChatRequestResponse {
    private boolean accepted;
    private Client client;

    public Boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
