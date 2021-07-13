package de.hs_rm.chat_client.controller.chat;

import de.hs_rm.chat_client.communication.tcp.ServerMessageService;
import de.hs_rm.chat_client.communication.udp.MessageReceiveService;
import de.hs_rm.chat_client.communication.udp.MessageSendService;
import de.hs_rm.chat_client.controller.BaseController;
import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.controller.StateObserver;
import de.hs_rm.chat_client.controller.sign_in.SignInController;
import de.hs_rm.chat_client.model.tcp.message.InvalidHeaderException;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;


public class ChatController extends BaseController implements StateObserver, ChatHandler, ChatMessageReceiver, ChatErrorHandler {

    @FXML
    @SuppressWarnings("unused")
    private TextArea chatTextArea;

    @FXML
    @SuppressWarnings("unused")
    private ListView<String> activeUserListView;

    @FXML
    @SuppressWarnings("unused")
    private TextArea chatLabel;

    @FXML
    @SuppressWarnings("unused")
    private Button sendButton;

    private ServerMessageService serverMessageService;
    private MessageSendService messageSendService;
    private MessageReceiveService messageReceiveService;
    private ClientState clientState;

    private final SimpleIntegerProperty finalChatRequestState = new SimpleIntegerProperty(0);
    private final SimpleStringProperty finalChatRequestMessage = new SimpleStringProperty("");

    private final ListProperty<String> messageList = new SimpleListProperty<>(FXCollections.observableArrayList(new ArrayList<>()));

    @FXML
    public void initialize() {
        serverMessageService = ServerMessageService.getInstance();
        messageSendService = new MessageSendService(this);

        try {
            messageReceiveService = new MessageReceiveService();
        } catch (SocketException e) {
            e.printStackTrace(); // TODO
        }

        clientState = ClientState.getInstance();
        clientState.addChatHandler(this);

        messageList.addListener((o, oldVal, newVal) -> {
            var messages = String.join("\n\n", newVal);
            chatLabel.setText(messages);
            chatLabel.setScrollTop(Integer.MAX_VALUE);
        });

        sendButton.setDisable(true);
    }

    @FXML
    private void refreshUserList(ActionEvent event) {
        try {
            serverMessageService.sendActiveUserListRequest();
        } catch (InvalidHeaderException e) {
            System.err.println("ChatController: Invalid header when sending refreshUserList()");
        } catch (IOException e) {
            System.err.println("ChatController: IOException when sending refreshUserList()");
        }
    }

    @FXML
    private void sendChat(ActionEvent event) {
        var text = chatTextArea.getText().trim();
        if (!text.isBlank()) {
            System.out.println("ChatController: Send chat message " + text + "\n");
            messageSendService.sendMessage(text);

            var str = "You: " + text;
            messageList.add(str);
            chatTextArea.clear();
            chatTextArea.requestFocus();
        }
    }

    @FXML
    private void signOut(ActionEvent event) {
        try {
            serverMessageService.sendSignOut();
            clientState.setCurrentState(ClientState.State.STRANGER);
            messageReceiveService.stopListening();

            navigateToNext();
        } catch (InvalidHeaderException e) {
            System.err.println("ChatController: Invalid header when sending signOut()");
        } catch (IOException e) {
            System.err.println("ChatController: IOException when sending signOut()");
        }

    }

    public void addIncomingChatMessage(String sender, String message) {
        message = message.trim();
        if (!message.isBlank()) {
            var str = sender + ": " + message;
            messageList.add(str);
            System.out.println("ChatController: Received from " + sender + ": " + message);
        }
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
        var obsList = FXCollections.observableArrayList(userList);

        activeUserListView.setOnMouseClicked(click -> {
            if (click.getClickCount() == 2) {
                var currentItemSelected = activeUserListView
                        .getSelectionModel()
                        .getSelectedItem();

                try {
                    serverMessageService.sendChatRequest(currentItemSelected, messageReceiveService.getReceivePort());
                } catch (InvalidHeaderException | IOException e) {
                    e.printStackTrace(); // TODO
                }

                System.out.println("ChatController: Requested chat with user " + currentItemSelected);

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
                                sendButton.setDisable(false);
                                alert.get().setContentText("User accepted your request. Happy chatting!");
                                finalChatRequestState.set(ChatRequestState.UNINITIALIZED.ordinal());
                                alert.get().getButtonTypes().add(ButtonType.CLOSE);
                            });
                            messageReceiveService.listenForMessages(this);
                            break;
                        default:
                            break;
                    }
                })).start();
            }
        });

        Platform.runLater(() -> {
            activeUserListView.getItems().clear();
            activeUserListView.setItems(obsList);
        });
    }

    @Override
    public void openChatRequest(String senderUsername, String senderIpAddress, int senderUdpPort) {
        var text = "User " + senderUsername + " wants to chat with you. Accept?";

        Platform.runLater(() -> {
            var alert = new Alert(Alert.AlertType.INFORMATION, text, ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Chat request");
            alert.setTitle("Incoming chat request");

            var accepted = false;
            var receivePort = 0;

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.YES) {
                    //ok button is pressed
                    sendButton.setDisable(false);
                    System.out.println("ChatController: accepted chat request");
                    accepted = true;
                    receivePort = messageReceiveService.getReceivePort();

                    clientState.setCurrentState(ClientState.State.CHATTING);
                    clientState.setCurrentChatPartner(senderUsername);
                    try {
                        clientState.setCurrentChatPartnerAddress(InetAddress.getByName(senderIpAddress));
                    } catch (UnknownHostException e) {
                        e.printStackTrace(); // TODO
                    }
                    clientState.setCurrentChatPartnerPort(senderUdpPort);

                    messageReceiveService.listenForMessages(this);
                } else if (result.get() == ButtonType.NO) {
                    // cancel button is pressed
                    System.out.println("ChatController: declined chat request");
                    accepted = false;
                }
            }

            try {
                serverMessageService.sendChatRequestResponse(clientState.getCurrentUsername(), senderUsername, accepted, receivePort);
            } catch (InvalidHeaderException | IOException e) {
                e.printStackTrace(); // TODO
            }
        });
    }

    @Override
    public void setFinalChatRequestState(ChatRequestState state, String message) {
        finalChatRequestMessage.set(message);
        finalChatRequestState.set(state.ordinal());
    }

    @Override
    public void notifyAboutSendingError(String message) {
        if (!messageList.isEmpty()) {
            var lastMessageIndex = messageList.size() - 1;
            var lastMessage = messageList.get().get(lastMessageIndex);
            messageList.remove(lastMessageIndex);
            chatTextArea.setText(lastMessage.replaceFirst("You: ", ""));
        }

        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE).showAndWait());
    }
}
