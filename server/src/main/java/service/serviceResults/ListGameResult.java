package service.serviceResults;

import service.GameDataSerializeable;

import java.util.Collection;

public record ListGameResult(Collection<GameDataSerializeable> games) {
}
