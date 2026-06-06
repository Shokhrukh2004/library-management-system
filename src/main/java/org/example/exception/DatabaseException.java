package org.example.exception;

public class DatabaseException extends LibraryException {
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
