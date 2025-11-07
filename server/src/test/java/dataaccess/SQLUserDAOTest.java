package dataaccess;

import exceptions.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import exceptions.AlreadyTakenException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SQLUserDAOTest {
    static UserDAO userDao;

    static UserData expectedUser;

    @BeforeAll
    static void init() {
        try {
            expectedUser = new UserData("bradford", "12345", "u@gmail.com");
            DatabaseManager.clearDatabase();
            DatabaseManager.createDatabase();
            userDao = new SQLUserDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to set up SQL DAO ", e);
        }
    }

    @Test
    @Order(1)
    void createUserSuccess() {
        //When a successful createUser happens it will have the user inside the user table with the correct email
        // hashed password and username

        //Assert doesnt throw anything
        Assertions.assertDoesNotThrow(() -> userDao.createUser(expectedUser));
        //Assert that there is a user with same username in the table.
        String sql = "SELECT username, email FROM users WHERE username = '" + expectedUser.username() + "'";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    Assertions.assertEquals("bradford", rs.getString("username"),
                            String.format("Usernames did not match. Expected: 'bradford' Actual: %s",
                                            rs.getString("username")));
                    Assertions.assertEquals("u@gmail.com", rs.getString("email"),
                            String.format("Emails did not match. Expected: 'u@gmail.com' Actual: %s",
                                            rs.getString("email")));

                } else {
                    fail("User was not inserted in Table");
                }
        } catch (SQLException | DataAccessException e) {
            fail("SQL Query did not execute correctly", e);
        }
    }

    @Test
    @Order(2)
    void createUserFail() {
        // When the createUser fails it will throw an error and or will not have the user inside the user table.

        //Assert that it throws an error when adding existing User again
        var existingUser = new UserData("bradford", "12345","u@gmail.com");

        Assertions.assertThrows(AlreadyTakenException.class, () -> userDao.createUser(existingUser),
                "Added another user when one already existed");

        String sql = "SELECT username, email FROM users WHERE username = 'bradford'";
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
        //Assert that Select function returns an empty ResultSet
    }

    @Test
    @Order(3)
    void getUserSuccess() {
        // When a getUser is successful it will return a User with the correct data.
        //Assert doesnt throw anything
        UserData result = Assertions.assertDoesNotThrow(()->userDao.getUser("bradford", "12345"));
        // Assert the returned User is equal to the Expected.
        Assertions.assertEquals(expectedUser, result);
    }

    @Test
    @Order(4)
    void getUserFail() {
        // When the getUser fails it will throw an exception.

        //Assert throws DataAccessException
        Assertions.assertThrows(DataAccessException.class,
                ()->userDao.getUser("wrongUser", "12345"));

        Assertions.assertThrows(DataAccessException.class,
                ()->userDao.getUser("bradford", "wrongPassword"));

    }


    @Test
    @Order(5)
    void clearTest() {
        //Just assert it doesnt throw something
        Assertions.assertDoesNotThrow(()->userDao.clear());
    }
}