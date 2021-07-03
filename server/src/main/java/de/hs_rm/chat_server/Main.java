package de.hs_rm.chat_server;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello chatters!");

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
