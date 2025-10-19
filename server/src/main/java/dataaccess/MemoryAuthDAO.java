package dataaccess;

import model.AuthData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {
    ArrayList<AuthData> allAuths = new ArrayList<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {
        for (AuthData auth : allAuths) {
            if (auth.equals(a)) {
                throw new DataAccessException("AuthData already exists");
            }
        }
        allAuths.add(a);
    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        for (AuthData auth : allAuths) {
            if (auth.username().equals(username)) {
                return auth;
            }
        }
        throw new DataAccessException("No AuthData with username: " + username);
    }

    @Override
    public void updateAuth(AuthData newAuth) throws DataAccessException {
    }

    @Override
    public void clearAuth(AuthData a) throws DataAccessException {
        for (AuthData auth : allAuths) {
            if (auth.equals(a)) {
                allAuths.remove(auth);
            }
        }
        throw new DataAccessException("AuthData does not exist in database");
    }
    @Override
    public void clear() {
        allAuths.clear();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(allAuths, that.allAuths);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(allAuths);
    }
}
