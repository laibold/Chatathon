package de.hs_rm.chat_server.service;

import de.hs_rm.chat_server.model.user.User;
import de.hs_rm.chat_server.model.user.UserAlreadyExistsException;
import de.hs_rm.chat_server.model.user.UserNotFoundException;
import de.hs_rm.chat_server.persistence.Database;

import java.sql.SQLException;

public class UserService {

    private static UserService instance;
    private final Database database;

    private UserService() {
        this.database = Database.getInstance();
    }

    public static synchronized UserService getInstance() {
        if (UserService.instance == null) {
            UserService.instance = new UserService();
        }

        return UserService.instance;
    }

    public void insertUser(User user) throws PersistenceException, UserAlreadyExistsException {
        var queriedUser = getUser(user);

        if (queriedUser != null) {
            throw new UserAlreadyExistsException();
        }

        try {
            database.insertUser(user.getUsername(), user.getPassword());
        } catch (SQLException e) {
            throw new PersistenceException(
                    "SQL Exception while inserting user with username " +
                            user.getUsername() + ": " +
                            e.getMessage()
            );
        }
    }

    public boolean checkUserCredentials(User user) throws UserNotFoundException, PersistenceException {
        var queriedUser = getUser(user);

        if (queriedUser == null) {
            throw new UserNotFoundException("User with username " + user.getUsername() + " not found.");
        }

        return queriedUser.getUsername().equals(user.getUsername()) && queriedUser.getPassword().equals(user.getPassword());
    }

    private User getUser(User user) throws PersistenceException {
        User queriedUser;

        try {
            queriedUser = database.getUser(user.getUsername());
        } catch (SQLException e) {
            throw new PersistenceException(
                    "SQL Exception while querying user with username " +
                            user.getUsername() + ": " +
                            e.getMessage()
            );
        }

        return queriedUser;
    }

    public User getUserByName(String name) {
        User queriedUser = null;
        try {
            queriedUser = database.getUser(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return queriedUser;
    }
}
