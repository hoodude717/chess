package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MemoryGameDAO implements GameDAO{
    ArrayList<GameData> allGames = new ArrayList<>();

    @Override
    public void createGame(GameData g) throws DataAccessException {
        for (GameData game : allGames) {
            if (game.equals(g)) {
                throw new DataAccessException("Game already exists");
            }
        }
        allGames.add(g);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : allGames) {
            var curID = game.gameID();
            if (curID == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game does not exist with ID: " + gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return allGames;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        for (GameData game : allGames) {
            var curID = game.gameID();
            if (curID == gameID) {
                game = updatedGame;
            }
        }
        throw new DataAccessException("No Game with ID: " + gameID);
    }

    @Override
    public void clear() {

    }
}
