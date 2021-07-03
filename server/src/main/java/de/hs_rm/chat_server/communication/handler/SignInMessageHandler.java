package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.model.user.User;
import de.hs_rm.chat_server.model.user.UserNotFoundException;
import de.hs_rm.chat_server.service.ClientService;
import de.hs_rm.chat_server.service.PersistenceException;
import de.hs_rm.chat_server.service.UserService;

public class SignInMessageHandler extends MessageHandler {

    private final UserService userService = UserService.getInstance();
    private final ClientService clientService = ClientService.getInstance();

    @Override
    public String handle(Message message) {
        var user = gson.fromJson(message.getBody(), User.class);

        Header.Status status;
        String bodyContent;

        try {
            var credentialsValid = userService.checkUserCredentials(user);
            if (credentialsValid) {
                status = Header.Status.SUCCESS;
                bodyContent = "";
                clientService.addClient(user, message.getClient());
            } else {
                status = Header.Status.ERROR;
                bodyContent = "Username or password incorrect";
            }
        } catch (UserNotFoundException e) {
            status = Header.Status.ERROR;
            bodyContent = "Username or password incorrect";
        } catch (PersistenceException e) {
            status = Header.Status.ERROR;
            bodyContent = "General failure";
        }

        try {
            return MessageGenerator.generateMessage(status, getResponseMessageType(), bodyContent);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public MessageType getResponseMessageType() {
        return MessageType.SIGN_IN_RESPONSE;
    }
}
