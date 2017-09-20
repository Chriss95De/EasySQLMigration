package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.vendors.TypedException;

public class UnkownDataTypeHandler implements Handler
{
    @Override
    public void handle(TypedException e, Migration migration)
    {
        String message = "";
        message += "unknown datatype in " + migration.getName();
        message += "\r";
        message += e.getErrorMessage();




        throw new SQLMigrationException(message);
    }
}
