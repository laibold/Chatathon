package de.hs_rm.chat_client.controller;

import javafx.application.Platform;

import java.util.EnumMap;
import java.util.Map;

public class ClientState {

    private static ClientState instance;
    private final Map<State, StateObserver> stateOberserverMap = new EnumMap<>(State.class);
    private State currentState = State.STRANGER;

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
    public void addObserver(StateObserver observer, State state) {
        stateOberserverMap.put(state, observer);
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
}
