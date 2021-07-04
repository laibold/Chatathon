package de.hs_rm.chat_client.controller.chat;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.controller.UserListObserver;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.List;


public class ChatController extends BaseController implements StateObserver, UserListObserver {

    @FXML
    private TextArea chatTextArea;

    @FXML
    private ListView<String> activeUserListView;

    private MessageService messageService;

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
        ClientState.getInstance().addUserListObserver(this);
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

    @Override
    public void setUserList(List<String> userList) {
        System.out.println("Users set:");
        userList.forEach(System.out::println);

        var obsList = FXCollections.observableArrayList(userList);

        activeUserListView.setOnMouseClicked(click -> {

            if (click.getClickCount() == 2) {
                var currentItemSelected = activeUserListView
                    .getSelectionModel()
                    .getSelectedItem();

                try {
                    messageService.sendChatRequest(currentItemSelected);
                } catch (InvalidHeaderException | IOException e) {
                    e.printStackTrace();
                }

                System.out.println(currentItemSelected + " angefragt");

                var alert = new Alert(Alert.AlertType.INFORMATION, "angefragt");

                new Thread(() -> {
                    try {
                        // Wait for 2 secs
                        // TODO auf Antwort warten
                        Thread.sleep(2000);
                        if (alert.isShowing()) {
                            Platform.runLater(alert::close);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

                alert.showAndWait();

            }
        });

        activeUserListView.setItems(obsList);
    }
}
