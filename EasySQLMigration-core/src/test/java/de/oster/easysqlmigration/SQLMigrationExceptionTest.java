package de.oster.easysqlmigration;

import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationExceptionTest extends CustomTest
{
    @Test
    public void migrateSQLScriptExceptionTest() {

        EasySQLMigration sqlMigration = EasySQLMigration.createInstance(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql");

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
