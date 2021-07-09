package de.hs_rm.chat_client.communication.udp;

public class MessageSendTimeoutException extends Exception {
    public MessageSendTimeoutException() {
    }

    public MessageSendTimeoutException(String message) {
        super(message);
    }
}
