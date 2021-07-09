package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.chat_message.IncomingChatRequest;
import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.service.ClientService;
import de.hs_rm.chat_server.service.UserService;

public class IncomingChatRequestMessageHandler extends MessageHandler {

    private final UserService userService = UserService.getInstance();
    private final ClientService clientService = ClientService.getInstance();

    @Override
    public String handle(Message message) {
        var request = gson.fromJson(message.getBody(), IncomingChatRequest.class);
        var queriedUser = userService.getUserByName(request.getRecipient());

        var senderClient = clientService.getClient(request.getSender());
        var senderIpAddress = senderClient.getIp();

        Header.Status status;
        String bodyContent;
        Client recipient = null;

        if (queriedUser == null) {
            status = Header.Status.ERROR;
            bodyContent = "User does not exist";
        } else {
            recipient = clientService.getClient(queriedUser.getUsername());
            if (recipient == null) {
                status = Header.Status.ERROR;
                bodyContent = "User not active";
            } else {
                status = Header.Status.SUCCESS;
                bodyContent = "Please wait, chat request will be forwarded";
            }
        }

        try {
            if (status == Header.Status.SUCCESS) {
                new OutgoingChatRequestMessageHandler().sendOutgoingChatRequestMessage(
                    recipient,
                    request.getSender(),
                    senderIpAddress,
                    request.getSenderPort()
                );
            }
            return MessageGenerator.generateMessage(status, MessageType.INCOMING_CHAT_REQUEST_RESPONSE, bodyContent);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
