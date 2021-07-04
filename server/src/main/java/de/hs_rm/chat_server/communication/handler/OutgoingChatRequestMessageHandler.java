package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.MessageType;

import java.io.DataOutputStream;
import java.io.IOException;

public class OutgoingChatRequestMessageHandler {

    public String handle(Client client) {
        var clientSocket = client.getSocket();
        DataOutputStream outToClient = null;

        try {
            outToClient = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String outgoingRequest = null;

        try {
            outgoingRequest = MessageGenerator.generateMessage(Header.Status.SUCCESS, MessageType.OUTGOING_CHAT_REQUEST, "Client A wants to chat with you");
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        System.out.println("OUTGOING (" + clientSocket.getRemoteSocketAddress().toString() + "):\t" + outgoingRequest);
        try {
            outToClient.writeBytes(outgoingRequest + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }
}
