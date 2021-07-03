package de.hs_rm.chat_server.service;

import de.hs_rm.chat_server.domain.user.User;
import de.hs_rm.chat_server.model.user.UserAlreadyExistsException;
import de.hs_rm.chat_server.model.user.UserNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void canInstantiate() {
        var instance1 = UserService.getInstance();
        var instance2 = UserService.getInstance();

        assertEquals(instance1, instance2);
    }

    @Test
    void canInsertAndGetUser() throws PersistenceException, UserAlreadyExistsException, UserNotFoundException {
        var user = generateRandomUser();
        var userService = UserService.getInstance();
        userService.insertUser(user);

        var matches = userService.checkUserCredentials(user);

        assertTrue(matches);
    }

    @Test
    void throwsExceptionWhenUserAlreadyExists() throws PersistenceException, UserAlreadyExistsException, UserNotFoundException {
        var user = generateRandomUser();
        var userService = UserService.getInstance();
        userService.insertUser(user);

        assertThrows(UserAlreadyExistsException.class, () ->
            userService.insertUser(user)
        );
    }

    @Test
    void throwsExceptionWhenNotExisting() throws PersistenceException, UserAlreadyExistsException, UserNotFoundException {
        var user = new User(".", ",");
        var userService = UserService.getInstance();

        assertThrows(UserNotFoundException.class, () ->
            userService.checkUserCredentials(user)
        );
    }

    @Test
    void canDetectWrongPassword() throws PersistenceException, UserAlreadyExistsException, UserNotFoundException {
        var user = generateRandomUser();
        var userService = UserService.getInstance();
        userService.insertUser(user);

        user.setPassword(user.getPassword() + "wrong");

        var matches = userService.checkUserCredentials(user);

        assertFalse(matches);
    }

    private User generateRandomUser() {
        var username = Long.toHexString(Double.doubleToLongBits(Math.random()));
        var password = Long.toHexString(Double.doubleToLongBits(Math.random()));
        return new User(username, password);
    }

}
