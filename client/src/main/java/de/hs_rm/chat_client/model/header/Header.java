package de.hs_rm.chat_client.model.header;

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
