package de.hs_rm.chat_server;

public class Server {

    public static void main(String[] args) {
        // TODO: Ist die Datenbank erreichbar?

        int port = 8080;

        // Listener listener = new Listener(port);
        // listener.listen();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
