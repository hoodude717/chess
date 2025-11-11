package client;

public class GameplayClient {

    private ServerFacade server;
    private String authToken;

    public GameplayClient(String url) {
        server = new ServerFacade(url);
    }

    public void setAuthToken(String auth) {
        authToken = auth;
    }
}
