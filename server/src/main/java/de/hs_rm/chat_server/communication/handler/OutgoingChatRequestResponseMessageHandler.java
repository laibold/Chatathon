package de.hs_rm.chat_server.communication.handler;

import de.hs_rm.chat_server.communication.MessageGenerator;
import de.hs_rm.chat_server.model.chat_message.FinalChatRequestResponse;
import de.hs_rm.chat_server.model.chat_message.OutgoingChatRequestResponse;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.model.message.MessageType;
import de.hs_rm.chat_server.service.ClientService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

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

            // in case client runs on server machine, use network ip instead of localhost
            var clientIpAddress = message.getClient().getIp();
            if (clientIpAddress.equals("127.0.0.1")) {
                try {
                    clientIpAddress = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException ignore) {
                }
            }

            finalChatRequestResponse.setIpAddress(clientIpAddress);
            finalChatRequestResponse.setUdpPort(response.getReceiveUdpPort());
            finalChatRequestResponse.setUsernameOfPartner(response.getRespondingUsername());
        }

        var clientService = ClientService.getInstance();
        var clientSocket = clientService.getClient(response.getRequestedUsername()).getSocket();
        BufferedWriter outToClient = null;

        try {
            outToClient = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();  // wichtig TODO, sonst könnte unten ein NullPointer fliegen
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
