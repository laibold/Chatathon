package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.model.user.User;
import de.hs_rm.chat_server.service.ClientService;

import java.util.stream.Collectors;

public class ListActiveUsersMessageHandler extends MessageHandler {

    private final ClientService clientService = ClientService.getInstance();

    @Override
    // Returns active Clients as keySet
    public String handle(Message message) {
        var activeClients = clientService.getClients().keySet().stream()
            .map(User::getUsername)
            .collect(Collectors.toList());

        try {
            return MessageGenerator.generateMessage(Header.Status.SUCCESS, MessageType.LIST_ACTIVE_USERS_RESPONSE, activeClients);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
