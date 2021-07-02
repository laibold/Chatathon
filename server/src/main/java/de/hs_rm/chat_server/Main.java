package de.hs_rm.chat_server;

import de.hs_rm.chat_server.persistence.Database;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello chatters!");
        var database = Database.getInstance();
    }
}
