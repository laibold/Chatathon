package de.hs_rm.chat_client.controller.chat;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.controller.*;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.List;


public class ChatController extends BaseController implements StateObserver, UserListObserver, FinalChatRequestResponseObserver {

    @FXML
    private TextArea chatTextArea;

    @FXML
    private ListView<String> activeUserListView;

    private MessageService messageService;

    private final SimpleIntegerProperty finalChatRequestState = new SimpleIntegerProperty(0);
    private final SimpleStringProperty finalChatRequestMessage = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
        var clientState = ClientState.getInstance();
        clientState.addUserListObserver(this);
        clientState.setFinalChatRequestResponseObserver(this);
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

                System.out.println("Requested chat with user " + currentItemSelected);

                var alert = new Alert(Alert.AlertType.INFORMATION, "Requested chat with user " + currentItemSelected);
                alert.getButtonTypes().clear();
                alert.setHeaderText("Chat request");
                alert.setTitle("Chat request");

                new Thread(() -> finalChatRequestState.addListener((o, oldVal, newVal) -> {
                    var state = ChatRequestState.values()[newVal.intValue()];
                    switch (state) {
                        case REQUESTED:
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            Platform.runLater(() ->
                                alert.setContentText("Server received request for user " + currentItemSelected + ", hold the line.")
                            );
                            break;
                        case REQUEST_ERROR:
                        case DECLINED:
                            Platform.runLater(() -> {
                                alert.setAlertType(Alert.AlertType.ERROR);
                                alert.setContentText(finalChatRequestMessage.get());
                                alert.getButtonTypes().add(ButtonType.CLOSE);
                            });
                            break;
                        case ACCEPTED:
                            Platform.runLater(() -> {
                                alert.setContentText("User accepted your request. Happy chatting!");
                                finalChatRequestState.set(ChatRequestState.UNINITIALIZED.ordinal());
                                alert.getButtonTypes().add(ButtonType.CLOSE);
                            });
                            break;
                        default:
                            break;
                    }
                })).start();

                alert.showAndWait();
            }
        });

        activeUserListView.setItems(obsList);
    }

    @Override
    public void setFinalChatRequestState(ChatRequestState state, String message) {
        finalChatRequestMessage.set(message);
        finalChatRequestState.set(state.ordinal());
    }
}
