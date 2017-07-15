package de.oster.sqlcommander.migration.exception;

import java.sql.SQLException;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationException extends SQLException
{
    public SQLMigrationException(String reason) {
        super(reason);
    }

    public SQLMigrationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
