package de.hs_rm.chat_client.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClientState {

    private static ClientState instance;
    private final Map<State, StateObserver> stateOberserverMap = new EnumMap<>(State.class);
    private final ArrayList<UserListObserver> userListObserverList = new ArrayList<>();
    private String currentUser;

    private State currentState = State.STRANGER;

    public String getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(String username) {
        this.currentUser = username;
    }

    public enum State {STRANGER, SIGNED_UP, SIGNED_IN}

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

    public void addUserListObserver(UserListObserver observer) {
        userListObserverList.add(observer);
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

    public void setActiveUsers(String body) {
        var gson = new Gson();

        var listType = new TypeToken<List<String>>() {
        }.getType();
        List<String> userList = gson.fromJson(body, listType);
        userList = userList.stream()
            .filter(user -> !user.equals(currentUser))
            .collect(Collectors.toList());

        for (var observer : userListObserverList) {
            observer.setUserList(userList);
        }
    }
}
