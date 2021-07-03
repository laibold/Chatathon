package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.model.header.MessageType;

public class SignUpMessageHandler implements HandlerInterface {

    @Override
    public String handle(String message) {
        return null;
    }

    @Override
    public MessageType getResponseMessageType() {
        return MessageType.SIGN_UP_RESPONSE;
    }
}
