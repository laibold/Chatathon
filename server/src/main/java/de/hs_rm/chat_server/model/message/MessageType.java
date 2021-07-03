package de.hs_rm.chat_server.model.message;

public enum MessageType {
    SIGN_UP,
    SIGN_UP_RESPONSE,

    SIGN_IN,
    SIGN_IN_RESPONSE,

    SIGN_OUT,

    LIST_ACTIVE_USERS,
    LIST_ACTIVE_USERS_RESPONSE,

    CHAT_REQUEST,
    CHAT_REQUEST_RESPONSE,

    FORWARD_CHAT_REQUEST,
    FORWARD_CHAT_REQUEST_RESPONSE
}
