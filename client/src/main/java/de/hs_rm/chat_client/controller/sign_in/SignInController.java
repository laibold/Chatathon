package de.hs_rm.chat_client.controller.sign_in;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.sign_up.SignUpController;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.controller.chat.ChatController;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.user.User;
import de.hs_rm.chat_client.service.PasswordHasher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SignInController extends BaseController implements StateObserver {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    private MessageService messageService;
    private ClientState clientState;

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
        this.clientState = ClientState.getInstance();
        clientState.addStateObserver(this, ClientState.State.SIGNED_IN);
    }

    @FXML
    private void signIn(ActionEvent event) {
        if (usernameText.getText().isBlank() || passwordText.getText().isBlank()) {
            new Alert(Alert.AlertType.NONE, "Invalid input", ButtonType.CLOSE).showAndWait();
        } else {
            var username = usernameText.getText().trim();
            var password = PasswordHasher.getHashedPassword(passwordText.getText().trim());

            try {
                messageService.sendSignInMessage(new User(username, password));
                clientState.setCurrentUser(username);
            } catch (InvalidHeaderException e) {
                new Alert(Alert.AlertType.ERROR, "Internal failure", ButtonType.CLOSE).showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Network failure", ButtonType.CLOSE).showAndWait();
            }
        }
    }

    @FXML
    private void navigateToSignUp(MouseEvent event) {
        navigateTo(new SignUpController());
    }

    @Override
    public String getViewPath() {
        return "signin.fxml";
    }

    @Override
    public void navigateToNext() {
        navigateTo(new ChatController());
    }
}
