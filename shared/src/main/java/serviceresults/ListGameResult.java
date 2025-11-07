package serviceresults;

import model.GameDataSerializeable;

import java.util.Collection;

public record ListGameResult(Collection<GameDataSerializeable> games) {
}
