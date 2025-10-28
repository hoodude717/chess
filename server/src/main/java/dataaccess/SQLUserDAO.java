package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLUserDAO implements UserDAO{

    public SQLUserDAO() throws DataAccessException{
        configureDatabase();
    }
    
    @Override
    public void createUser(UserData u) throws DataAccessException {
        //change the allusers to a table in the database and search for the row that matches the same values
        // if the table returns anything except an emptytable then its a already taken
        String sql = "SELECT username, password FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, u.username());
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    storeUserPassword(u.username(), u.password());
                } else {
                    throw new AlreadyTakenException("Error: Already Taken");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Bad Request");
        }

    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        //use a sql select cmd to return the user obj with ow that matches username. username will be primary key
        //hashed key will be value in other column that will need to be varified when logging in
//        for (UserData user : allUsers) {
//            var tempName = user.username();
//            if (username.equals(tempName)) {
//                return user;
//            }
//        }
//        throw new UnauthorizedException("Error: Unauthorized"); // No user with that name
        return null;
    }

    @Override
    public void clear() {
        //Clear tables in databases

    }

    private final String[] userDBCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  users (
              `username` string NOT NULL,
              `password` varchar(256) NOT NULL,
              PRIMARY KEY (`username`)
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

    //Function which connects and writes to the database directly with the new hashed password
    private void writeHashedPasswordToDatabase(String username, String hashedPassword) throws DataAccessException {
        String statement = "INSERT INTO user (username, password) VALUES (?, ?)";;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                if (preparedStatement.executeUpdate() == 1) {
                    System.out.println("One User added to the User database");
                } else {
                    System.out.println("Error inserting user into user database");
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not write Password", e);
        }
    }

    //Function to read directly from SQL Database to get hahed
    private String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        String statement = "SELECT password FROM user WHERE username = ?";
        String returnString = "";
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, username);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        returnString = rs.getString("password");
                    }
                    else {
                        throw new DataAccessException("No password found");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not get Password", e);
        }
        return returnString;

    }

    boolean verifyUser(String username, String providedClearTextPassword) {
        // read the previously hashed password from the database
        String hashedPassword;
        try {
            hashedPassword = readHashedPasswordFromDatabase(username);
        } catch (DataAccessException e) {
            hashedPassword = "";
        }

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }

    private void storeUserPassword(String username, String clearTextPassword) throws DataAccessException {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }
}
