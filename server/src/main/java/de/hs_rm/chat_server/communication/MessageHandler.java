package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.model.header.InvalidHeaderException;
import de.hs_rm.chat_server.model.header.MessageType;
import de.hs_rm.chat_server.service.HeaderMapper;

public class MessageHandler {
    private MessageGenerator messageGenerator;

    public MessageHandler() {
        messageGenerator = new MessageGenerator();
    }

    MessageType messageType;

    public String createResponse(String message) throws InvalidHeaderException {
        var header = HeaderMapper.toHeader(message);

        switch (messageType) {
            case SIGN_UP:
                return AuthHandler.getResponseForSignUp();

            case SIGN_IN:
                return AuthHandler.getResponseForSignIn();

            case SIGN_OUT:
                return AuthHandler.getResponseForSignOut();

            case CHAT_REQUEST:
                return ChatHandler.getResponseForChatRequest();
        }
    }
}