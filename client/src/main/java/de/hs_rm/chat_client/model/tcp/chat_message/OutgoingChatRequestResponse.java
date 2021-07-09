package de.hs_rm.chat_client.model.tcp.chat_message;

public class OutgoingChatRequestResponse {

    private String respondingUsername;
    private String requestedUsername;
    private boolean accepted;
    private int receiveUdpPort;

    public OutgoingChatRequestResponse(String respondingUsername, String requestedUsername, boolean accepted, int receiveUdpPort) {
        this.respondingUsername = respondingUsername;
        this.requestedUsername = requestedUsername;
        this.accepted = accepted;
        this.receiveUdpPort = receiveUdpPort;
    }

    public String getRespondingUsername() {
        return respondingUsername;
    }

    public void setRespondingUsername(String respondingUsername) {
        this.respondingUsername = respondingUsername;
    }

    public String getRequestedUsername() {
        return requestedUsername;
    }

    public void setRequestedUsername(String requestedUsername) {
        this.requestedUsername = requestedUsername;
    }

    public boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public int getReceiveUdpPort() {
        return receiveUdpPort;
    }

    public void setReceiveUdpPort(int receiveUdpPort) {
        this.receiveUdpPort = receiveUdpPort;
    }
}
