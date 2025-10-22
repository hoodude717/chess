package server;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.router.Endpoint;
import service.*;
import service.serviceRequests.*;
import service.serviceResults.CreateGameResult;
import service.serviceResults.ListGameResult;
import service.serviceResults.LoginResult;
import service.serviceResults.RegisterResult;

import java.util.Map;

public class Server {

    private final Javalin javalin;

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;


    private ClearService clearService;
    private GameService gameService;
    private UserService userService;

    private Gson serializer = new Gson();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userDAO = new MemoryUserDAO();

        // Register your endpoints and exception handlers here.
        clearService = new ClearService(authDAO, gameDAO, userDAO);
        gameService = new GameService(authDAO, gameDAO, userDAO);
        userService = new UserService(authDAO, gameDAO, userDAO);



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
        clearService.clear();
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
        String authToken = ctx.header("Authorization");
        var listGamesRequest = new ListGameRequest(authToken);

        try {
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
        var createGameRequest = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        try {
            CreateGameResult createGameResult = gameService.createGame(createGameRequest);
            ctx.json(serializer.toJson(createGameResult));
            ctx.status(200);
        } catch (UnauthorizedException e) {
            ctx.json(serializer.toJson(e));
            ctx.status(401);
        } catch (DataAccessException e) {
            ctx.status(500);
            ctx.json(serializer.toJson(e));
        }


    }
    public void joinGame(Context ctx) {
    }
}
