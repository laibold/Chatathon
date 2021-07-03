package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.model.user.User;
import de.hs_rm.chat_server.model.user.UserAlreadyExistsException;
import de.hs_rm.chat_server.service.PersistenceException;
import de.hs_rm.chat_server.service.UserService;

public class SignUpMessageHandler extends MessageHandler {

    private final UserService userService = UserService.getInstance();
    private MessageGenerator messageGenerator;

    @Override
    public String handle(Message message) {
        var user = gson.fromJson(message.getBody(), User.class);
        try {
            userService.insertUser(user);
        } catch (PersistenceException | UserAlreadyExistsException e) {
            e.printStackTrace();
        }
        try {
            return MessageGenerator.generateMessage(Header.Status.SUCCESS, getResponseMessageType(), "");
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public MessageType getResponseMessageType() {
        return MessageType.SIGN_UP_RESPONSE;
    }
}
