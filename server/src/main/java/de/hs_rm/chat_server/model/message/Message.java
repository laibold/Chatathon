package de.hs_rm.chat_server.model.message;

import de.hs_rm.chat_server.model.client.Client;

public class Message {
    private Header header;
    private String body;
    private Client client;

    public Message(Header header, String body, Client client) {
        this.header = header;
        this.body = body;
        this.client = client;
    }

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
