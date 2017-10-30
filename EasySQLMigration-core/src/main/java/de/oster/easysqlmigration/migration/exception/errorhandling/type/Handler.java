package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.MigrationObject;
import de.oster.easysqlmigration.migration.SQLScriptObject;
import de.oster.easysqlmigration.vendors.TypedException;

public interface Handler
{
    void handle(TypedException e, SQLScriptObject sqlScriptObject, MigrationObject migrationObject, String sqlStatement);
}
