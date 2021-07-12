package de.hs_rm.chat_client.controller.chat;

public interface ChatErrorHandler {
    void notifyAboutSendingError(String message);
}
