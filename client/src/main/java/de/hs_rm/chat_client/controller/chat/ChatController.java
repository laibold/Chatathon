package de.hs_rm.chat_client.controller.chat;

import de.hs_rm.chat_client.communication.MessageService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.controller.sign_in.SignInController;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;


public class ChatController extends BaseController implements StateObserver, ChatHandler {

    @FXML
    @SuppressWarnings("unused")
    private TextArea chatTextArea;

    @FXML
    @SuppressWarnings("unused")
    private ListView<String> activeUserListView;

    @FXML
    @SuppressWarnings("unused")
    private TextArea chatLabel;

    private MessageService messageService;
    private ClientState clientState;

    private final SimpleIntegerProperty finalChatRequestState = new SimpleIntegerProperty(0);
    private final SimpleStringProperty finalChatRequestMessage = new SimpleStringProperty("");

    @FXML
    public void initialize() {
        messageService = MessageService.getInstance();
        clientState = ClientState.getInstance();
        clientState.addChatHandler(this);

        new Thread(() -> {
            var messages = List.of(
                "Seit 1998 ist Kiko Pangilinan mit Sharon Cuneta verheiratet.",
                "Im Garten regnet es.",
                "Janneks diskrete Cousine fällt ihre Pappel.",
                "Weshalb baut die Luftheizungsbauerin intelligent Heizungen auf der Eröffnung einer Ausstellung auf?",
                "Der Vermieter hilft krank.",
                "Es stürmt.",
                "Wie bereitet sich sie in einer Besprechung vor?",
                "Per hat eine Wunde am Fuß.",
                "Deine Freundin erholt sich diskret.",
                "Der hilflose Tomas isst gerade Pfirsichpizza.",
                "Claudia schreit im Restaurant.",
                "Grau ist eine hemmungslose Farbe.",
                "Wo schwimmt die Hartz-IV-Empfängerin?"
            );
            var random = new Random();

            while (true) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                var randInt = random.nextInt(messages.size());
                var message = messages.get(randInt);
                addIncomingChatMessage("Partner", message);
            }
        }).start();
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
            var str = "You: " + text;
            appendChatMessageToTextArea(str);
            chatTextArea.clear();
            chatTextArea.requestFocus();
            System.out.println("Send " + text);
        }
    }

    @FXML
    private void signOut(ActionEvent event) {
        var username = clientState.getCurrentUser();
        try {
            messageService.sendSignOut(username);
            clientState.setCurrentState(ClientState.State.STRANGER);
            navigateToNext();
        } catch (InvalidHeaderException e) {
            System.out.println("invalid header");
        } catch (IOException e) {
            System.out.println("IOException");
        }
    }

    public void addIncomingChatMessage(String sender, String message) {
        message = message.trim();
        if (!message.isBlank()) {
            var str = sender + ": " + message;
            appendChatMessageToTextArea(str);
            System.out.println("Received from " + sender + ": " + message);
        }
    }

    private void appendChatMessageToTextArea(String message) {
        Platform.runLater(() -> {
            chatLabel.appendText(message + "\n\n");
            chatLabel.setScrollTop(Integer.MAX_VALUE);
        });
    }

    @Override
    public String getViewPath() {
        return "chat.fxml";
    }

    @Override
    public void navigateToNext() {
        navigateTo(new SignInController());
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

            var accepted = false;

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    //ok button is pressed
                    System.out.println("yes");
                    accepted = true;
                } else if (result.get() == ButtonType.NO) {
                    // cancel button is pressed
                    System.out.println("no");
                    accepted = false;
                }
            }

            try {
                messageService.sendChatRequestResponse(username, accepted);
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
