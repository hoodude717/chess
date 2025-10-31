package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SQLGameDAO implements GameDAO{

    int gameCount = 1;
    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException{
        configureDatabase();
    }


    @Override
    public int getGameID() {
        return gameCount;
    }

    private void storeGameData(int ID, String whiteUser, String blackUser, String gameName, ChessGame game) throws DataAccessException {
        String statement = "INSERT INTO games (gameID, whiteUser, blackUser, gameName, game) VALUES (?, ?, ?, ?, ?)";;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, ID);
                preparedStatement.setString(2, whiteUser);
                preparedStatement.setString(3, blackUser);
                preparedStatement.setString(4, gameName);
                String gameStr = gson.toJson(game);
                preparedStatement.setString(5, gameStr);
                if (preparedStatement.executeUpdate() == 1) {
                    System.out.println("One Auth added to the Auths table");
                    gameCount += 1;
                } else {
                    System.out.println("Error inserting auth into user database");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not add AuthData", e);
        }
    }

    @Override
    public void createGame(GameData g) throws DataAccessException {
        //Look for existing game with game id if it exists then store Data in table
        try {
            GameData existingGame = getGame(g.gameID());
            throw new AlreadyTakenException("Error: Already Taken");
        } catch (DataAccessException e) {
            storeGameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
        }

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        //Look for game with game id
        //hashed key will be value in other column that will need to be varified when logging in
        String sql = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    ChessGame receivedGame = gson.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUser"),
                            rs.getString("blackUser"),
                            rs.getString("gameName"),
                            receivedGame);
                } else {
                    throw new UnauthorizedException("Error: Unauthorized");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Bad Request");
        }
    }

    @Override
    public Collection<GameData> listGames() {
        ArrayList<GameData> allGames = new ArrayList<>();
        String sql = "SELECT * FROM games";
        try (Connection conn = DatabaseManager.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                ChessGame curGame = gson.fromJson(rs.getString("game"), ChessGame.class);
                var curGameData = new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUser"),
                        rs.getString("blackUser"),
                        rs.getString("gameName"),
                        curGame
                        );
                allGames.add(curGameData);
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: Bad Request");
        }
        return allGames;
    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE games";
        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    private final String[] userDBCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL,
              `whiteUser` varchar(256),
              `blackUser` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` longtext NOT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : userDBCreateStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User Database not created properly", e);
        }
    }

}
