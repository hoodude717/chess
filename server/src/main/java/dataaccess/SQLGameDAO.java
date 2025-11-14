package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import exceptions.DataAccessException;
import model.GameData;
import exceptions.AlreadyTakenException;
import exceptions.BadRequestException;
import exceptions.UnauthorizedException;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;


public class SQLGameDAO implements GameDAO{

    static int gameCount = 1;
    private final Gson gson = new Gson();

    public SQLGameDAO() throws DataAccessException {
        configureDatabase();
    }


    @Override
    public int getGameID() {
        return gameCount;
    }

    private int storeGameData(String whiteUser, String blackUser, String gameName, ChessGame game) throws DataAccessException {
        String statement = "INSERT INTO games (whiteUser, blackUser, gameName, game) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS)) {
//                preparedStatement.setInt(1, id);
                preparedStatement.setString(1, whiteUser);
                preparedStatement.setString(2, blackUser);
                preparedStatement.setString(3, gameName);
                if (game == null) {
                    throw new DataAccessException("Error: no null game field");
                }
                String gameStr = gson.toJson(game);
                preparedStatement.setString(4, gameStr);
                if (preparedStatement.executeUpdate() == 1) {
                    System.out.println("One Auth added to the Auths table");
                    gameCount += 1;
                    var resultSet = preparedStatement.getGeneratedKeys();
                    resultSet.next();
                    return resultSet.getInt(1);
                } else {
                    System.out.println("Error inserting auth into user database");
                    throw new DataAccessException("Error: Bad Request Did not insert into game table");
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Could not add AuthData", e);
        }
    }

    @Override
    public int createGame(GameData g) throws DataAccessException {
        //Look for existing game with game id if it exists then store Data in table
        return storeGameData(g.whiteUsername(), g.blackUsername(), g.gameName(), g.game());
    }

    public GameData getGame(String gameName) throws DataAccessException {
        //Look for game with game id
        //hashed key will be value in other column that will need to be varified when logging in
        String sql = "SELECT * FROM games WHERE gameName = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, gameName);
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
    public GameData getGame(int gameID) throws DataAccessException {
        //Look for game with game id
        //hashed key will be value in other column that will need to be varified when logging in
        String sql = "SELECT * FROM games WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, gameID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    //create new Chess Game to serialize back into the gameData
                    ChessGame receivedGame = gson.fromJson(rs.getString("game"), ChessGame.class);
                    return new GameData(
                            rs.getInt("gameID"),
                            rs.getString("whiteUser"),
                            rs.getString("blackUser"),
                            rs.getString("gameName"),
                            receivedGame);
                } else {
                    //No new next thing
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
        String sql = """
                UPDATE games
                SET whiteUser = ?, blackUser = ?, gameName = ?, game = ?
                WHERE gameID = ?;
                """;
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            String gameStr = gson.toJson(updatedGame.game());
            stmt.setInt(5, gameID);
            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, updatedGame.gameName());
            stmt.setString(4, gameStr);

            int rowsChanged = stmt.executeUpdate();
            if (rowsChanged != 1) {
                throw new BadRequestException("Error: Bad Request No Games with that ID");
            }
        } catch (SQLException | DataAccessException e) {
            throw new BadRequestException("Error: Bad Request");
        }

    }

    @Override
    public void clear() {
        String sql = "TRUNCATE TABLE games";
        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Error: Failed to clear games table", e);
        }
    }

    private final String[] userDBCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
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
            throw new DataAccessException("Error: User Database not created properly", e);
        }
    }

}
