package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.serviceRequests.CreateGameRequest;
import service.serviceRequests.JoinGameRequest;
import service.serviceRequests.ListGameRequest;
import service.serviceResults.CreateGameResult;
import service.serviceResults.JoinGameResult;
import service.serviceResults.ListGameResult;

public class GameService {

    private GameDAO gameDAO;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO, UserDAO userDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGameResult listGames(ListGameRequest listRequest) {
        return null;
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        return null;
    }
    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) {
        return null;
    }

}
