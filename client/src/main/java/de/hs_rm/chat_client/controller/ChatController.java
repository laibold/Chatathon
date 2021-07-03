package de.hs_rm.chat_client.controller;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.IOException;


public class ChatController extends BaseController implements StateObserver {

    @FXML
    TextArea chatTextArea;

    private MessageService messageService;

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
    }

    @FXML
    private void refreshUserList(ActionEvent event) {
        System.out.println("refresh");
        try {
            messageService.sendActiveUserListRequest();
        } catch (InvalidHeaderException e) {
            System.out.println("invalid header");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    @FXML
    private void sendChat(ActionEvent event) {
        var text = chatTextArea.getText().trim();
        if (!text.isBlank()) {
            System.out.println("Send " + text);
        }
    }

    @Override
    public String getViewPath() {
        return "chat.fxml";
    }

    @Override
    public void navigateToNext() {
        // TODO logout
    }
}
