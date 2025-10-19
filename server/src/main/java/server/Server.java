package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.router.Endpoint;
import service.ClearService;
import service.GameService;
import service.UserService;

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
        gameService = new GameService();
        userService = new UserService();



        javalin.delete("/db", this::clearDatabase);

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
}
