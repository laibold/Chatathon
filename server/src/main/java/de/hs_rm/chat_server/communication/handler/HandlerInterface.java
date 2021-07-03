package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.model.header.MessageType;

public interface HandlerInterface {

    String handle(String message);

    MessageType getResponseMessageType();

}
