package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;

public class OutgoingChatRequestResponseMessageHandler extends MessageHandler {
    @Override
    public String handle(Message message) {
        var answer = gson.fromJson(message.getBody(), String.class);
        Header.Status status;
        String bodyContent;

        if (answer.toLowerCase().contains("declined")) {
            status = Header.Status.ERROR;
            bodyContent = "Chat request declined";

            try {
                return MessageGenerator.generateMessage(status, MessageType.CHAT_REQUEST_DECLINED, bodyContent);
            } catch (InvalidHeaderException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
