package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.router.Endpoint;
import service.ClearService;
import service.GameService;
import service.UserService;
import service.serviceRequests.RegisterRequest;
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

    }
}
