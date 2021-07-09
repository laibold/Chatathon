package de.hs_rm.chat_client.controller.chat;

import java.util.List;

public interface ChatHandler {

    enum ChatRequestState {UNINITIALIZED, REQUESTED, REQUEST_ERROR, DECLINED, ACCEPTED}

    void setFinalChatRequestState(ChatRequestState state, String message);

    void setUserList(List<String> userList);

    void openChatRequest(String senderUsername, String senderIpAddress, int senderUdpPort);
}
