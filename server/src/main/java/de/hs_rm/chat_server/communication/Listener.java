package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.model.client.Client;
import de.hs_rm.chat_server.model.message.Header;
import de.hs_rm.chat_server.model.message.InvalidHeaderException;
import de.hs_rm.chat_server.model.message.Message;
import de.hs_rm.chat_server.service.HeaderMapper;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Listener {
    private ServerSocket welcomeSocket;

    public Listener(int port) {
        try {
            this.welcomeSocket = new ServerSocket(port);
            System.out.println("Warte auf Client...");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        while (true) {
            try {
                final var connectionSocket = this.welcomeSocket.accept();
                System.out.println("Client hat sich verbunden: " + connectionSocket.getInetAddress() + "\n");

                new Thread(() -> {
                    BufferedReader inFromClient = null;
                    DataOutputStream outToClient = null;

                    try {
                        inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                        outToClient = new DataOutputStream(connectionSocket.getOutputStream());
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

    private void handleRequests(final Socket connectionSocket, final BufferedReader inFromClient, final DataOutputStream outToClient) {
        var connected = true;

        while (connected) {
            final String line;
            try {
                line = inFromClient.readLine();
                if (line != null) {
                    //
                    // PARSE HEADER AND BODY
                    //

                    Header header = null;
                    try {
                        header = HeaderMapper.toHeader(line);
                    } catch (InvalidHeaderException e) {
                        e.printStackTrace();
                    }

                    assert header != null;
                    var chars = new char[header.getContentLength()];

                    String body;
                    var charsRead = inFromClient.read(chars, 0, header.getContentLength());
                    if (charsRead != -1) {
                        body = new String(chars, 0, charsRead);
                    } else {
                        body = null;
                    }

                    var clientStr = connectionSocket.getRemoteSocketAddress() + ":" + connectionSocket.getLocalPort();
                    System.out.printf("Vom Client %s empfangen:\n%s \n%s \n\n", clientStr, header, body);

                    //
                    // RESPONSE
                    //

                    // Brauchen wir "outToClient.flush();"?

                    var messageTypeHandler = new MessageTypeHandler();

                    var client = new Client(connectionSocket.getRemoteSocketAddress(), connectionSocket.getLocalPort());
                    var message = new Message(header, body, client);

                    var response = messageTypeHandler.handleMessage(message);
                    System.out.printf("Sende an Client (%s):\n%s%n\n", connectionSocket.getRemoteSocketAddress(), response);
                    outToClient.writeBytes(response + "\n");
                } else {
                    connected = false;
                }
            } catch (IOException | InvalidHeaderException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
