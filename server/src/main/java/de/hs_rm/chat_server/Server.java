package de.hs_rm.chat_server;

import de.hs_rm.chat_server.communication.Listener;

public class Server {

    final static int PORT = 8080;

    public static void main(String[] args) {
        // TODO: Ist die Datenbank erreichbar?

        Listener listener = new Listener(PORT);
        listener.listen();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
