package servicerequests;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
