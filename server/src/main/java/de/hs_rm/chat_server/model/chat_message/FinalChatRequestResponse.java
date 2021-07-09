package de.hs_rm.chat_server.model.chat_message;

public class FinalChatRequestResponse {
    private boolean accepted;
    private String ipAddress;
    private int udpPort;
    private String usernameOfPartner;

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public String getUsernameOfPartner() {
        return usernameOfPartner;
    }

    public void setUsernameOfPartner(String usernameOfPartner) {
        this.usernameOfPartner = usernameOfPartner;
    }
}
