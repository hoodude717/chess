package dataaccess;

import model.AuthData;
import model.UserData;
import service.AlreadyTakenException;
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
    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        try {
            getAuth(a.authToken());
            throw new AlreadyTakenException("Error: Already taken"); //Potential issue, if it is throw it after the try block
        } catch (DataAccessException e) {
            //No user with that authToken now create one TODO
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken, username FROM user WHERE authToken = ?";
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
    public void clearAuth(AuthData a) {

    }

    @Override
    public void clear() {
        //Clear tables in databases
        String sql = "DROP TABLE IF EXISTS auth";
        try (Connection conn = DatabaseManager.getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }



    private final String[] authDBCreateStatements = {
            """
            CREATE TABLE IF NOT EXISTS  auth (
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
