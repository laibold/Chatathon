package de.hs_rm.chat_server.model.message;

public enum MessageType {
    SIGN_UP,
    SIGN_UP_RESPONSE,

    SIGN_IN,
    SIGN_IN_RESPONSE,

    SIGN_OUT,

    LIST_ACTIVE_USERS,
    LIST_ACTIVE_USERS_RESPONSE,

    INCOMING_CHAT_REQUEST,
    INCOMING_CHAT_REQUEST_RESPONSE,

    OUTGOING_CHAT_REQUEST,
    OUTGOING_CHAT_REQUEST_RESPONSE,

    FINAL_CHAT_REQUEST_RESPONSE
}
