package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.service.ClientService;
import de.hs_rm.chat_server.service.UserService;

public class SignOutMessageHandler extends MessageHandler {

    ClientService clientService = ClientService.getInstance();
    UserService userService = UserService.getInstance();

    @Override
    public String handle(Message message) {
        var username = gson.fromJson(message.getBody(), String.class);
        var user = userService.getUserByName(username);

        if (user != null) {
            clientService.deleteClient(user);
        }

        return null;
    }
}
