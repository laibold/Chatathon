package de.hs_rm.chat_client.communication;

import de.hs_rm.chat_client.model.header.InvalidHeaderException;
import de.hs_rm.chat_client.model.header.MessageType;
import de.hs_rm.chat_client.model.user.User;

import java.io.*;
import java.net.Socket;

public class MessageService {

    private static final String REMOTE_HOST = "localhost";
    private static final int REMOTE_PORT = 8080;

    private static MessageService instance;

    private final Socket socket;
    private final BufferedWriter writer;

    private MessageService() throws IOException {
        socket = new Socket(REMOTE_HOST, REMOTE_PORT);
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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

    private void writeMessage(String messageString) throws IOException {
        writer.write(messageString);
        writer.flush();
    }

    private void listen() {
        new Thread(() -> {
            while (true) {
                try {
                    var serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    var response = serverIn.readLine();
                    System.out.println(response);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
