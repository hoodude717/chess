package service;

public record GameDataSerializeable(
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName) {}