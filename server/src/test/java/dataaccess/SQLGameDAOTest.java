package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLGameDAOTest {
    static GameDAO gameDAO;
    static GameData expectedGame;
    static String expectedGameStr;
    private final Gson gson = new Gson();
    @BeforeAll
    static void init() {
        try {
            expectedGame = new GameData(1, "player1", "player2", "expectedGame", new ChessGame());
            DatabaseManager.createDatabase();
            DatabaseManager.clearDatabase();
            gameDAO = new SQLGameDAO();
        } catch (Exception e) {
            throw new RuntimeException("Failed create SQL DAO", e);
        }
    }

    @Test
    @Order(1)
    void createGameSuccess() {
        //When a game is created succesfully it will not throw any errors and it will have the Game in the games table
        expectedGameStr = gson.toJson(expectedGame.game());
        //Assert doesnt throw
        Assertions.assertDoesNotThrow(() -> gameDAO.createGame(expectedGame));
        // Assert game is in table.
        String sql = "SELECT * FROM games WHERE gameID = '" + expectedGame.gameID() + "'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Assertions.assertEquals(expectedGame.gameName(), rs.getString("gameName"),
                        String.format("gameNames did not match. Expected: %s Actual: %s",
                                expectedGame.gameName(), rs.getString("gameName")));

                Assertions.assertEquals(expectedGame.whiteUsername(), rs.getString("whiteUser"),
                        String.format("whiteUser did not match. Expected: %s Actual: %s",
                                expectedGame.whiteUsername(), rs.getString("whiteUser")));

                Assertions.assertEquals(expectedGame.blackUsername(), rs.getString("blackUser"),
                        String.format("blackUser did not match. Expected: %s Actual: %s",
                                expectedGame.blackUsername(), rs.getString("blackUser")));

                Assertions.assertEquals(expectedGameStr, rs.getString("game"),
                        String.format("game did not match. Expected: %s Actual: %s",
                                expectedGameStr, rs.getString("game")));

            } else {
                fail("User was not inserted in Table");
            }
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }
    }
    @Test
    @Order(2)
    void createGameFail() {
        //If it fails it will not have the game in the table and it will throw an exception
        GameData badGame = new GameData(1, null, null, "badGame", new ChessGame());
        //Assert throws error
        Assertions.assertThrows(DataAccessException.class, ()->gameDAO.createGame(expectedGame));
        //Assert that there is not a game with the game data in the table
        String sql = "SELECT * FROM games WHERE gameID = '" + expectedGame.gameID() + "'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            int numExisting = 0;
            while(rs.next()) {
                numExisting++;
            }
            Assertions.assertEquals(1, numExisting);
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }
    }

    @Test
    @Order(3)
    void getGameSuccess() {
        //Assert doesnt throw anything
        GameData result = Assertions.assertDoesNotThrow(()->gameDAO.getGame(1));
        //Assert there returns the a game dat with the right gameID
        Assertions.assertEquals(expectedGame, result);
    }

    @Test
    @Order(4)
    void getGameFail() {
        //Assert throws DataAccessException
        Assertions.assertThrows(DataAccessException.class,
                ()->gameDAO.getGame(45));
        Assertions.assertThrows(DataAccessException.class,
                ()->gameDAO.getGame(2));
    }

    @Test
    @Order(5)
    void listGamesSuccess() {
        // Assert no error thrown
        Assertions.assertDoesNotThrow(() ->gameDAO.clear());
        GameData game1 = new GameData(1,"player1","player2","game1", new ChessGame());
        GameData game2 = new GameData(2,"player2","player1","game2", new ChessGame());

        Collection<GameData> expectedList = new ArrayList<>();
        expectedList.add(game1);
        expectedList.add(game2);

        Assertions.assertDoesNotThrow(()->gameDAO.createGame(game1));
        Assertions.assertDoesNotThrow(()->gameDAO.createGame(game2));
        Collection<GameData> testList = Assertions.assertDoesNotThrow(() -> gameDAO.listGames());
        int index = 1;
        for (GameData game : testList) {
            if (index == 1) {
                Assertions.assertEquals(game1, game, "Game 1 Does not match");
            } else if (index == 2) {
                Assertions.assertEquals(game2, game, "Game 2 does not match");
            }
            index++;
        }
        Assertions.assertEquals(expectedList, testList, "Game Lists are not the same");
    }

    @Test
    @Order(6)
    void listGamesFail() {
        //Assert throws an error
        GameData newGame = new GameData(4, null, null, "nullGame", null);
        Assertions.assertDoesNotThrow(()->gameDAO.createGame(newGame));
        Assertions.assertThrows(RuntimeException.class, ()->gameDAO.listGames());

    }

    @Test
    @Order(7)
    void updateGameSuccess() {
        //Assert no error thrown
        Assertions.assertDoesNotThrow(()->gameDAO.clear());
        ChessGame chessGame = new ChessGame();

        Assertions.assertDoesNotThrow(()->chessGame.makeMove(
                new ChessMove(new ChessPosition(2,3), new ChessPosition(3, 3), null)));
        expectedGameStr = gson.toJson(chessGame);

        GameData updatedGame = new GameData(1, "player1", "player2", "expectedGame", chessGame);

        Assertions.assertDoesNotThrow(() -> gameDAO.updateGame(1, updatedGame));

        //Assert that game has changed to updated in SQL
        String sql = "SELECT * FROM games WHERE gameID = '" + expectedGame.gameID() + "'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Assertions.assertEquals(expectedGame.gameName(), rs.getString("gameName"),
                        String.format("gameNames did not match. Expected: %s Actual: %s",
                                expectedGame.gameName(), rs.getString("gameName")));

                Assertions.assertEquals(expectedGame.whiteUsername(), rs.getString("whiteUser"),
                        String.format("whiteUser did not match. Expected: %s Actual: %s",
                                expectedGame.whiteUsername(), rs.getString("whiteUser")));

                Assertions.assertEquals(expectedGame.blackUsername(), rs.getString("blackUser"),
                        String.format("blackUser did not match. Expected: %s Actual: %s",
                                expectedGame.blackUsername(), rs.getString("blackUser")));

                Assertions.assertEquals(expectedGameStr, rs.getString("game"),
                        String.format("game did not match. Expected: %s Actual: %s",
                                expectedGameStr, rs.getString("game")));

            } else {
                fail("User was not updated in Table");
            }
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }

    }

    @Test
    @Order(8)
    void updateGameFail() {
        //Assert throws an error
        GameData newGame = new GameData(5, "bradford", "loly", "updatedGame", new ChessGame());

        Assertions.assertThrows(DataAccessException.class, ()->gameDAO.updateGame(newGame.gameID(), newGame));
        //Assert that the table is the same as before after the attempted change

    }

    @Test
    @Order(9)
    void clear() {
        // Assert that no thrown error
        Assertions.assertDoesNotThrow(()->gameDAO.clear());

    }
}