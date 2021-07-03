package de.hs_rm.chat_server.communication;

import de.hs_rm.chat_server.model.header.Header;
import de.hs_rm.chat_server.model.header.InvalidHeaderException;
import de.hs_rm.chat_server.service.HeaderMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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

                new Thread(() -> {
                    final BufferedReader reader;
                    try {
                        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        final var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                        handleRequests(socket, reader, writer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleRequests(final Socket socket, final BufferedReader reader, final BufferedWriter writer) {
        while (true) {
            try {
                // Parse header to get body length
                var headerString = reader.readLine();

                if (headerString == null || headerString.equals("")) {
                    return; // TODO
                }

                Header header = null;
                try {
                    header = HeaderMapper.toHeader(headerString);
                } catch (InvalidHeaderException e) {
                    e.printStackTrace(); // TODO
                }

                assert header != null;
                var chars = new char[header.getContentLength()];

                String body;
                var charsRead = reader.read(chars, 0, header.getContentLength());
                if (charsRead != -1) {
                    body = new String(chars, 0, charsRead);
                } else {
                    body = "";
                }

                System.out.println("INCOMING (" + socket.getRemoteSocketAddress().toString() + "):\t" + headerString + " " + body);
                // TODO: Forward message type and body to MessageHandler

                writer.write("ok\n");
                writer.flush();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                return;
            }
        }
    }
}
