package servicerequests;

public record CreateGameRequest(String authToken, String gameName) {
}
