package de.hs_rm.chat_client.controller;

import de.hs_rm.chat_client.communication.MessageService;
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

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
        ClientState.getInstance().addObserver(this, ClientState.State.SIGNED_UP);
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
        System.out.println("Start chatting!"); // TODO
    }
}
