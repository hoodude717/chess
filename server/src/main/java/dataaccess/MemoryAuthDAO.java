package dataaccess;

import model.AuthData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MemoryAuthDAO implements AuthDAO {

    Map<String,AuthData> allAuths = new HashMap<>();

    @Override
    public void createAuth(AuthData a) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void updateAuth(AuthData newAuth) {

    }

    @Override
    public void clearAuth(AuthData a) throws DataAccessException {

    }
    @Override
    public void clear() {

    }
}
