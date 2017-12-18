package de.oster.easysqlmigration.exception.postgres;

import de.oster.easysqlmigration.exception.ExceptionTest;
import de.oster.easysqlmigration.migration.api.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

public class PostgresExceptionTest extends PSQLConnection implements ExceptionTest
{
    @Override
    @Test
    public void unknownDataTypeTest() {
        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(jdbcURL, user, password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql/unknowndatatype");

        boolean failed = false;
        try
        {
            sqlMigration.migrate();
        }
        catch (SQLMigrationException e)
        {
            e.printStackTrace();
            failed = true;
        }

        Assert.assertTrue(failed);
    }

    @Override
    @Test
    public void sytanxTest() {
        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(jdbcURL, user, password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql/syntax");

        boolean failed = false;
        try
        {
            sqlMigration.migrate();
        }
        catch (SQLMigrationException e)
        {
            e.printStackTrace();
            failed = true;
        }

        Assert.assertTrue(failed);
    }

    @Override
    @Test
    public void handleComments() {
        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(jdbcURL, user, password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql/comments");

        boolean failed = false;
        try
        {
            sqlMigration.migrate();
        }
        catch (SQLMigrationException e)
        {
            e.printStackTrace();
            failed = true;
        }

        Assert.assertTrue(failed);
    }
}
