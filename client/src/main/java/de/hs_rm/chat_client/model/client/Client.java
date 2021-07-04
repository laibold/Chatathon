package de.hs_rm.chat_client.model.client;

import java.net.Socket;

public class Client {
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

}
