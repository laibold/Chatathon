package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.*;

public class OutgoingChatRequestResponseMessageHandler extends MessageHandler {
    @Override
    public String handle(Message message) {
        // TODO: Ich bekomme USERNAME (initiator) und BOOLEAN

        var response = gson.fromJson(message.getBody(), OutgoingChatRequestResponse.class);
        var status = Header.Status.SUCCESS;

        var finalChatRequestResponse = new FinalChatRequestResponse();

        if (!response.getResponse()) {
            finalChatRequestResponse.setAcceptance(false);
        } else {
            finalChatRequestResponse.setAcceptance(true);
            finalChatRequestResponse.setClient(message.getClient());
        }

        try {
            return MessageGenerator.generateMessage(status, MessageType.FINAL_CHAT_REQUEST_RESPONSE, finalChatRequestResponse);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
