package dataaccess;

import model.AuthData;
import model.UserData;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    private void storeAuthData(String authToken, String username) throws DataAccessException {
        String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";;
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                if (preparedStatement.executeUpdate() == 1) {
                    System.out.println("One Auth added to the Auths table");
                } else {
                    System.out.println("Error inserting auth into user database");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Could not add AuthData", e);
        }
    }

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        //Searches for the authdata if it exists if so then throw already take else add
        String sql = "SELECT authToken, username FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, a.authToken());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    throw new AlreadyTakenException("Error: Already Taken");
                } else {
                    storeAuthData(a.authToken(), a.username());
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Bad Request");
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new AuthData(
                            rs.getString("authToken"),
                            rs.getString("username"));
                } else {
                    throw new UnauthorizedException("Error: Unauthorized");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Bad Request");
        }
    }

    @Override
    public void clearAuth(AuthData a) throws BadRequestException {
        //Throws badrequest if there is no AuthData a in the table
        String sql = "DELETE FROM auths WHERE authToken = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            try(PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, a.authToken());
                int rowsDeleted = stmt.executeUpdate();
                if (rowsDeleted == 0) {
                    throw new BadRequestException("Error: Bad Request");
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new BadRequestException("Error: Bad Request");
        }
    }

    @Override
    public void clear() {
        //Clear tables in databases
        String sql = "TRUNCATE TABLE auths";
        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }



    private final String[] authDBCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(256) NOT NULL,
              `username` varchar(256) NOT NULL,
              PRIMARY KEY (`authToken`),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : authDBCreateStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException("Error: Bad Request", e);
        }
    }


}
