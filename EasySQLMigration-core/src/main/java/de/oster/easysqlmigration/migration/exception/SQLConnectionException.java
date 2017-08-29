package de.oster.easysqlmigration.migration.exception;

public class SQLConnectionException extends RuntimeException
{
    public SQLConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLConnectionException(String message) {
        super(message);
    }
}
