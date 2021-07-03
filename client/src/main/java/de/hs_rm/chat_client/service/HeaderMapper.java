package de.hs_rm.chat_client.service;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import de.hs_rm.chat_client.model.header.Header;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;

public class HeaderMapper {

    private static final Gson gson = new Gson();

    public static Header toHeader(String json) throws InvalidHeaderException {
        try {
            var header = gson.fromJson(json, Header.class);

            if (header.getMessageType() == null) {
                throw new InvalidHeaderException("MessageType not existing.");
            }

            if (header.getContentLength() == 0) {
                throw new InvalidHeaderException("Content length must not be 0.");
            }

            return header;
        } catch (JsonSyntaxException e) {
            throw new InvalidHeaderException(e.getMessage());
        }
    }

    public static String toJsonString(Header header) throws InvalidHeaderException {
        if (header.getMessageType() == null) {
            throw new InvalidHeaderException("MessageType not existing.");
        }

        if (header.getContentLength() == 0) {
            throw new InvalidHeaderException("Content length must not be 0.");
        }

        return gson.toJson(header);
    }
}
