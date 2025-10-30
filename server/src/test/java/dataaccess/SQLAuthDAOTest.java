package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.AlreadyTakenException;

import javax.xml.crypto.Data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLAuthDAOTest {

    static AuthDAO authDAO;
    static AuthData expectedAuth;

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    @BeforeAll
    static void init() {
        try {
            expectedAuth = new AuthData(generateToken(), "bradford");
            DatabaseManager.clearDatabase();
            DatabaseManager.createDatabase();
            authDAO = new SQLAuthDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create SQL DAO", e);
        }

    }

    @Test
    @Order(1)
    void createAuthSuccess() {
        //When it is a success it will have a new auth in the table with the correct username

        //Assert doesnt throw
        Assertions.assertDoesNotThrow(()-> authDAO.createAuth(expectedAuth));
        //Assert row is in the table with Auth username
        String sql = "SELECT username, authToken FROM auths WHERE username = '" + expectedAuth.username() + "'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Assertions.assertEquals("bradford", rs.getString("username"),
                        String.format("Usernames did not match. Expected: 'bradford' Actual: %s",
                                rs.getString("username")));
                Assertions.assertEquals(expectedAuth.authToken(), rs.getString("authToken"),
                        String.format("AuthTokens did not match. Expected: %s Actual: %s",
                                expectedAuth.authToken(), rs.getString("authToken")));

            } else {
                fail("Auth was not inserted in Table");
            }
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }

    }

    @Test
    @Order(2)
    void createAuthFail() {
        // If it throws an error
        //Assert an error is thrown
        var existingAuth = new AuthData(expectedAuth.authToken(), "bradford");

        Assertions.assertThrows(AlreadyTakenException.class, () -> authDAO.createAuth(existingAuth),
                "Added another auth when one already existed");
        // Assert that the sql doesn't have the data
        String sql = "SELECT username, authToken FROM auths WHERE username = 'bradford'";
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
    void getAuthSuccess() {
        // Assert doesnt throw error
        AuthData result = Assertions.assertDoesNotThrow(()->authDAO.getAuth(expectedAuth.authToken()));
        // Assert that the Auth received has the same authtoken as the one passed in
        Assertions.assertEquals(expectedAuth, result);
    }

    @Test
    @Order(4)
    void getAuthFail() {
        //Assert throws DataAccessException
        Assertions.assertThrows(DataAccessException.class,
                ()->authDAO.getAuth("wrongAuth"));
    }

    @Test
    @Order(5)
    void clearAuthSuccess() {
        //Assert the table does not have the cleared auth data
        Assertions.assertDoesNotThrow(() -> authDAO.clearAuth(expectedAuth));

        String sql = "SELECT username, authToken FROM auths WHERE username = 'bradford'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            Assertions.assertFalse(rs.next());
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }

    }
    @Test
    @Order(6)
    void clearAuthFail() {
        // How to check for failed clear AUth?
        //Assert throws an error if there is no authData when it tries and delete.
        AuthData unknownAuth = new AuthData("123", "unknown");
        Assertions.assertThrows(DataAccessException.class, ()->authDAO.clearAuth(unknownAuth));

    }

    @Test
    @Order(7)
    void clear() {
        //Assert there is nothing in the tables
        Assertions.assertDoesNotThrow(()->authDAO.clear());

    }
}