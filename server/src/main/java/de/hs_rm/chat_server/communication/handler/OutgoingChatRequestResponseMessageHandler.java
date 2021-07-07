package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.message.*;
import de.hs_rm.chat_server.service.ClientService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class OutgoingChatRequestResponseMessageHandler extends MessageHandler {
    @Override
    public String handle(Message message) {

        var response = gson.fromJson(message.getBody(), OutgoingChatRequestResponse.class);
        var status = Header.Status.SUCCESS;

        var finalChatRequestResponse = new FinalChatRequestResponse();

        if (!response.getAccepted()) {
            finalChatRequestResponse.setAccepted(false);
        } else {
            finalChatRequestResponse.setAccepted(true);
            finalChatRequestResponse.setIp(message.getClient().getIp());
            finalChatRequestResponse.setPort(message.getClient().getPort());
            finalChatRequestResponse.setUsernameOfPartner(response.getUsername());
        }

        var clientService = ClientService.getInstance();
        var clientSocket = clientService.getClient(response.getUsername()).getSocket();
        BufferedWriter outToClient = null;

        try {
            outToClient = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String outgoingRequest = null;

        try {
            outgoingRequest = MessageGenerator.generateMessage(status, MessageType.FINAL_CHAT_REQUEST_RESPONSE, finalChatRequestResponse);
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

        return null;
    }
}
