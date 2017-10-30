package de.oster.easysqlmigration;

import java.util.List;

import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.Migration;
import de.oster.easysqlmigration.migration.api.EasySQLMigration;
import de.oster.easysqlmigration.migration.exception.SQLConnectionException;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationTest extends CustomTest
{
    @Test
    public void migrateSQLScript() {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");

        sqlMigration.migrate();
    }

    @Test
    public void retrieveMetricInfo() throws SQLMigrationException {
        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");

        sqlMigration.migrate();

        List<Migration> migrations = sqlMigration.retriveMigrationInfo();
        for (Migration migration : migrations)
        {
            System.out.println("Applied migrations: " + migration.getName());
            Assert.assertNotNull(migration.getCreated());
            Assert.assertNotNull(migration.getHash());
            Assert.assertNotNull(migration.getClass());
        }
    }

    @Test
    public void repeatMigrations()
    {
        for(int i=0; i<= 20; i++)
            migrateSQLScript();
    }

    @Test
    public void migrateSQLScriptExceptionTest() {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);

        //folder to sql scripts that are broken
        sqlMigration.setSQLScripts("/brokensql");

        boolean failed = false;
        try
        {
            sqlMigration.migrate();
        }
        catch (SQLMigrationException e)
        {
            System.out.println(e.getMessage());
            failed = true;
        }

        Assert.assertTrue(failed);
    }

    @Test
    public void createJDBCConnectionExceptionTest() {
        boolean failed = false;
        try
        {
            EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user + "definitlywrongusernow", CustomTest.password);
        }
        catch (SQLConnectionException exc)
        {
            System.out.println(exc.getMessage());
            failed = true;
        }
        Assert.assertTrue(failed);
    }
}
