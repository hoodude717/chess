package service.serviceresults;

import service.GameDataSerializeable;

import java.util.Collection;

public record ListGameResult(Collection<GameDataSerializeable> games) {
}
