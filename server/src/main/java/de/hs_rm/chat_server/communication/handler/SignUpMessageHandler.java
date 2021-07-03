package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.model.header.MessageType;
import de.hs_rm.chat_server.model.user.User;
import de.hs_rm.chat_server.model.user.UserAlreadyExistsException;
import de.hs_rm.chat_server.service.PersistenceException;
import de.hs_rm.chat_server.service.UserService;

public class SignUpMessageHandler extends MessageHandler {

    private final UserService userService = UserService.getInstance();

    @Override
    public String handle(String body) {
        var user = gson.fromJson(body, User.class);
        try {
            userService.insertUser(user);
        } catch (PersistenceException | UserAlreadyExistsException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageType getResponseMessageType() {
        return MessageType.SIGN_UP_RESPONSE;
    }
}
