package de.hs_rm.chat_client.controller;

import java.util.List;

public interface UserListObserver {

    void setUserList(List<String> userList);
}
