package de.hs_rm.chat_client.controller.chat;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.model.message.InvalidHeaderException;
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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class ChatController extends BaseController implements StateObserver, ChatHandler {

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
        clientState.addChatHandler(this);
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
                    e.printStackTrace(); // TODO
                }

                System.out.println("Requested chat with user " + currentItemSelected);

                AtomicReference<Alert> alert = new AtomicReference<>();

                Platform.runLater(() -> {
                    alert.set(new Alert(Alert.AlertType.INFORMATION, "Requested chat with user " + currentItemSelected));
                    alert.get().getButtonTypes().clear();
                    alert.get().setHeaderText("Chat request");
                    alert.get().setTitle("Outgoing chat request");

                    alert.get().showAndWait();
                });

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
                                alert.get().setContentText("Server received request for user " + currentItemSelected + ", hold the line.")
                            );
                            break;
                        case REQUEST_ERROR:
                        case DECLINED:
                            Platform.runLater(() -> {
                                alert.get().setAlertType(Alert.AlertType.ERROR);
                                alert.get().setContentText(finalChatRequestMessage.get());
                                alert.get().getButtonTypes().add(ButtonType.CLOSE);
                            });
                            break;
                        case ACCEPTED:
                            Platform.runLater(() -> {
                                alert.get().setContentText("User accepted your request. Happy chatting!");
                                finalChatRequestState.set(ChatRequestState.UNINITIALIZED.ordinal());
                                alert.get().getButtonTypes().add(ButtonType.CLOSE);
                            });
                            break;
                        default:
                            break;
                    }
                })).start();

            }
        });

        activeUserListView.setItems(obsList);
    }

    @Override
    public void openChatRequest(String username) {
        var text = "User " + username + " wants to chat with you. Accept?";

        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Chat request");
            alert.setTitle("Incoming chat request");

            var acceptance = false;

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty()) {
                // alert is exited, no button has been pressed.
                // TODO auf windows testen, ob man beim Fenster schließen hier landet
                System.out.println("exited");
                acceptance = false;
            } else if (result.get() == ButtonType.YES) {
                //oke button is pressed
                System.out.println("yes");
                acceptance = true;
            } else if (result.get() == ButtonType.NO) {
                // cancel button is pressed
                System.out.println("no");
                acceptance = false;
            }

            try {
                messageService.sendChatRequestResponse(username, acceptance);
            } catch (InvalidHeaderException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace(); // TODO
            }
        });
    }

    @Override
    public void setFinalChatRequestState(ChatRequestState state, String message) {
        finalChatRequestMessage.set(message);
        finalChatRequestState.set(state.ordinal());
    }
}
