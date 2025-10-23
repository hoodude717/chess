package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;


public class ClearService {
    private final GameDAO gameDAO;
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

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
