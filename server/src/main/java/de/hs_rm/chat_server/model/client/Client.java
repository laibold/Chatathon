package de.hs_rm.chat_server.model.client;

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

    public String getIp() {
        return socket.getInetAddress().getHostAddress();
    }

    public int getPort() {
        return socket.getLocalPort();
    }

}
