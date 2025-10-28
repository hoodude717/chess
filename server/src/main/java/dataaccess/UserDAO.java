package dataaccess;

import model.UserData;

public interface UserDAO {

    //Creates a new User data  and inputs it into the data store aka database
    void createUser(UserData u) throws DataAccessException;
    //Reads in the user data by username
    UserData getUser(String username, String password) throws DataAccessException;
    
    void clear();
}
