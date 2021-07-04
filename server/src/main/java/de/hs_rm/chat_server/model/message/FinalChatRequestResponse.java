package de.hs_rm.chat_server.model.message;

import java.net.InetAddress;

public class FinalChatRequestResponse {
    private boolean accepted;
    private InetAddress ip;
    private int port;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public InetAddress getIp() {
        return ip;
    }

    public void setIp(InetAddress ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
