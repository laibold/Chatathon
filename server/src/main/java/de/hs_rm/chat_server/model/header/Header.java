package de.hs_rm.chat_server.model.header;

public class Header {
    private MessageType messageType;
    private int contentLength;

    public Header(MessageType messageType, int contentLength) {
        this.messageType = messageType;
        this.contentLength = contentLength;
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
}
