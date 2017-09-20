package de.oster.easysqlmigration.exception;

import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

public class H2ExceptionTest extends CustomTest
{
    @Test
    public void unknownDataTypeTest()  {

        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);

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

    @Test
    public void sytanxTest()
    {
        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);

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
}
