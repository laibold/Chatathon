package de.hs_rm.chat_server.model.header;

public class InvalidHeaderException extends Exception{

    public InvalidHeaderException() {
    }

    public InvalidHeaderException(String message) {
        super(message);
    }
}
