package service.serviceRequests;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {
}
