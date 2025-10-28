package dataaccess;

import model.UserData;
import service.AlreadyTakenException;
import service.UnauthorizedException;

import java.util.ArrayList;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO{
    ArrayList<UserData> allUsers = new ArrayList<>();

    @Override
    public void createUser(UserData u) throws DataAccessException {
        for (UserData user : allUsers) {
            if (user.equals(u)) {
                throw new AlreadyTakenException("Error: Already Taken");
            }
            if (user.username().equals(u.username())) {
                throw new AlreadyTakenException("Error: Already Taken");
            }
        }
        allUsers.add(u);
    }

    @Override
    public UserData getUser(String username, String password) throws DataAccessException {
        for (UserData user : allUsers) {
            var tempName = user.username();
            if (username.equals(tempName)) {
                return user;
            }
        }
        throw new UnauthorizedException("Error: Unauthorized"); // No user with that name
    }

    @Override
    public void clear() {
        allUsers.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(allUsers, that.allUsers);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allUsers);
    }
}
