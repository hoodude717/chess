package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public void clear() {
        gameDAO.clear();
        userDAO.clear();
        authDAO.clear();
    }

}
