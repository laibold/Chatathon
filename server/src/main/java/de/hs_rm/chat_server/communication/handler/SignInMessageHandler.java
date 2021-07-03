package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.model.header.MessageType;

public class SignInMessageHandler extends MessageHandler {

    @Override
    public String handle(String body) {
        return null;
    }

    @Override
    public MessageType getResponseMessageType() {
        return MessageType.SIGN_IN_RESPONSE;
    }
}
