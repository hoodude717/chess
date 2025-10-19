package service;

import dataaccess.DataAccessException;

public class AlreadyTakenException extends DataAccessException {
    public AlreadyTakenException(String message) {
        super(message);
    }
}
