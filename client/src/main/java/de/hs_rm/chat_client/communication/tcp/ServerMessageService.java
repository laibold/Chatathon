package de.hs_rm.chat_client.communication.tcp;

import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.model.tcp.chat_message.IncomingChatRequest;
import de.hs_rm.chat_client.model.tcp.chat_message.OutgoingChatRequestResponse;
import de.hs_rm.chat_client.model.tcp.message.Header;
import de.hs_rm.chat_client.model.tcp.message.InvalidHeaderException;
import de.hs_rm.chat_client.model.tcp.message.MessageType;
import de.hs_rm.chat_client.model.tcp.user.User;
import de.hs_rm.chat_client.service.HeaderMapper;

import java.io.*;
import java.net.Socket;

public class ServerMessageService {

    private static final String REMOTE_HOST = "localhost";
    private static final int REMOTE_PORT = 8080;

    private static ServerMessageService instance;

    private final Socket socket;
    private final BufferedWriter writer;
    private final ClientState clientState;

    private ServerMessageService() throws IOException {
        socket = new Socket(REMOTE_HOST, REMOTE_PORT);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        clientState = ClientState.getInstance();
        listen();
    }

    public static synchronized ServerMessageService getInstance() {
        if (ServerMessageService.instance == null) {
            try {
                ServerMessageService.instance = new ServerMessageService();
            } catch (IOException e) {
                System.err.println("Could not create socket: " + e.getMessage());
            }
        }

        return ServerMessageService.instance;
    }

    public void sendSignUpMessage(User user) throws InvalidHeaderException, IOException {
        var message = ServerMessageGenerator.generateMessage(MessageType.SIGN_UP, user);

        writeMessage(message);
    }

    public void sendSignInMessage(User user) throws InvalidHeaderException, IOException {
        var message = ServerMessageGenerator.generateMessage(MessageType.SIGN_IN, user);

        writeMessage(message);
    }

    public void sendActiveUserListRequest() throws InvalidHeaderException, IOException {
        var message = ServerMessageGenerator.generateMessage(MessageType.LIST_ACTIVE_USERS, "");

        writeMessage(message);
    }

    public void sendChatRequest(String recipient, int udpReceivePort) throws InvalidHeaderException, IOException {
        var incomingChatRequest = new IncomingChatRequest(clientState.getCurrentUsername(), recipient, udpReceivePort);
        var message = ServerMessageGenerator.generateMessage(MessageType.INCOMING_CHAT_REQUEST, incomingChatRequest);

        writeMessage(message);
    }

    public void sendChatRequestResponse(String respondingUsername, String requestedUsername, boolean accepted, int receiveUdpPort) throws InvalidHeaderException, IOException {
        var response = new OutgoingChatRequestResponse(respondingUsername, requestedUsername, accepted, receiveUdpPort);
        var message = ServerMessageGenerator.generateMessage(MessageType.OUTGOING_CHAT_REQUEST_RESPONSE, response);

        writeMessage(message);
    }

    public void sendSignOut() throws InvalidHeaderException, IOException {
        var message = ServerMessageGenerator.generateMessage(MessageType.SIGN_OUT, clientState.getCurrentUsername());

        writeMessage(message);
    }

    private void writeMessage(String messageString) throws IOException {
        System.out.println("Sending message to Server:\n" + messageString + "\n");
        writer.write(messageString);
        writer.flush();
    }

    // TODO hier bisschen auslagern und direkt in Threads auslagern
    private void listen() {
        new Thread(() -> {
            while (true) {
                try {
                    var inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    var line = inFromServer.readLine();

                    if (line != null) {
                        Header header = null;

                        try {
                            header = HeaderMapper.toHeader(line);
                        } catch (InvalidHeaderException e) {
                            e.printStackTrace();
                        }

                        assert header != null;
                        var chars = new char[header.getContentLength()];

                        String body;
                        var charsRead = inFromServer.read(chars, 0, header.getContentLength());
                        if (charsRead != -1) {
                            body = new String(chars, 0, charsRead);
                        } else {
                            body = null;
                        }

                        System.out.println("Received Message from Server:");
                        System.out.println(header);
                        System.out.println(body + "\n");

                        if (header.getStatus() == Header.Status.SUCCESS) {
                            switch (header.getMessageType()) {
                                case SIGN_UP_RESPONSE:
                                    clientState.setCurrentState(ClientState.State.SIGNED_UP);
                                    break;
                                case SIGN_IN_RESPONSE:
                                    clientState.setCurrentState(ClientState.State.SIGNED_IN);
                                    break;
                                case LIST_ACTIVE_USERS_RESPONSE:
                                    if (clientState.getCurrentState() == ClientState.State.SIGNED_IN) {
                                        clientState.setActiveUsers(body);
                                    }
                                    break;
                                case INCOMING_CHAT_REQUEST_RESPONSE:
                                case FINAL_CHAT_REQUEST_RESPONSE:
                                    clientState.setFinalChatRequestResponseState(header.getStatus(), header.getMessageType(), body);
                                    break;
                                case OUTGOING_CHAT_REQUEST:
                                    clientState.openChatRequest(body);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            // TODO error handling und dann evl beide switches zusammenfassen
                            switch (header.getMessageType()) {
                                case INCOMING_CHAT_REQUEST_RESPONSE:
                                    clientState.setFinalChatRequestResponseState(header.getStatus(), header.getMessageType(), body);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
