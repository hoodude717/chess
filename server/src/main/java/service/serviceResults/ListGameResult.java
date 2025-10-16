package service.serviceResults;

import model.GameData;

import java.util.Collection;

public record ListGameResult(Collection<GameData> gamesList) {
}
