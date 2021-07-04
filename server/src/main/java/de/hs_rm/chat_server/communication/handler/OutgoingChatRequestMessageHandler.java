package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.MessageType;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class OutgoingChatRequestMessageHandler {

    public void sendOutgoingChatRequestMessage(Client recipient, String username) {
        var clientSocket = recipient.getSocket();
        BufferedWriter outToClient = null;

        try {
            outToClient = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String outgoingRequest = null;

        try {
            outgoingRequest = MessageGenerator.generateMessage(Header.Status.SUCCESS, MessageType.OUTGOING_CHAT_REQUEST, username);
        } catch (InvalidHeaderException e) {
            e.printStackTrace();
        }

        System.out.println("OUTGOING (" + clientSocket.getRemoteSocketAddress().toString() + "):\t" + outgoingRequest);
        try {
            outToClient.write(outgoingRequest + "\n");
            outToClient.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
