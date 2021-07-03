package de.hs_rm.chat_client.communication;

import de.hs_rm.chat_client.controller.ClientState;
import de.hs_rm.chat_client.model.header.Header;
import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.header.MessageType;
import de.hs_rm.chat_client.model.user.User;
import de.hs_rm.chat_client.service.HeaderMapper;

import java.io.*;
import java.net.Socket;

public class MessageService {

    private static final String REMOTE_HOST = "localhost";
    private static final int REMOTE_PORT = 8080;

    private static MessageService instance;

    private final Socket socket;
    private final BufferedWriter writer;
    private final ClientState clientState;

    private MessageService() throws IOException {
        socket = new Socket(REMOTE_HOST, REMOTE_PORT);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        clientState = ClientState.getInstance();
        listen();
    }

    public static synchronized MessageService getInstance() {
        if (MessageService.instance == null) {
            try {
                MessageService.instance = new MessageService();
            } catch (IOException e) {
                System.err.println("Could not create socket: " + e.getMessage());
            }
        }

        return MessageService.instance;
    }

    public void sendSignUpMessage(User user) throws InvalidHeaderException, IOException {
        var message = MessageGenerator.generateMessage(MessageType.SIGN_UP, user);

        writeMessage(message);
    }

    public void sendSignInMessage(User user) throws InvalidHeaderException, IOException {
        var message = MessageGenerator.generateMessage(MessageType.SIGN_IN, user);

        writeMessage(message);
    }

    public void sendActiveUserListRequest() throws InvalidHeaderException, IOException {
        var message = MessageGenerator.generateMessage(MessageType.LIST_ACTIVE_USERS, "");

        writeMessage(message);
    }

    private void writeMessage(String messageString) throws IOException {
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

                        if (header.getStatus() == Header.Status.SUCCESS) {
                            switch (header.getMessageType()) {
                                case SIGN_UP_RESPONSE:
                                    clientState.setCurrentState(ClientState.State.SIGNED_UP);
                                    break;
                                case SIGN_IN_RESPONSE:
                                    clientState.setCurrentState(ClientState.State.SIGNED_IN);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            // TODO error handling
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
