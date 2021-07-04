package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;

public class OutgoingChatRequestResponseMessageHandler extends MessageHandler {
    @Override
    public String handle(Message message) {
        var chatAccepted = gson.fromJson(message.getBody(), Boolean.class);
        var status = Header.Status.SUCCESS;
        String bodyContent;

        if (!chatAccepted) {
            bodyContent = "false";
        } else {
            bodyContent = "true";
        }

        try {
            return MessageGenerator.generateMessage(status, MessageType.FINAL_CHAT_REQUEST_RESPONSE, bodyContent);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
