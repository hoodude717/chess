package service;

import com.google.gson.Gson;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.serviceRequests.CreateGameRequest;
import service.serviceRequests.JoinGameRequest;
import service.serviceRequests.ListGameRequest;
import service.serviceRequests.RegisterRequest;
import service.serviceResults.CreateGameResult;
import service.serviceResults.ListGameResult;
import service.serviceResults.RegisterResult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();


    GameService gameService = new GameService(authDAO, gameDAO, userDAO);
    UserService userService = new UserService(authDAO, gameDAO, userDAO);



    @Test
    @Order(1)
    void successfulCreateGame() {
        RegisterResult result;
        try {
            result = userService.register(
                    new RegisterRequest("b2", "12345", "b.A"));
        } catch (Exception e) {
            throw new RuntimeException("Failed Register");
        }
        var authToken = result.authToken();

        Assertions.assertDoesNotThrow(() -> gameService.createGame(
                new CreateGameRequest(authToken, "game1")));


    }

    @Test
    @Order(3)
    void successfulListGames() {
        RegisterResult regResult;
        CreateGameResult gameResult;
        try {
            regResult = userService.register(
                    new RegisterRequest("b1", "12345", "b.A"));
            var authToken = regResult.authToken();
            gameResult = gameService.createGame(
                    new CreateGameRequest(authToken, "gameWithBlack"));
            gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameResult.gameID()));
            final ListGameResult[] listResult = new ListGameResult[1];
             Assertions.assertDoesNotThrow(() -> {
                 listResult[0] = gameService.listGames(new ListGameRequest(regResult.authToken()));
             });
            String json = new Gson().toJson(listResult[0]);
            var correctList = """
                    {"games":[{"gameID":1402838791,"whiteUsername":"","blackUsername":"b1","gameName":"gameWithBlack"}]}
                    """;
            Assertions.assertEquals((String)correctList, (String)json);

        } catch (Exception e) {
            throw new RuntimeException("Failed Register or Make Game", e);
        }

    }


    @Test
    @Order(2)
    void successfulJoinGame() {
        RegisterResult regResult;
        CreateGameResult gameResult;
        try {
            regResult = userService.register(
                    new RegisterRequest("b3", "12345", "b.A"));
            var authToken = regResult.authToken();
            gameResult = gameService.createGame(
                    new CreateGameRequest(authToken, "game2"));
        } catch (Exception e) {
            throw new RuntimeException("Failed Register or Make Game");
        }

        Assertions.assertDoesNotThrow(() -> gameService.joinGame(
                new JoinGameRequest(regResult.authToken(),"WHITE", gameResult.gameID())));


    }


    @Test
    void failedListGames() {

    }

    @Test
    void failedCreateGame() {
    }

    @Test
    void failedJoinGame() {
    }
}