package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.MigrationObject;
import de.oster.easysqlmigration.migration.SQLScriptObject;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.vendors.TypedException;

public class UnkownDataTypeHandler implements Handler
{
    @Override
    public void handle(TypedException e, SQLScriptObject sqlScriptObject, MigrationObject migrationObject, String sqlStatement )
    {
        String message = "";
        message += "unknown datatype in " + sqlScriptObject.getName();
        message += "\r";
        message += e.getErrorMessage();

        throw new SQLMigrationException(message);
    }
}
