package dataaccess;

import model.GameData;
import java.util.Collection;

public interface GameDAO {
    int gameID = 1;
    // Get game number for id number
    default int getGameID() {
        return gameID;
    }
    //    createGame: Create a new game.
    void createGame(GameData g) throws DataAccessException;
    //    getGame: Retrieve a specified game with the given game ID.
    GameData getGame(int gameID) throws DataAccessException;
    //    listGames: Retrieve all games.
    Collection<GameData> listGames();
    //    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    //Clears all games
    void clear();

}
