package dataaccess;

import model.AuthData;

public interface AuthDAO {

    //Creates a new User data  and inputs it into the data store aka database
    void createAuth(AuthData a) throws DataAccessException;
    //Reads in the user data by username
    AuthData getAuth(String username) throws DataAccessException;
    //Updates the userdata inside the database
    void updateAuth(AuthData newAuth);
    //Deletes the UserData
    void clearAuth(AuthData a) throws DataAccessException;

    void clear();
}
