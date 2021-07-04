package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.service.ClientService;
import de.hs_rm.chat_server.service.UserService;

public class IncomingChatRequestMessageHandler extends MessageHandler {

    UserService userService = UserService.getInstance();
    ClientService clientService = ClientService.getInstance();

    @Override
    public String handle(Message message) {
        var username = gson.fromJson(message.getBody(), String.class);
        var queriedUser = userService.getUserByName(username);

        Header.Status status;
        String bodyContent;

        if (queriedUser == null) {
            status = Header.Status.ERROR;
            bodyContent = "User does not exist";
        } else {
            var client = clientService.getClient(queriedUser);
            if (client == null) {
                status = Header.Status.ERROR;
                bodyContent = "User not active";
            } else {
                status = Header.Status.SUCCESS;
                bodyContent = "Please wait, chat request will be forwarded";
            }
        }

        try {
            return MessageGenerator.generateMessage(status, MessageType.INCOMING_CHAT_REQUEST_RESPONSE, bodyContent);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
