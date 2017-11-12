package de.oster.easysqlmigration.exception.h2;

import de.oster.easysqlmigration.exception.ExceptionTest;
import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.api.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

public class H2ExceptionTest extends CustomTest implements ExceptionTest
{
    @Test
    public void unknownDataTypeTest()  {

        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql/unknowndatatype/");

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
        sqlMigration.setSQLScripts("/brokensql/syntax/");

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
