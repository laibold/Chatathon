package de.hs_rm.chat_client.controller;

import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.header.MessageType;
import de.hs_rm.chat_client.model.user.User;
import de.hs_rm.chat_client.service.MessageGenerator;
import de.hs_rm.chat_client.service.PasswordHasher;
import de.hs_rm.chat_client.service.SendMessageService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class SignupController extends BaseController {

    @FXML
    private TextField usernameText;

    @FXML
    private PasswordField passwordText;

    SendMessageService sendMessageService;

    @FXML
    public void initialize() {

    }

    @FXML
    private void signUp(ActionEvent event) {
        if (usernameText.getText().isBlank() || passwordText.getText().isBlank()) {
            new Alert(Alert.AlertType.NONE, "Eingaben überprüfen!", ButtonType.CLOSE).showAndWait();
        } else {
            var username = usernameText.getText().trim();
            var password = PasswordHasher.getHashedPassword(passwordText.getText().trim());

            var message = "";
            try {
                message = MessageGenerator.generateMessage(MessageType.SIGN_UP, new User(username, password));
            } catch (InvalidHeaderException e) {
                new Alert(Alert.AlertType.ERROR, "Allgemeiner Fehler!", ButtonType.CLOSE).showAndWait();
            }

            //sendMessageService.sendMessage(message);
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
