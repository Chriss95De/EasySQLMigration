package de.oster.easysqlmigration.migration.exception;

import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.exception.errorhandling.ErrorHandler;
import de.oster.easysqlmigration.migration.exception.errorhandling.type.Handler;
import de.oster.easysqlmigration.vendors.TypedException;
import org.springframework.jdbc.support.SQLErrorCodesFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;

public class SQLMigrationException extends RuntimeException
{
    public ErrorHandler errorHandler;

    public SQLMigrationException(String reason)
    {
        super(reason);
        this.setStackTrace(new StackTraceElement[]{});
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

    public SQLMigrationException(TypedException e, Migration migration)
    {
        handleTypedException(e, migration);
    }

    public SQLMigrationException(String reason, Migration migration)
    {
        throw new SQLMigrationException(reason, migration, null);
    }

    private void handleTypedException(TypedException e, Migration migration)
    {
        try
        {
            Constructor<?> ctor = e.getHandler().getConstructor();
            Handler handler = (Handler) ctor.newInstance();
            handler.handle(e, migration);
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
