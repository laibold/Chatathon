package de.hs_rm.chat_client.model.message;

public class InvalidHeaderException extends Exception{

    public InvalidHeaderException() {
    }

    public InvalidHeaderException(String message) {
        super(message);
    }
}
