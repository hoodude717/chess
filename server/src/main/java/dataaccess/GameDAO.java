package dataaccess;

import exceptions.DataAccessException;
import model.GameData;
import java.util.Collection;

public interface GameDAO {
    // Get game number for id number
    int getGameID();
    //    createGame: Create a new game.
    int createGame(GameData g) throws DataAccessException;
    //    getGame: Retrieve a specified game with the given game ID.
    GameData getGame(int gameID) throws DataAccessException;
    //    listGames: Retrieve all games.
    Collection<GameData> listGames();
    //    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID.
    void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    //Clears all games
    void clear();

}
