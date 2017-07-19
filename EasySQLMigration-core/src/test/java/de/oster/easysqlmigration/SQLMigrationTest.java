package de.oster.easysqlmigration;

import java.io.File;
import java.io.IOException;

import de.oster.easysqlmigration.migration.CustomTest;
import de.oster.easysqlmigration.migration.exception.SQLMigrationException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by Christian on 12.07.2017.
 */
public class SQLMigrationTest extends CustomTest
{
    @Test
    public void migrateSQLScript() throws SQLMigrationException, IOException {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");

        sqlMigration.migrate();
    }

    public void lowerVersionThenAppliedTest() throws SQLMigrationException, IOException {

        EasySQLMigration sqlMigration = new EasySQLMigration(CustomTest.jdbcURL, CustomTest.user, CustomTest.password);
        Assert.assertNotNull(sqlMigration);

        sqlMigration.setSQLScripts("/sql");
        sqlMigration.setMigrationTableName("custom_migration");
        sqlMigration.setPrefixes("sql");
        sqlMigration.setSeparator("_");

        File file = new File("/sql/2_1_test.sql");
        file.delete();

        sqlMigration.migrate();

        file.createNewFile();

        try {
            sqlMigration.migrate();
        }
        catch (Exception exc)
        {
            Assert.assertNotNull(exc);
        }

        file.deleteOnExit();
    }
}
