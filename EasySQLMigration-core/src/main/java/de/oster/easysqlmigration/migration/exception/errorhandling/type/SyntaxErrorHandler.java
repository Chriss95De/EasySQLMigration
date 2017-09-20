package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.vendors.TypedException;

public class SyntaxErrorHandler implements Handler
{
    @Override
    public void handle(TypedException e, Migration migration)
    {
        throw new SQLMigrationException("\n syntax error in "+migration.getName()+"\n"+e.getErrorMessage()+"\n");
    }
}
