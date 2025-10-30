package dataaccess;

import model.AuthData;
import service.BadRequestException;

public interface AuthDAO {

    //Creates a new User data  and inputs it into the data store aka database
    void createAuth(AuthData a) throws DataAccessException;
    //Reads in the user data by username
    AuthData getAuth(String authToken) throws DataAccessException;
    //Deletes the UserData
    void clearAuth(AuthData a) throws BadRequestException;

    void clear();
}
