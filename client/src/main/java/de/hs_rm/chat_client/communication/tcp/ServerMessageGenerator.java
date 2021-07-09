package de.hs_rm.chat_client.communication.tcp;

import com.google.gson.Gson;
import de.hs_rm.chat_client.model.tcp.message.Header;
import de.hs_rm.chat_client.model.tcp.message.InvalidHeaderException;
import de.hs_rm.chat_client.model.tcp.message.MessageType;
import de.hs_rm.chat_client.service.HeaderMapper;

public class ServerMessageGenerator {

    private ServerMessageGenerator() {
    }

    private static final Gson gson = new Gson();

    public static String generateMessage(MessageType messageType, Object body) throws InvalidHeaderException {
        var bodyJsonString = gson.toJson(body);
        var header = new Header(messageType, bodyJsonString.length());
        var headerString = HeaderMapper.toJsonString(header);

        return headerString + "\n" + bodyJsonString;
    }
}
