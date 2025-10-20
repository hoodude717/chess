package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.router.Endpoint;
import service.*;
import service.serviceRequests.LoginRequest;
import service.serviceRequests.LogoutRequest;
import service.serviceRequests.RegisterRequest;
import service.serviceResults.LoginResult;
import service.serviceResults.RegisterResult;

public class Server {

    private final Javalin javalin;

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private final UserDAO userDAO;


    private ClearService clearService;
    private GameService gameService;
    private UserService userService;


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


    public void clearDatabase(Context ctx) {
        clearService.clear();
        ctx.status(200);
    }

    public void registerUser(Context ctx) {
        var registerRequest = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        try {
            RegisterResult registerResult = userService.register(registerRequest);
            ctx.json(new Gson().toJson(registerResult));

        } catch (DataAccessException ex) {
            ctx.status(403);
            ctx.json(new Gson().toJson(ex));
        }
    }

    public void loginUser(Context ctx) {
        var loginRequest = new Gson().fromJson(ctx.body(), LoginRequest.class);
        try {
            LoginResult loginResult = userService.login(loginRequest);
            ctx.json(new Gson().toJson(loginResult));
            ctx.status(200);
        } catch (BadRequestException e) {
            ctx.status(400);
            ctx.json(new Gson().toJson(e));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(e));
        } catch (DataAccessException e) {
            ctx.status(403);
            ctx.json(new Gson().toJson(e));
        }

    }

    public void logoutUser(Context ctx) {
        var logoutRequest = new Gson().fromJson(ctx.body(), LogoutRequest.class);
        try {
            userService.logout(logoutRequest);
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.json(new Gson().toJson(e));
        }
    }
    public void listGames(Context ctx) {
    }
    public void createGame(Context ctx) {
    }
    public void joinGame(Context ctx) {
    }
}
