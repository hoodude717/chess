package service;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ClearServiceTest {
    private GameDAO gameDAO = new MemoryGameDAO();
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private final ClearService clearService = new ClearService(authDAO, gameDAO, userDAO);

    @Test
    void successfulClearAuthData(){
        //First create a databse to clear
        AuthDAO database = new MemoryAuthDAO();
        assertAll(() -> database.createAuth(new AuthData("12345", "bradford")));
        assertAll(() -> database.createAuth(new AuthData("67890", "loly")));
        //Assert it has been deleted
        assertAll(database::clear);
        var emptyDatabase = new MemoryAuthDAO();
        assertEquals(emptyDatabase, database, "Clear did not clear AuthData");
    }

    @Test
    void successfulClearGameData() {
        //First create a databse to clear
        GameDAO database = new MemoryGameDAO();
        assertAll(() -> database.createGame(
                new GameData(12345, "bradford", "loly", "bradford white", new ChessGame())));
        assertAll(() -> database.createGame(
                new GameData(67890, "loly", "bradford", "loly white", new ChessGame())));
        //Assert it has been deleted
        assertAll(database::clear);
        var emptyDatabase = new MemoryGameDAO();
        assertEquals(emptyDatabase, database, "Clear did not clear GameData");

    }

    @Test
    void successfulClearUserData() {
        //First create a databse to clear
        UserDAO database = new MemoryUserDAO();
        assertAll(() -> database.createUser(new UserData("bradford", "54321", "bradford@byu.edu")));
        assertAll(() -> database.createUser(new UserData("loly", "09876", "loly@byu.edu")));
        //Assert it has been deleted
        assertAll(database::clear);
        var emptyDatabase = new MemoryUserDAO();
        assertEquals(emptyDatabase, database, "Clear did not clear UserData");
    }

    @Test
    void clear() {
        //First create a databse to clear
        GameDAO databaseG = gameDAO;

        assertAll(() -> databaseG.createGame(
                new GameData(12345, "bradford", "loly", "bradford white", new ChessGame())));
        assertAll(() -> databaseG.createGame(
                new GameData(67890, "loly", "bradford", "loly white", new ChessGame())));

        //First create a databse to clear
        AuthDAO databaseA = authDAO;
        assertAll(() -> databaseA.createAuth(new AuthData("12345", "bradford")));
        assertAll(() -> databaseA.createAuth(new AuthData("67890", "loly")));

        //First create a databse to clear
        UserDAO databaseU = userDAO;
        assertAll(() -> databaseU.createUser(new UserData("bradford", "54321", "bradford@byu.edu")));
        assertAll(() -> databaseU.createUser(new UserData("loly", "09876", "loly@byu.edu")));
        //Assert it has been deleted
        assertAll(clearService::clear);

        var emptyDatabaseG = new MemoryGameDAO();
        assertEquals(emptyDatabaseG, databaseG, "Clear did not clear GameData");
        var emptyDatabaseA = new MemoryAuthDAO();
        assertEquals(emptyDatabaseA, databaseA, "Clear did not clear AuthData");
        var emptyDatabase = new MemoryUserDAO();
        assertEquals(emptyDatabase, databaseU, "Clear did not clear UserData");

    }

}
