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
        Header.Status status;
        String bodyContent;

        try {
            userService.insertUser(user);
            status = Header.Status.SUCCESS;
            bodyContent = "";
        } catch (PersistenceException e) {
            status = Header.Status.ERROR;
            bodyContent = "General failure";
        } catch (UserAlreadyExistsException e) {
            status = Header.Status.ERROR;
            bodyContent = "User already exists";
        }

        try {
            return MessageGenerator.generateMessage(status, MessageType.SIGN_UP_RESPONSE, bodyContent);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }
}
