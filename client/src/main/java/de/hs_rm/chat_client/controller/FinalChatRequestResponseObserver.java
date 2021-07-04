package de.hs_rm.chat_client.controller;

public interface FinalChatRequestResponseObserver {

    enum ChatRequestState {UNINITIALIZED, REQUESTED, REQUEST_ERROR, DECLINED, ACCEPTED}

    void setFinalChatRequestState(ChatRequestState state, String message);
}
