package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.model.header.InvalidHeaderException;
import de.hs_rm.chat_server.service.HeaderMapper;

import java.io.*;
import java.net.ServerSocket;

public class Listener {
    private ServerSocket serverSocket;
    private int port;

    public Listener(int port) {
        try {
            this.port = port;
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void listen() {
        System.out.println("LISTENING ON PORT:\t" + this.port);

        while (true) {
            try {
                final var socket = serverSocket.accept();
                System.out.println("CLIENT CONNECTED:\t" + socket.getRemoteSocketAddress());

                final var thread = new Thread(() -> {
                    final BufferedReader reader;
                    try {
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        // Parse header to get body length
                        var headerString = reader.readLine();

                        // TODO: Abfangen: if (line == null || line.equals(""))
                        var header = HeaderMapper.toHeader(headerString);
                        var chars = new char[header.getContentLength()];

                        String body;
                        var charsRead = reader.read(chars, 0, header.getContentLength());
                        if (charsRead != -1) {
                            body = new String(chars, 0, charsRead);
                        } else {
                            body = "";
                        }

                        // TODO: Forward message type and body to MessageHandler


                    } catch (IOException | InvalidHeaderException e) {
                        e.printStackTrace();
                    }
                });
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
