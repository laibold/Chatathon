package de.hs_rm.chat_client.controller.chat;

public interface ChatMessageReceiver {
    void addIncomingChatMessage(String sender, String message);
}
