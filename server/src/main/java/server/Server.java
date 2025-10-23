package server;

import com.google.gson.Gson;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.servicerequests.*;
import service.serviceresults.*;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final GameService gameService;
    private final UserService userService;

    private final Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        UserDAO userDAO = new MemoryUserDAO();

        // Register your endpoints and exception handlers here.
        gameService = new GameService(authDAO, gameDAO);
        userService = new UserService(authDAO, userDAO);

        javalin.delete("/db", this::clearDatabase);
        javalin.post("/user", this::registerUser);
        javalin.post("/session", this::loginUser);
        javalin.delete("/session", this::logoutUser);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void printErrorMsg(Context ctx, Exception e, Integer code) {
        String message = serializer.toJson(Map.of("message", e.getMessage()));
        ctx.status(code).result(message).contentType("application/json");
    }

    public void clearDatabase(Context ctx) {
        userService.clear();
        gameService.clear();
        ctx.status(200);
    }

    public void registerUser(Context ctx) {
        var registerRequest = serializer.fromJson(ctx.body(), RegisterRequest.class);
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.status(200).result(serializer.toJson(registerResult)).contentType("application/json");
        } catch (AlreadyTakenException e) {
            printErrorMsg(ctx, e, 403);
        } catch (BadRequestException e) {
            printErrorMsg(ctx, e, 400);
        } catch (DataAccessException e) {
            printErrorMsg(ctx, e, 500);
        }
    }

    public void loginUser(Context ctx) {
        var loginRequest = serializer.fromJson(ctx.body(), LoginRequest.class);
        try {
            LoginResult loginResult = userService.login(loginRequest);
            ctx.json(serializer.toJson(loginResult));
            ctx.status(200);
        } catch (BadRequestException e) {
            printErrorMsg(ctx, e, 400);
        } catch (UnauthorizedException e) {
            printErrorMsg(ctx, e, 401);
        } catch (DataAccessException e) {
            printErrorMsg(ctx, e, 500);
        }

    }

    public void logoutUser(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            LogoutRequest logoutRequest = new LogoutRequest(authToken);
            userService.logout(logoutRequest);
            ctx.status(200);
        } catch (UnauthorizedException e) {
            printErrorMsg(ctx, e, 401);
        } catch (BadRequestException e) {
            printErrorMsg(ctx, e, 400);
        } catch (Exception e) {
            printErrorMsg(ctx, e, 500);
        }
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("Authorization");
            var listGamesRequest = new ListGameRequest(authToken);
            ListGameResult gamesResult = gameService.listGames(listGamesRequest);
            ctx.status(200);
            ctx.json(serializer.toJson(gamesResult));
        } catch (UnauthorizedException e) {
            printErrorMsg(ctx, e, 401);
        } catch (Exception e) {
            printErrorMsg(ctx, e, 500);
        }

    }
    public void createGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        var partialRequest = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        var createGameRequest = new CreateGameRequest(authToken, partialRequest.gameName());
        try {
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            ctx.json(serializer.toJson(createGameResult));
            ctx.status(200);
        } catch (UnauthorizedException e) {
            printErrorMsg(ctx, e, 401);
        } catch (BadRequestException e) {
            printErrorMsg(ctx, e, 400);
        } catch (DataAccessException e) {
            printErrorMsg(ctx, e, 500);
        }
    }

    public void joinGame(Context ctx) {
        String authToken = ctx.header("Authorization");
        var partialRequest = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        var joinGameRequest = new JoinGameRequest(authToken, partialRequest.playerColor(), partialRequest.gameID());
        try {
            JoinGameResult joinGameResult = gameService.joinGame(joinGameRequest);
            ctx.json(serializer.toJson(joinGameResult));
            ctx.status(200);
        } catch (UnauthorizedException e) {
            printErrorMsg(ctx, e, 401);
        } catch (BadRequestException e) {
            printErrorMsg(ctx, e, 400);
        } catch (AlreadyTakenException e) {
            printErrorMsg(ctx, e, 403);
        } catch (DataAccessException e) {
            printErrorMsg(ctx, e, 500);
        }
    }
}
