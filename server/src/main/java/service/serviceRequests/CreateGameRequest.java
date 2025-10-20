package service.serviceRequests;

public record CreateGameRequest(String authToken, String gameName) {
}
