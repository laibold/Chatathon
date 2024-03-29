package de.hs_rm.chat_client.config;

public class Config {
    private Config() {

    }

    public static final String SERVER_IP_ADDRESS = "localhost";
    public static final int SERVER_PORT = 8080;

    public static final int MAXIMUM_UDP_SEGMENT_SIZE = 20;
    public static final double UDP_ERROR_RATE = 0.1;
}
