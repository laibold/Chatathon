package de.hs_rm.chat_client.controller;

import java.util.List;

public interface ChatHandler {

    enum ChatRequestState {UNINITIALIZED, REQUESTED, REQUEST_ERROR, DECLINED, ACCEPTED}

    void setFinalChatRequestState(ChatRequestState state, String message);

    void setUserList(List<String> userList);
}
