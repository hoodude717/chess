package service;

import dataaccess.*;
import model.UserData;
import org.eclipse.jetty.util.log.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.serviceRequests.LoginRequest;
import service.serviceRequests.LogoutRequest;
import service.serviceRequests.RegisterRequest;
import service.serviceResults.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();


    UserService userService = new UserService(authDAO, gameDAO, userDAO);
    ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);

    @BeforeEach
    void setClearDatabases() {
        clearService.clear();
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
//        Assertions.assertDoesNotThrow(() -> userService.register(
//                new RegisterRequest("bradford", "12345", "bradford@byu.edu")));


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
//        Assertions.assertDoesNotThrow(() -> userService.register(
//                new RegisterRequest("bradford", "12345", "bradford@byu.edu")));


        Assertions.assertThrows(DataAccessException.class,
                () -> userService.login(new LoginRequest("bradford", "1234")));


    }

    @Test
    void failedLogout() {
        Assertions.assertThrows(UnauthorizedException.class,
                () -> userService.logout(new LogoutRequest("123456789")));

    }
}