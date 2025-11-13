package serviceresults;

import model.GameDataSerializeable;

import java.util.Collection;

public record ListResultSerialize(Collection<GameDataSerializeable> games) {
}
