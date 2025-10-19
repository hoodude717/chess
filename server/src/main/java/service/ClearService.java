package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class ClearService {
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public ClearService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }
    public void clear() {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

}
