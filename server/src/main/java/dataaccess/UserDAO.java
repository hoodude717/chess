package dataaccess;

import model.UserData;
import org.eclipse.jetty.server.Authentication;

public interface UserDAO {

    //Creates a new User data  and inputs it into the data store aka database
    void createUser(UserData u) throws DataAccessException;
    //Reads in the user data by username
    UserData getUser(String username) throws DataAccessException;

    //Updates the userdata inside the database
    void updateUser(UserData newUser);

    //Deletes the UserData
    void clearUser(UserData u) throws DataAccessException;

    void clear();
}
