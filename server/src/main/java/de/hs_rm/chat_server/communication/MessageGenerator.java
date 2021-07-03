package de.hs_rm.chat_server.communication;

import com.google.gson.Gson;
import de.hs_rm.chat_server.model.header.Header;
import de.hs_rm.chat_server.model.header.InvalidHeaderException;
import de.hs_rm.chat_server.model.header.MessageType;
import de.hs_rm.chat_server.service.HeaderMapper;

public class MessageGenerator {

    private MessageGenerator() {
    }

    private static final Gson gson = new Gson();

    public static String generateMessage(MessageType messageType, Object body) throws InvalidHeaderException {
        var bodyJsonString = gson.toJson(body);
        var header = new Header(messageType, bodyJsonString.length());
        var headerString = HeaderMapper.toJsonString(header);

        return headerString + "\n" + bodyJsonString;
    }
}
