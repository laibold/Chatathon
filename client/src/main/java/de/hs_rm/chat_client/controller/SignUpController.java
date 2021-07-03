package de.hs_rm.chat_client.controller;

import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.user.User;
import de.hs_rm.chat_client.service.PasswordHasher;
import de.hs_rm.chat_client.service.SendMessageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SignUpController extends BaseController {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    private SendMessageService sendMessageService;

    @FXML
    public void initialize() {
        sendMessageService = SendMessageService.getInstance();
    }

    @FXML
    private void signUp(ActionEvent event) {
        if (usernameText.getText().isBlank() || passwordText.getText().isBlank()) {
            new Alert(Alert.AlertType.NONE, "Eingaben überprüfen!", ButtonType.CLOSE).showAndWait();
        } else {
            var username = usernameText.getText().trim();
            var password = PasswordHasher.getHashedPassword(passwordText.getText().trim());

            try {
                sendMessageService.sendSignUpMessage(new User(username, password));
            } catch (InvalidHeaderException e) {
                new Alert(Alert.AlertType.ERROR, "Interner Fehler", ButtonType.CLOSE).showAndWait();
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "Netzwerkfehler", ButtonType.CLOSE).showAndWait();
            }
        }
    }

    @FXML
    private void navigateToSignIn(MouseEvent event) {
        navigateTo(new SignInController());
    }

    @Override
    public String getViewPath() {
        return "signup.fxml";
    }
}