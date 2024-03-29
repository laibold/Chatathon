package de.hs_rm.chat_client.model.tcp.message;

public class Header {
    private MessageType messageType;
    private int contentLength;
    private Status status;

    public enum Status {SUCCESS, ERROR}

    public Header(MessageType messageType, int contentLength) {
        this.messageType = messageType;
        this.contentLength = contentLength;
    }

    public Header(MessageType messageType, int contentLength, Status status) {
        this.messageType = messageType;
        this.contentLength = contentLength;
        this.status = status;
    }

    @Override
    public String toString() {
        var statusStr = status == null ? "" : ", status=" + status;
        return "{" +
            "messageType=" + messageType +
            ", contentLength=" + contentLength +
            statusStr +
            '}';
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
}
