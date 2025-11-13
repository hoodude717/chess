package dataaccess;

import exceptions.DataAccessException;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    ArrayList<GameData> allGames = new ArrayList<>();
    int totalGames = 1;

    @Override
    public int createGame(GameData g) throws DataAccessException {
        for (GameData game : allGames) {
            if (game.equals(g)) {
                throw new DataAccessException("Game already exists");
            }
        }

        allGames.add(g);
        return totalGames++;
    }

    public int getGameID() { return totalGames; }

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
    public Collection<GameData> listGames() {
        return allGames;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        for (int i=0; i<allGames.size(); i++) {
            GameData game = allGames.get(i);
            var curID = game.gameID();
            if (curID == gameID) {
                allGames.set(i, updatedGame);
                return;
            }
        }
        throw new DataAccessException("No Game with ID: " + gameID);
    }

    @Override
    public void clear() {
        allGames.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(allGames, that.allGames);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allGames);
    }
}
