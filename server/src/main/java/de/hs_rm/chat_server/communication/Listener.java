package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.service.HeaderMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Listener {
    private ServerSocket welcomeSocket;
    private MessageTypeHandler messageTypeHandler;


    public Listener(int port) {
        try {
            // socket for handshake with every client
            this.welcomeSocket = new ServerSocket(port);
            this.messageTypeHandler = new MessageTypeHandler();
            System.out.println("Waiting for client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        // listening loop
        while (true) {
            try {
                // create communication socket for each connected client
                final var connectionSocket = this.welcomeSocket.accept();
                System.out.println("Client connected: " + connectionSocket.getInetAddress().getHostAddress() + "\n");

                // start thread for reading from and writing to client
                new Thread(() -> {
                    BufferedReader inFromClient = null;
                    BufferedWriter outToClient = null;

                    try {
                        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream(), StandardCharsets.UTF_8));
                        outToClient = new BufferedWriter(new OutputStreamWriter(connectionSocket.getOutputStream(), StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    handleRequests(connectionSocket, inFromClient, outToClient);
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequests(final Socket connectionSocket, final BufferedReader inFromClient, final BufferedWriter outToClient) {
        var connected = true;

        while (connected && connectionSocket.isConnected()) {
            final String line;
            try {
                // first line marks the header
                line = inFromClient.readLine();
                if (line != null) {
                    // parse header to object
                    Header header = null;
                    try {
                        header = HeaderMapper.toHeader(line);
                    } catch (InvalidHeaderException e) {
                        e.printStackTrace();
                    }

                    assert header != null;
                    // create char array with size of body
                    var chars = new char[header.getContentLength()];

                    // read chars from body to String
                    String body;
                    var charsRead = inFromClient.read(chars, 0, header.getContentLength());
                    if (charsRead != -1) {
                        body = new String(chars, 0, charsRead);
                    } else {
                        body = null;
                    }

                    var clientStr = connectionSocket.getRemoteSocketAddress();
                    System.out.printf("Received from client %s\n%s \n%s \n\n", clientStr, header, body);

                    var client = new Client(connectionSocket);
                    var message = new Message(header, body, client);

                    // create response string (contains header and body) from responsible handler
                    var response = messageTypeHandler.handleMessage(message);

                    if (response != null) {
                        System.out.printf("Send to client %s\n%s%n\n", connectionSocket.getRemoteSocketAddress(), response);
                        outToClient.write(response + "\n");
                        outToClient.flush();
                    }
                } else {
                    System.out.printf("Client disconnected: %s", connectionSocket.getRemoteSocketAddress());
                    connected = false;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
