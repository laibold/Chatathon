package de.hs_rm.chat_client.controller.sign_up;

import de.hs_rm.chat_client.communication.tcp.ServerMessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.controller.sign_in.SignInController;
import de.hs_rm.chat_client.model.tcp.message.InvalidHeaderException;
import de.hs_rm.chat_client.model.tcp.user.User;
import de.hs_rm.chat_client.service.PasswordHasher;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

public class SignUpController extends BaseController implements StateObserver {

    @FXML
    @SuppressWarnings("unused")
    private TextField usernameText;

    @FXML
    @SuppressWarnings("unused")
    private PasswordField passwordText;

    private ServerMessageService messageService;

    @FXML
    public void initialize() {
        messageService = ServerMessageService.getInstance();
        ClientState.getInstance().addStateObserver(this, ClientState.State.SIGNED_UP);
    }

    @FXML
    private void signUp(ActionEvent event) {
        if (usernameText.getText().isBlank() || passwordText.getText().isBlank()) {
            Platform.runLater(() -> new Alert(Alert.AlertType.NONE, "Invalid input", ButtonType.CLOSE).showAndWait());
        } else {
            var username = usernameText.getText().trim();
            var password = PasswordHasher.getHashedPassword(passwordText.getText().trim());

            if (messageService == null) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Network failure", ButtonType.CLOSE).showAndWait());
                return;
            }

            try {
                messageService.sendSignUpMessage(new User(username, password));
            } catch (InvalidHeaderException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Internal failure", ButtonType.CLOSE).showAndWait());
            } catch (IOException e) {
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Network failure", ButtonType.CLOSE).showAndWait());
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

    @Override
    public void navigateToNext() {
        navigateTo(new SignInController());
    }
}
