package de.hs_rm.chat_server.communication;

import com.google.gson.Gson;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.service.HeaderMapper;

public class MessageGenerator {

    /*
     *  TODO: Gson wird mehrmals instanziiert: MessageGenerator, HeaderMapper und in den Handlers.
     *  Wir können den bestimmt in eine einzige Klasse "JSONHelper" oder sowas auslagern.
     */
    private static final Gson gson = new Gson();

    public static String generateMessage(Header.Status status, MessageType messageType, Object body) throws InvalidHeaderException {
        var bodyJsonString = gson.toJson(body);
        var header = new Header(messageType, bodyJsonString.length(), status);
        var headerString = HeaderMapper.toJsonString(header);

        return headerString + "\n" + bodyJsonString;
    }
}
