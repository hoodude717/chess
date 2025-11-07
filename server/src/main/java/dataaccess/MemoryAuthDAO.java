package dataaccess;

import exceptions.DataAccessException;
import model.AuthData;
import exceptions.BadRequestException;

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
    public AuthData getAuth(String authToken) throws DataAccessException {
        for (AuthData auth : allAuths) {
            if (auth.authToken().equals(authToken)) {
                return auth;
            }
        }
        throw new DataAccessException("Error: unauthorized");
    }

    @Override
    public void clearAuth(AuthData a) throws BadRequestException {
        var allAuthCopy = new ArrayList<>(allAuths);
        for (AuthData auth : allAuthCopy) {
            if (auth.equals(a)) {
                allAuths.remove(auth);
            }
        }
        if (allAuthCopy.size() == allAuths.size()) {
            throw new BadRequestException("Error: Bad Request");
        }
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
