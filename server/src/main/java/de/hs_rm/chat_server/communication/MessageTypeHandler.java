package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.communication.handler.MessageHandler;
import de.hs_rm.chat_server.communication.handler.SignInMessageHandler;
import de.hs_rm.chat_server.communication.handler.SignUpMessageHandler;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;

import java.util.HashMap;
import java.util.Map;

public class MessageTypeHandler {
    private final Map<MessageType, MessageHandler> handlers = new HashMap<>();

    public MessageTypeHandler() {
        handlers.put(MessageType.SIGN_UP, new SignUpMessageHandler());
        handlers.put(MessageType.SIGN_IN, new SignInMessageHandler());
    }

    public String handleMessage(Message message) throws InvalidHeaderException {
        var handler = handlers.get(message.getHeader().getMessageType());

        return handler.handle(message);
    }


}