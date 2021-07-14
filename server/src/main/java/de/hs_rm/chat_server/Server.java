package de.hs_rm.chat_server;

import de.hs_rm.chat_server.communication.Listener;

public class Server {

    static final int PORT = 8080;

    public static void main(String[] args) {
        Listener listener = new Listener(PORT);
        listener.listen();
    }
}
