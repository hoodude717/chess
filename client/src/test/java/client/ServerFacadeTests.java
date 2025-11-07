package client;

import exceptions.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import servicerequests.RegisterRequest;
import serviceresults.RegisterResult;


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
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerGood() {
        RegisterRequest request = new RegisterRequest("bradford", "bradford", "bradford");
        Assertions.assertDoesNotThrow(()->serverFacade.register(request));
    }

    @Test
    public void registerBad() {
        RegisterRequest request = new RegisterRequest("bradford", "Bradford", "bradford");
        Assertions.assertThrows(ResponseException.class, ()->serverFacade.register(request));
    }

    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

}
