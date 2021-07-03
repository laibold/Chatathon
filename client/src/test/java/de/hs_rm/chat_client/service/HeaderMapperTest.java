package de.hs_rm.chat_client.service;

import de.hs_rm.chat_client.model.header.Header;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.header.MessageType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeaderMapperTest {

    @Test
    void canMapJsonToHeader() throws InvalidHeaderException {
        var json = "{\"messageType\":\"CHAT_REQUEST\",\"contentLength\":123}";
        var header = HeaderMapper.toHeader(json);

        assertEquals(MessageType.CHAT_REQUEST, header.getMessageType());
        assertEquals(123, header.getContentLength());
    }

    @Test
    void canMapHeaderToJsonString() throws InvalidHeaderException {
        var header = new Header(MessageType.CHAT_REQUEST, 123);
        var json = HeaderMapper.toJsonString(header);

        assertEquals("{\"messageType\":\"CHAT_REQUEST\",\"contentLength\":123}", json);
    }

    @Test
    void throwsExceptionOnInvalidHeader() {
        var header1 = new Header(null, 123);
        var header2 = new Header(MessageType.CHAT_REQUEST, 0);

        assertThrows(InvalidHeaderException.class, () ->
            HeaderMapper.toJsonString(header1)
        );

        assertThrows(InvalidHeaderException.class, () ->
            HeaderMapper.toJsonString(header2)
        );
    }

    @Test
    void throwsExceptionOnInvalidJson() {
        var json1 = "{\"messageType\":\"SCHÄTT\",\"contentLength\":123}";
        var json2 = "{\"messageType\":\"CHAT_REQUEST\",\"contentLengt\":123}";
        var json3 = "{\"messageTypeCHAT_REQUEST\",\"contentLengt\":123}";

        assertThrows(InvalidHeaderException.class, () ->
            HeaderMapper.toHeader(json1)
        );

        assertThrows(InvalidHeaderException.class, () ->
            HeaderMapper.toHeader(json2)
        );

        assertThrows(InvalidHeaderException.class, () ->
            HeaderMapper.toHeader(json3)
        );
    }
}
