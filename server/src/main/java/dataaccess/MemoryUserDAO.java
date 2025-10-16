package dataaccess;

import model.UserData;

public class MemoryUserDAO implements UserDAO{


    @Override
    public void createUser(UserData u) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void updateUser(UserData newUser) {

    }

    @Override
    public void clearUser(UserData u) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}
