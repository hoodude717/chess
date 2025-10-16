package dataaccess;

import io.javalin.http.UnauthorizedResponse;
import model.GameData;

import javax.xml.crypto.Data;
import java.util.Collection;

public interface GameDAO {
    //    createGame: Create a new game.
    public void createGame(GameData g) throws DataAccessException;
    //    getGame: Retrieve a specified game with the given game ID.
    public GameData getGame(int gameID) throws DataAccessException;
    //    listGames: Retrieve all games.
    public Collection<GameData> listGames() throws DataAccessException;
    //    updateGame: Updates a chess game. It should replace the chess game string corresponding to a given gameID. This is used when players join a game or when a move is made.
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    //Clears all games
    public void clear();

}
