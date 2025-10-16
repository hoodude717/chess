package dataaccess;

import model.UserData;

import java.util.ArrayList;

public class MemoryUserDAO implements UserDAO{
    ArrayList<UserData> allUsers = new ArrayList<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        for (UserData user : allUsers) {
            if (user.equals(u)) {
                throw new DataAccessException("User exists with name: " + u.username());
            }
        }
        allUsers.add(u);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : allUsers) {
            var tempName = user.username();
            if (username.equals(tempName)) {
                return user;
            }
        }
        throw new DataAccessException("No User with the username: " + username);
    }

    @Override
    public void updateUser(UserData newUser) {
    }

    @Override
    public void clearUser(UserData u) throws DataAccessException {
        for (UserData user : allUsers) {
            if (user.equals(u)) {
                allUsers.remove(user);
            }
        }
        throw new DataAccessException("No User with the username: " + u.username());

    }

    @Override
    public void clear() {
        allUsers.clear();
    }
}
