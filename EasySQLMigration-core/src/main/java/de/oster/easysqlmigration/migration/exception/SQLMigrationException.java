package de.oster.easysqlmigration.migration.exception;

import de.oster.easysqlmigration.migration.Migration;

import java.sql.SQLException;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationException extends RuntimeException
{
    public SQLMigrationException(String reason) {
        super(reason);
    }

    public SQLMigrationException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLMigrationException(String reason, Migration migration)
    {
        String errorMessage = "";
        errorMessage += "\n";
        errorMessage += "Their occurred an error in " + migration.getName();
        errorMessage += "\n";
        errorMessage += "error message:";
        errorMessage += "\n";
        errorMessage += "\n";
        errorMessage += reason;
        throw new SQLMigrationException(errorMessage);
    }
}
