package de.hs_rm.chat_client.model.chat_message;

public class FinalChatRequestResponse {
    private boolean accepted;
    private String ip;
    private int port;
    private String usernameOfPartner;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsernameOfPartner() {
        return usernameOfPartner;
    }

    public void setUsernameOfPartner(String usernameOfPartner) {
        this.usernameOfPartner = usernameOfPartner;
    }
}
