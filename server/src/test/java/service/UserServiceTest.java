package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.servicerequests.LoginRequest;
import service.servicerequests.LogoutRequest;
import service.servicerequests.RegisterRequest;
import service.serviceresults.RegisterResult;


class UserServiceTest {

    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();


    UserService userService = new UserService(authDAO, userDAO);

    @BeforeEach
    void setClearDatabases() {
        userService.clear();
    }

    @Test
    void successfulRegister() {
        //We want to test if the register function throws an exception
        // We also want to test if the register function adds a person to the database and returns the right things
        Assertions.assertDoesNotThrow(() -> userService.register(
                new RegisterRequest("bradford", "12345", "bradford@byu.edu")));


    }

    @Test
    void successfulLogin() {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(
                new UserData("bradford", "12345", "bradford@byu.edu")));

        Assertions.assertDoesNotThrow(() -> userService.login(new LoginRequest("bradford", "12345")));

    }

    @Test
    void successfulLogout() {
        RegisterResult result;
        try {
            result = userService.register(
                    new RegisterRequest("bradford", "12345", "bradford@byu.edu"));
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed Register", e);
        }

        Assertions.assertDoesNotThrow(() -> userService.logout(new LogoutRequest(result.authToken())));
    }


    @Test
    void failedRegister() {
        try {
            userService.register(
                    new RegisterRequest("bradford", "12345", "bradford@byu.edu"));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertThrows(DataAccessException.class, () -> userService.register(
                new RegisterRequest("bradford", "12345", "bradford@byu.edu")));
    }

    @Test
    void failedLogin() {

        Assertions.assertDoesNotThrow(() -> userDAO.createUser(
                new UserData("bradford", "12345", "bradford@byu.edu")));

        Assertions.assertThrows(DataAccessException.class,
                () -> userService.login(new LoginRequest("bradford", "1234")));
    }

    @Test
    void failedLogout() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.logout(new LogoutRequest("123456789")));
    }
}