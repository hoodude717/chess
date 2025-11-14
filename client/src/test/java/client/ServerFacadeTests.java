package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import servicerequests.*;
import serviceresults.LoginResult;
import serviceresults.RegisterResult;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;
    private static LoginResult result;
    private static RegisterResult regResult;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        var serverUrl = "http://localhost:" + port;

        serverFacade = new ServerFacade(serverUrl);

        Assertions.assertDoesNotThrow(()->serverFacade.clear());
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    @Order(1)
    public void registerGood() {
        RegisterRequest request = new RegisterRequest("bradford", "bradford", "bradford");
        regResult = Assertions.assertDoesNotThrow(()->serverFacade.register(request));
    }

    @Test
    @Order(2)
    public void registerBad() {
        RegisterRequest request = new RegisterRequest("bradford", "Bradford", "bradford");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.register(request));
    }

    @Test
    @Order(3)
    public void logoutBad() {
        LogoutRequest request = new LogoutRequest("112345");
        Assertions.assertDoesNotThrow(()->serverFacade.logout(request));
    }

    @Test
    @Order(4)
    public void loginBad() {
        LoginRequest request = new LoginRequest("bradford", "12345");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.login(request));
    }

    @Test
    @Order(5)
    public void loginGood() {
        LoginRequest request = new LoginRequest("bradford", "bradford");
        result = Assertions.assertDoesNotThrow(()->serverFacade.login(request));
    }

    @Test
    @Order(6)
    public void logoutGood() {
        LogoutRequest request = new LogoutRequest(regResult.authToken());
        Assertions.assertDoesNotThrow(()->serverFacade.logout(request));
    }

    @Test
    @Order(7)
    public void listGood() {
        ListGameRequest request = new ListGameRequest(result.authToken());
        Assertions.assertDoesNotThrow(()->serverFacade.listGames(request));
    }

    @Test
    @Order(8)
    public void listBad() {
        ListGameRequest request = new ListGameRequest("112345");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.listGames(request));
    }

    @Test
    @Order(9)
    public void createGood() {
        CreateGameRequest request = new CreateGameRequest(result.authToken(), "newGame");
        Assertions.assertDoesNotThrow(()->serverFacade.createGame(request));

    }

    @Test
    @Order(10)
    public void createBad() {
        CreateGameRequest request = new CreateGameRequest("12345", "newGame");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.createGame(request));
    }

    @Test
    @Order(11)
    public void joinGood() {
        JoinGameRequest request = new JoinGameRequest(result.authToken(), "WHITE", 1);
        Assertions.assertDoesNotThrow(()->serverFacade.joinGame(request));

    }

    @Test
    @Order(12)
    public void joinBad() {
        JoinGameRequest request = new JoinGameRequest("result.authToken()", "WHITE", 1);
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.joinGame(request));
    }

    @Test
    @Order(13)
    public void clearGood() {
        Assertions.assertDoesNotThrow(()->serverFacade.clear());
    }






}
