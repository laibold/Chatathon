package de.hs_rm.chat_server.communication.handler;

import com.google.gson.Gson;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;

public abstract class MessageHandler {

    protected final Gson gson = new Gson();

    public abstract String handle(Message message);

    public abstract MessageType getResponseMessageType();

}
