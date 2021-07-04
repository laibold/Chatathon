package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.communication.handler.*;
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
        handlers.put(MessageType.SIGN_OUT, new SignOutMessageHandler());
        handlers.put(MessageType.LIST_ACTIVE_USERS, new ListActiveUsersMessageHandler());
        handlers.put(MessageType.INCOMING_CHAT_REQUEST, new IncomingChatRequestMessageHandler());
        handlers.put(MessageType.OUTGOING_CHAT_REQUEST_RESPONSE, new OutgoingChatRequestResponseMessageHandler());
    }

    public String handleMessage(Message message) throws InvalidHeaderException {
        var handler = handlers.get(message.getHeader().getMessageType());

        return handler.handle(message);
    }


}