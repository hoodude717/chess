package service;

import dataaccess.AuthDAO;
import exceptions.BadRequestException;
import exceptions.DataAccessException;
import dataaccess.UserDAO;
import exceptions.UnauthorizedException;
import model.AuthData;
import model.UserData;
import servicerequests.LoginRequest;
import servicerequests.LogoutRequest;
import servicerequests.RegisterRequest;
import serviceresults.LoginResult;
import serviceresults.RegisterResult;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if (username == null || password == null || email == null) {
            throw new BadRequestException("Error: Bad Request");
        }
        //Try creating a user it will throw exception if it already exists
        userDAO.createUser(new UserData(username, password, email));

        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));
        return new RegisterResult(username, authToken, "");
    }

    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        String username = loginRequest.username();
        String password = loginRequest.password();
        if (username == null || password == null) {
            throw new BadRequestException("Error: Bad Request");
        }

        var user = userDAO.getUser(username, password);
        String authToken;
        if (user.password().equals(password)) {
            authToken = generateToken();
            authDAO.createAuth(new AuthData(authToken, username));
        } else {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return new LoginResult(username, authToken);

    }

    public void logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (logoutRequest == null || logoutRequest.authToken() == null) {
            throw new BadRequestException("Error: Bad Request");
        }
        var authToken = logoutRequest.authToken();
        AuthData authData;

        authData = authDAO.getAuth(authToken);


        authDAO.clearAuth(authData);
    }

    public void clear() {
        try {
            userDAO.clear();
            authDAO.clear();
        } catch (RuntimeException e) {
            throw new RuntimeException("Error: Bad Connection", e);
        }
    }
}
