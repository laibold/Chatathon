package de.hs_rm.chat_client.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.hs_rm.chat_client.controller.chat.ChatHandler;
import de.hs_rm.chat_client.controller.chat.ChatHandler.ChatRequestState;
import de.hs_rm.chat_client.model.tcp.chat_message.FinalChatRequestResponse;
import de.hs_rm.chat_client.model.tcp.chat_message.OutgoingChatRequest;
import de.hs_rm.chat_client.model.tcp.message.Header;
import de.hs_rm.chat_client.model.tcp.message.MessageType;
import javafx.application.Platform;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO das ganze fromJson sollte eigentlich hier nicht behandelt werden, eher eine Schicht darüber und hier nur durchreichen
public class ClientState {

    private static ClientState instance;
    private final Map<State, StateObserver> stateOberserverMap = new EnumMap<>(State.class);
    private final Gson gson = new Gson();
    private ChatHandler chatHandler;

    private String currentUsername;
    private String currentChatPartner;
    private InetAddress currentChatPartnerAddress;
    private int currentChatPartnerPort;
    private State currentState = State.STRANGER;

    public enum State {STRANGER, SIGNED_UP, SIGNED_IN, CHATTING}

    private ClientState() {
    }

    public static synchronized ClientState getInstance() {
        if (ClientState.instance == null) {
            ClientState.instance = new ClientState();
        }

        return ClientState.instance;
    }

    /**
     * Add observer that needs to navigate to successor on given state
     *
     * @param observer UI Controller
     * @param state    client state
     */
    public void addStateObserver(StateObserver observer, State state) {
        stateOberserverMap.put(state, observer);
    }

    public void addChatHandler(ChatHandler observer) {
        chatHandler = observer;
    }

    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String username) {
        this.currentUsername = username;
    }

    public String getCurrentChatPartner() {
        return currentChatPartner;
    }

    public void setCurrentChatPartner(String currentChatPartner) {
        this.currentChatPartner = currentChatPartner;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State newState) {
        this.currentState = newState;

        var observerForcedToAct = stateOberserverMap.get(currentState);
        if (observerForcedToAct != null) {
            Platform.runLater(observerForcedToAct::navigateToNext);
        }
    }

    public InetAddress getCurrentChatPartnerAddress() {
        return currentChatPartnerAddress;
    }

    public void setCurrentChatPartnerAddress(InetAddress currentChatPartnerAddress) {
        this.currentChatPartnerAddress = currentChatPartnerAddress;
    }

    public int getCurrentChatPartnerPort() {
        return currentChatPartnerPort;
    }

    public void setCurrentChatPartnerPort(int currentChatPartnerPort) {
        this.currentChatPartnerPort = currentChatPartnerPort;
    }

    public void setActiveUsers(String body) {
        var listType = new TypeToken<List<String>>() {
        }.getType();
        List<String> userList = gson.fromJson(body, listType);
        userList = userList.stream()
            .filter(user -> !user.equals(currentUsername))
            .collect(Collectors.toList());

        chatHandler.setUserList(userList);
    }

    public void setFinalChatRequestResponseState(Header.Status status, MessageType messageType, String body) {
        if (chatHandler == null) {
            return;
        }

        var chatRequestState = ChatHandler.ChatRequestState.UNINITIALIZED;
        var errorMessage = "";

        if (messageType == MessageType.INCOMING_CHAT_REQUEST_RESPONSE) {
            if (status == Header.Status.SUCCESS) {
                // request sent, waiting for other user's response
                chatRequestState = ChatRequestState.REQUESTED;
            } else if (status == Header.Status.ERROR) {
                // request failed, message available
                var bodyMessage = gson.fromJson(body, String.class);

                chatRequestState = ChatRequestState.REQUEST_ERROR;
                errorMessage = bodyMessage;
            }
        } else if (messageType == MessageType.FINAL_CHAT_REQUEST_RESPONSE) {
            var bodyValue = gson.fromJson(body, FinalChatRequestResponse.class);

            if (bodyValue.isAccepted()) {
                // start chatting
                chatRequestState = ChatRequestState.ACCEPTED;
                setCurrentChatPartner(bodyValue.getUsernameOfPartner());

                try {
                    setCurrentChatPartnerAddress(InetAddress.getByName(bodyValue.getIpAddress()));
                } catch (UnknownHostException e) {
                    e.printStackTrace(); // TODO
                }
                setCurrentChatPartnerPort(bodyValue.getUdpPort());

                setCurrentState(State.CHATTING);
                // Listener will be informed now
            } else {
                // request was declined
                chatRequestState = ChatRequestState.DECLINED;
                errorMessage = "User declined your request, sorry :(";
            }
        }

        chatHandler.setFinalChatRequestState(chatRequestState, errorMessage);
    }

    public void openChatRequest(String body) {
        var request = gson.fromJson(body, OutgoingChatRequest.class);
        chatHandler.openChatRequest(request.getSender(), request.getSenderIpAddress(), request.getSenderUdpPort());
    }

}
