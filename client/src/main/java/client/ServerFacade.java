package client;

import com.google.gson.Gson;
import exceptions.ResponseException;
import serviceresults.*;
import servicerequests.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

public class ServerFacade {

    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws ResponseException{
        var request = buildRequest("POST", "/user", registerRequest);
        var result = sendRequest(request);
        return handleResponse(result, RegisterResult.class);
    }

    public LoginResult login(LoginRequest req) throws ResponseException{
        var request = buildRequest("POST", "/session", req);
        var result = sendRequest(request);
        return handleResponse(result, LoginResult.class);
    }

    public void logout(LogoutRequest req) throws ResponseException {
        var request = buildRequest("DELETE", "/session", req);
        sendRequest(request);
    }

    public ListGameResult listGames(ListGameRequest req) throws ResponseException{
        var request = buildRequest("GET", "/game", req);
        var result = sendRequest(request);
        return handleResponse(result, ListGameResult.class);
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException{
        var request = buildRequest("POST", "/game", req);
        var result = sendRequest(request);
        return handleResponse(result, CreateGameResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws ResponseException{
        var request = buildRequest("PUT", "/game", req);
        var result = sendRequest(request);
        return handleResponse(result, JoinGameResult.class);
    }

    public void clear() throws ResponseException{
        var request = buildRequest("DELETE", "/db", null);
        sendRequest(request);
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }

        // Add Authorization header for specific request types
        String authToken = switch (body) {
            case ListGameRequest req -> req.authToken();
            case CreateGameRequest req -> req.authToken();
            case JoinGameRequest req -> req.authToken();
            case LogoutRequest req -> req.authToken();
            // Add more authenticated request types here
            case null, default -> null;
        };

        if (authToken != null) {
            request.setHeader("Authorization", authToken);
        }
        return request.build();
    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ResponseException {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ResponseException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw ResponseException.fromJson(body);
            }
            throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
