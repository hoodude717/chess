package service.serviceResults;

import service.GameDataSerializeable;

import java.util.Collection;
import java.util.Map;

public record ListGameResult(Collection<GameDataSerializeable> games) {
}
