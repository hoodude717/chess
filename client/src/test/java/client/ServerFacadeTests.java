package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import servicerequests.LoginRequest;
import servicerequests.RegisterRequest;
import serviceresults.RegisterResult;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ServerFacadeTests {

    private static Server server;
    static ServerFacade serverFacade;

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
        Assertions.assertDoesNotThrow(()->serverFacade.register(request));
    }

    @Test
    @Order(2)
    public void registerBad() {
        RegisterRequest request = new RegisterRequest("bradford", "Bradford", "bradford");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.register(request));
    }

    @Test
    @Order(3)
    public void loginBad() {
        LoginRequest request = new LoginRequest("bradford", "12345");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.login(request));
    }

    @Test
    @Order(4)
    public void loginGood() {
        LoginRequest request = new LoginRequest("bradford", "bradford");
        Assertions.assertDoesNotThrow(()->serverFacade.login(request));
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
