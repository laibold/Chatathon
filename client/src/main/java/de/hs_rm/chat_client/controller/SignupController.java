package de.hs_rm.chat_client.controller;

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

    @FXML
    public void initialize() {

    }

    @FXML
    private void signUp(ActionEvent event) {
        if (usernameText.getText().isBlank() || passwordText.getText().isBlank()) {
            new Alert(Alert.AlertType.NONE, "Eingaben überprüfen!", ButtonType.CLOSE).showAndWait();
        } else {
            System.out.println("sign up");
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
