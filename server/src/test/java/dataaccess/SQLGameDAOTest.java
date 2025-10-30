package dataaccess;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLGameDAOTest {
    static GameDAO gameDAO;

    @BeforeAll
    static void init() {
        try {
            DatabaseManager.createDatabase();
            gameDAO = new SQLGameDAO();
        } catch (Exception e) {
            throw new RuntimeException("Failed create SQL DAO", e);
        }
    }

    @Test
    void createGameSuccess() {
        //When a game is created succesfully it will not throw any errors and it will have the Game in the games table

        //Assert doesnt throw
        // Assert game is in table.
    }
    @Test
    void createGameFail() {
        //If it fails it will not have the game in the table and it will throw an exception

        //Assert throws error

        //Assert that there is not a game with the game data in the table
    }

    @Test
    void getGameSuccess() {
        // Assert not thrown an error
        //Assert there returns the a game dat with the right gameID
    }
    @Test
    void getGameFail() {
        //Assert ther eis a error thrown
    }

    @Test
    void listGamesSuccess() {
        // ASsert no error thrown

        //Assert correct list of games in the collection returned

    }
    @Test
    void listGamesFail() {
        //Assert throws an error

        //Assert that not all games listed?
    }

    @Test
    void updateGameSuccess() {
        //Assert no error thrown

        //Assert that game has changed to updated in SQL
    }
    @Test
    void updateGameFail() {
        //Assert throws an error
        //Assert that the table is the same as before after the attempted change

    }

    @Test
    void clear() {
        // Assert that no thrown error
    }
}