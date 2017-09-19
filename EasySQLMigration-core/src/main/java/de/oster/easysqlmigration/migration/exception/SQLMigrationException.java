package de.oster.easysqlmigration.migration.exception;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import org.springframework.jdbc.support.SQLErrorCodesFactory;

import java.sql.SQLException;

public class SQLMigrationException extends RuntimeException
{
    public ErrorHandler errorHandler;

    public SQLMigrationException(String reason) {
        super(reason);
    }

    public SQLMigrationException(String reason, Throwable cause) {
        super(reason, cause);
    }

    public SQLMigrationException(String reason, Migration migration, Throwable cause)
    {

        SQLException sqlException = (SQLException)cause.getCause();

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

    public SQLMigrationException(String reason, Migration migration)
    {
        throw new SQLMigrationException(reason, migration, null);
    }
}
