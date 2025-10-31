package service;

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.jetbrains.annotations.NotNull;
import service.servicerequests.CreateGameRequest;
import service.servicerequests.JoinGameRequest;
import service.servicerequests.ListGameRequest;
import service.serviceresults.CreateGameResult;
import service.serviceresults.JoinGameResult;
import service.serviceresults.ListGameResult;


import java.util.ArrayList;
import java.util.Collection;

public class GameService {

    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGameResult listGames(ListGameRequest listRequest) throws DataAccessException {
        if (listRequest == null || listRequest.authToken() == null) {
            throw new BadRequestException("Error: Bad Request");
        }

        authDAO.getAuth(listRequest.authToken());


        Collection<GameData> games = gameDAO.listGames();
        Collection<GameDataSerializeable> gameMap = getGameMaps(games);
        return new ListGameResult(gameMap);
    }

    @NotNull
    private static Collection<GameDataSerializeable> getGameMaps(Collection<GameData> games) {
        Collection<GameDataSerializeable> gameMap = new ArrayList<>();
        for (GameData game : games) {
            var gameID = game.gameID();
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();
            var gameName = game.gameName();
            var map = new GameDataSerializeable(gameID, whiteUser, blackUser, gameName);
            gameMap.add(map);
        }
        return gameMap;
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException {
        var authToken = createGameRequest.authToken();
        var gameName = createGameRequest.gameName();
        if (authToken == null || gameName == null) {
            throw new BadRequestException("Error: Bad Request");
        }

        authDAO.getAuth(authToken);


        int gameID = gameDAO.getGameID(); // Unique for each game name
        try {
            gameDAO.createGame(new GameData(gameID, null, null, gameName, new ChessGame()));
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: Bad Request");
        }
        return new CreateGameResult(gameID);
    }

    public JoinGameResult joinGame(JoinGameRequest joinGameRequest) throws DataAccessException {
        var authToken = joinGameRequest.authToken();
        var gameID = joinGameRequest.gameID();
        var playerColor = joinGameRequest.playerColor();
        if (authToken == null || gameID <= 0 || playerColor == null) {
            throw new BadRequestException("Error: Bad Request");
        }
        AuthData auth = authDAO.getAuth(authToken);

        var playerUsername = auth.username();
        GameData game;

        try { game = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException("Error: Bad Request"); }

        switch (playerColor) {
            case "WHITE":
                if (game.whiteUsername() != null) {
                    throw new AlreadyTakenException("Error: Already Taken");
                } else {
                    gameDAO.updateGame(gameID,
                            new GameData(gameID, playerUsername, game.blackUsername(), game.gameName(), game.game()));
                }
                break;
            case "BLACK":
                if (game.blackUsername() != null) {
                    throw new AlreadyTakenException("Error: Already Taken");
                } else {
                    gameDAO.updateGame(gameID,
                            new GameData(gameID, game.whiteUsername(), playerUsername, game.gameName(), game.game()));
                }
                break;
            default:
                throw new BadRequestException("Error: Bad Request");
        }

        return new JoinGameResult();
    }

    public void clear() {
        gameDAO.clear();
    }

}
