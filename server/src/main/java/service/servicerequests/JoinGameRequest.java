package service.servicerequests;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
