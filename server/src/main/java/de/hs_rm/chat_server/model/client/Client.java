package de.hs_rm.chat_server.model.client;

import java.net.SocketAddress;

public class Client {

    private SocketAddress ip;
    private int port;

    public Client(SocketAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public SocketAddress getIp() {
        return ip;
    }

    public void setIp(SocketAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
