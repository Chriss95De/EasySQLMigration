package de.oster.easysqlmigration.migration.exception;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.MigrationObject;
import de.oster.easysqlmigration.migration.SQLScriptObject;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static de.oster.easysqlmigration.migration.exception.errorhandling.type.HandlerUtil.info;

public class SQLMigrationException extends RuntimeException
{
    private String errorSQL;
    public ErrorHandler errorHandler;

    public SQLMigrationException(String reason)
    {
        super(reason);
        this.setStackTrace(new StackTraceElement[]{});
    }

    public SQLMigrationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SQLMigrationException(String reason, Migration migration, SQLScriptObject sqlScriptObject, String specificStatement)
    {
        String errorMessage = "";
        errorMessage += "\n";
        errorMessage += info("SQLMigrationException");
        errorMessage += info("error in " + sqlScriptObject.getFile());
        errorMessage += info("error message: ");
        errorMessage += "\n" + reason;
        errorMessage += "\n";
        errorMessage += "\n";
        //errorMessage += info("statement position in " + sqlScriptObject.getFile() + " is " + sqlScriptPos);
        errorMessage += info("problem statement: ");
        errorMessage += specificStatement;
        errorMessage += "\n";

        errorSQL = specificStatement;

        throw new SQLMigrationException(errorMessage);
    }

    public SQLMigrationException(TypedException e, MigrationObject migration, SQLScriptObject sqlScriptObject, String specificStatement)
    {
        handleTypedException(e, migration, sqlScriptObject, specificStatement);
    }

    private void handleTypedException(TypedException e, MigrationObject migration, SQLScriptObject sqlScriptObject, String specificStatement)
    {
        try
        {
            Constructor<?> ctor = e.getHandler().getConstructor();
            Handler handler = (Handler) ctor.newInstance();
            handler.handle(e, sqlScriptObject, migration, specificStatement);
        }
        catch (NoSuchMethodException e1)
        {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        } catch (InvocationTargetException e1) {
            e1.printStackTrace();
        } catch (InstantiationException e1) {
            e1.printStackTrace();
        }
    }
}
