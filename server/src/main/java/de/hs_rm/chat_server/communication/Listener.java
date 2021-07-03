package de.hs_rm.chat_server.communication;

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

                        // TODO: Parse requests

                        // TODO: Forward request - differentiate by using Enum


                    } catch (IOException e) {
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
