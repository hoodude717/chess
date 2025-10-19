package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.serviceRequests.LoginRequest;
import service.serviceRequests.LogoutRequest;
import service.serviceRequests.RegisterRequest;
import service.serviceResults.LoginResult;
import service.serviceResults.RegisterResult;

import java.util.UUID;

public class UserService {

    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        //Try creating a user it will throw exception if it already exists
        userDAO.createUser(new UserData(username, password, email));

        String authToken = generateToken();
        authDAO.createAuth(new AuthData(authToken, username));
        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    public void logout(LogoutRequest logoutRequest) {}
}
