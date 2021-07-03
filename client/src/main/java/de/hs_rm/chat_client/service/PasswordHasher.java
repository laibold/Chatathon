package de.hs_rm.chat_client.service;

import java.util.Base64;

public class PasswordHasher {

    private PasswordHasher() {
    }

    /**
     * Wir wissen, dass das nicht sicher ist, nur zum Späßchen
     */
    public static String getHashedPassword(String password) {
        return Base64.getEncoder().encodeToString(password.getBytes()).replace("=", "cmr");
    }
}
