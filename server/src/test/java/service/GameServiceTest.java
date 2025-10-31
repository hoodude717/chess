package service;

import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.servicerequests.CreateGameRequest;
import service.servicerequests.JoinGameRequest;
import service.servicerequests.ListGameRequest;
import service.servicerequests.RegisterRequest;
import service.serviceresults.CreateGameResult;
import service.serviceresults.ListGameResult;
import service.serviceresults.RegisterResult;



class GameServiceTest {
    private final GameDAO gameDAO = new MemoryGameDAO();
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();


    GameService gameService = new GameService(authDAO, gameDAO);
    UserService userService = new UserService(authDAO, userDAO);



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
        RegisterResult regResult;
        CreateGameResult gameResult;
        try {
            regResult = userService.register(
                    new RegisterRequest("b4", "12356", "b.A"));
            var authToken = regResult.authToken();
            var wrongAuthToken = "12345";
            gameResult = gameService.createGame(
                    new CreateGameRequest(authToken, "gameWithBlack"));
            gameService.joinGame(new JoinGameRequest(authToken, "BLACK", gameResult.gameID()));
            final ListGameResult[] listResult = new ListGameResult[1];
            Assertions.assertThrows(DataAccessException.class,
                    () -> gameService.listGames(new ListGameRequest(wrongAuthToken)));
        } catch (Exception e) {
            throw new RuntimeException("Failed Register or Make Game", e);
        }

    }

    @Test
    void failedCreateGame() {
        var wrongAuthToken = "1234";
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame(
                new CreateGameRequest(wrongAuthToken, "game1")));
    }

    @Test
    void failedJoinGame() {
        RegisterResult regResult;
        CreateGameResult gameResult;
        try {
            regResult = userService.register(
                    new RegisterRequest("b4", "12345", "b.A"));
            var authToken = regResult.authToken();
            gameResult = gameService.createGame(
                    new CreateGameRequest(authToken, "game2"));
        } catch (Exception e) {
            throw new RuntimeException("Failed Register or Make Game");
        }

        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(
                new JoinGameRequest("wrongToken","WHITE", gameResult.gameID())));

    }
}