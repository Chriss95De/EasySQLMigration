package de.oster.easysqlmigration.migration.exception.errorhandling.type;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.MigrationObject;
import de.oster.easysqlmigration.migration.SQLScriptObject;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import de.oster.easysqlmigration.vendors.TypedException;

import static de.oster.easysqlmigration.migration.exception.errorhandling.type.HandlerUtil.info;

public class SyntaxErrorHandler implements Handler
{
    @Override
    public void handle(TypedException e, SQLScriptObject sqlScriptObject, MigrationObject migrationObject, String sqlStatement)
    {
        String sqlScriptPos = HandlerUtil.pointOutLineAndPos(sqlScriptObject.getSqlScript(), sqlStatement);

        String errorMessage = "";
        errorMessage += "\n";
        errorMessage += info("SyntaxErrorException");
        errorMessage += info("error in " + sqlScriptObject.getFile());
        errorMessage += info("error message: ");
        errorMessage += "\n" + e.getErrorMessage();
        errorMessage += "\n";
        errorMessage += "\n";
        //errorMessage += info("statement position in " + sqlScriptObject.getFile() + " is " + sqlScriptPos);
        errorMessage += info("problem statement: ");
        errorMessage += sqlStatement;
        errorMessage += "\n";

        throw new SQLMigrationException(errorMessage);
    }
}
